package beaniejoy.io.springbatch.part3;

import beaniejoy.io.springbatch.part3.entity.Person;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@Slf4j
public class ItemWriterConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    public ItemWriterConfiguration(
        JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory,
        DataSource dataSource,
        EntityManagerFactory entityManagerFactory
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job itemWriterJob() throws Exception {
        return jobBuilderFactory.get("itemWriterJob")
            .incrementer(new RunIdIncrementer())
            .start(csvItemWriterStep())
//            .next(jdbcBatchItemWriterStep())
            .next(jpaItemWriterStep())
            .build();
    }

    private ItemReader<Person> itemReader() {
        return new CustomItemReader<>(getItems());
    }

    private List<Person> getItems() {
        return IntStream.range(0, 100)
            .mapToObj(
                index -> new Person("test name" + index, "test age", "test address"))
            .collect(Collectors.toList());
    }

    // ####### [START] write csv file #######
    @Bean
    public Step csvItemWriterStep() throws Exception {
        return stepBuilderFactory.get("csvItemWriterStep")
            .<Person, Person>chunk(10)
            .reader(itemReader())
            .writer(csvFileItemWriter())
            .build();
    }

    private ItemWriter<Person> csvFileItemWriter() throws Exception {
        BeanWrapperFieldExtractor<Person> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "name", "age", "address"});

        DelimitedLineAggregator<Person> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(","); // csv file 형식으로 write
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<Person> itemWriter = new FlatFileItemWriterBuilder<Person>()
            .name("csvFileItemWriter")
            .encoding("UTF-8")
            .resource(new FileSystemResource("csv/output/test-output.csv"))
            .lineAggregator(lineAggregator)
            .headerCallback(writer -> writer.write("id,이름,나이,거주지"))
            .footerCallback(writer -> writer.write("---------------\n"))
            .append(true) // 기존 file 존재 하는 경우 아래에 append
            .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
    // ####### [END] write csv file #######

    // ####### [START] write jdbc batch #######
    @Bean
    public Step jdbcBatchItemWriterStep() throws Exception {
        return stepBuilderFactory.get("jdbcBatchItemWriterStep")
            .<Person, Person>chunk(10)
            .reader(itemReader())
            .writer(jdbcBatchItemWriter())
            .build();
    }

    private ItemWriter<Person> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<Person> itemWriter = new JdbcBatchItemWriterBuilder<Person>()
            .dataSource(dataSource)
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("insert into person(name, age, address) values(:name, :age, :address)")
            .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
    // ####### [END] write jdbc batch #######

    // ####### [START] write jpa batch #######
    @Bean
    public Step jpaItemWriterStep() throws Exception {
        return stepBuilderFactory.get("jpaItemWriterStep")
            .<Person, Person>chunk(10)
            .reader(itemReader())
            .writer(jpaItemWriter())
            .build();
    }

    private ItemWriter<Person> jpaItemWriter() throws Exception {
        JpaItemWriter<Person> itemWriter = new JpaItemWriterBuilder<Person>()
            .entityManagerFactory(entityManagerFactory)
            // 기본 설정으로 jpa 영속화 실행시 merge로 실행되기 때문
            // insert 전에 불필요한 select 쿼리가 실행된다.
            // jpa에서 Person id에 값을 할당하고 실행시 merge로 실행된다.
            .usePersist(true) // persist로 실행하기(insert 쿼리만 실행됨)
            .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
    // ####### [END] write jpa batch #######
}
