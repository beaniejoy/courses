package beaniejoy.io.springbatch.part3;

import beaniejoy.io.springbatch.part3.SavePersonListener.SavePersonAnnotationJobExecutionListener;
import beaniejoy.io.springbatch.part3.entity.Person;
import javax.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Slf4j
public class SavePersonConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public SavePersonConfiguration(
        JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory,
        EntityManagerFactory entityManagerFactory
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job savePersonJob() throws Exception {
        return jobBuilderFactory.get("savePersonJob")
            .incrementer(new RunIdIncrementer())
            .start(savePersonStep(null))
            .listener(new SavePersonListener.SavePersonJobExecutionListener())
            .listener(new SavePersonListener.SavePersonAnnotationJobExecutionListener())
            .build();
    }

    @Bean
    @JobScope
    public Step savePersonStep(@Value("#{jobParameters[allow_duplicate]}") String allowDuplicate) throws Exception {
        return stepBuilderFactory.get("savePersonStep")
            .<Person, Person>chunk(10)
            .reader(itemReader())
            .processor(
                new DuplicateValidationProcessor<>(
                    Person::getName,
                    Boolean.parseBoolean(allowDuplicate)
                )
            )
            .writer(itemWriter())
            .listener(new SavePersonListener.SavePersonStepExecutionListener())
//            .listener(new SavePersonListener.ItemReaderListener())
            .listener(new SavePersonListener.ChunkListener())
            .build();
    }

    private ItemReader<? extends Person> itemReader() throws Exception {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("name", "age", "address");

        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> new Person(
            fieldSet.readString(0),
            fieldSet.readString(1),
            fieldSet.readString(2)
        ));

        FlatFileItemReader<Person> itemReader = new FlatFileItemReaderBuilder<Person>()
            .name("savePersonItemReader")
            .encoding("UTF-8")
            .linesToSkip(1)
            .resource(new ClassPathResource("csv/person.csv"))
            .lineMapper(lineMapper)
            .build();

        itemReader.afterPropertiesSet();

        return itemReader;
    }

    private ItemWriter<? super Person> itemWriter() throws Exception {
//        return items -> items.forEach(item -> log.info("저는 {} 입니다.", item.getName()));
        JpaItemWriter<Person> jpaItemWriter = new JpaItemWriterBuilder<Person>()
            .entityManagerFactory(entityManagerFactory)
            .build();

        ItemWriter<Person> logItemWriter = items -> log.info("person size: {}", items.size());

        CompositeItemWriter<Person> itemWriter = new CompositeItemWriterBuilder<Person>()
            .delegates(jpaItemWriter, logItemWriter)
            .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
}
