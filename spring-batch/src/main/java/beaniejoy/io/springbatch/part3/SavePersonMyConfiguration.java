package beaniejoy.io.springbatch.part3;

import beaniejoy.io.springbatch.part3.entity.Person;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
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
public class SavePersonMyConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final Set<String> personNameSet = new HashSet<>();

    public SavePersonMyConfiguration(
        JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory,
        EntityManagerFactory entityManagerFactory
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job savePersonMyJob() throws Exception {
        return jobBuilderFactory.get("savePersonMyJob")
            .incrementer(new RunIdIncrementer())
            .start(savePersonMyStep(null))
            .build();
    }

    @Bean
    @JobScope
    public Step savePersonMyStep(
        @Value("#{jobParameters[allow_duplicate]}") Boolean allowDuplicate
    ) throws Exception {
        return stepBuilderFactory.get("savePersonMyStep")
            .<Person, Person>chunk(10)
            .reader(csvFileItemReader())
            .processor(duplicateCheckitemProcessor(allowDuplicate))
            .writer(compositeItemWriter())
            .build();
    }

    private FlatFileItemReader<Person> csvFileItemReader() throws Exception {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("name", "age", "address");

        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> {
            String name = fieldSet.readString("name");
            String age = fieldSet.readString("age");
            String address = fieldSet.readString("address");

            return new Person(name, age, address);
        });

        FlatFileItemReader<Person> itemReader = new FlatFileItemReaderBuilder<Person>()
            .name("csvFileItemReader")
            .encoding("UTF-8")
            .resource(new ClassPathResource("csv/person.csv"))
            .linesToSkip(1)
            .lineMapper(lineMapper)
            .build();

        itemReader.afterPropertiesSet(); // 필수 설정값이 제대로 설정되었는지 검증

        return itemReader;
    }

    private ItemProcessor<Person, Person> duplicateCheckitemProcessor(
        Boolean allowDuplicate
    ) {
        return item -> {
            if ((allowDuplicate == null || !allowDuplicate) &&
                personNameSet.contains(item.getName())
            ) {
                return null;
            }

            personNameSet.add(item.getName());
            return item;
        };
    }

    private ItemWriter<Person> compositeItemWriter() throws Exception {
        CompositeItemWriter<Person> itemWriter = new CompositeItemWriterBuilder<Person>()
            .delegates(jpaItemWriter(), logItemWriter())
            .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    private ItemWriter<Person> jpaItemWriter() throws Exception {
        JpaItemWriter<Person> itemWriter = new JpaItemWriterBuilder<Person>()
            .entityManagerFactory(entityManagerFactory)
            .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    private ItemWriter<Person> logItemWriter() {
        return items -> log.info("saved item size : {}", items.size());
    }
}
