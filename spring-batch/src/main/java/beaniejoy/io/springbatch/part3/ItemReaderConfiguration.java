package beaniejoy.io.springbatch.part3;

import beaniejoy.io.springbatch.part3.batch.CustomItemReader;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Slf4j
public class ItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    public ItemReaderConfiguration(
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
    public Job itemReaderJob() throws Exception {
        return this.jobBuilderFactory.get("itemReaderJob")
            .incrementer(new RunIdIncrementer())
            .start(customItemReaderStep())
            .next(csvFileStep())
            .next(jdbcStep())
            .next(jpaStep())
            .build();
    }

    @Bean
    public Step customItemReaderStep() {
        return this.stepBuilderFactory.get("customItemReaderStep")
            .<Person, Person>chunk(10)
            .reader(new CustomItemReader<>(getItems()))
            .writer(itemWriter())
            .build();
    }

    private List<Person> getItems() {
        return IntStream.range(0, 10)
            .mapToObj(
                index -> new Person(index + 1, "test name" + index, "test age", "test address"))
            .collect(Collectors.toList());
    }

    private ItemWriter<Person> itemWriter() {
        return items -> log.info(
            items.stream()
                .map(Person::getName)
                .collect(Collectors.joining(", "))
        );
    }

    // ####### [START] read csv file #######
    @Bean
    public Step csvFileStep() throws Exception {
        return stepBuilderFactory.get("csvFileStep")
            .<Person, Person>chunk(5)
            .reader(this.csvFileItemReader())
            .writer(itemWriter())
            .build();
    }

    private FlatFileItemReader<Person> csvFileItemReader() throws Exception {
        // csv file 한 줄 씩 read할 수 있는 lineMapper
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "name", "age", "address"); // 주입해주려는 대상 클래스의 필드명
        lineMapper.setLineTokenizer(tokenizer);

        lineMapper.setFieldSetMapper(fieldSet -> {
            int id = fieldSet.readInt("id");
            String name = fieldSet.readString("name");
            String age = fieldSet.readString("age");
            String address = fieldSet.readString("address");

            return new Person(id, name, age, address);
        });

        FlatFileItemReader<Person> itemReader = new FlatFileItemReaderBuilder<Person>()
            .name("csvFileItemReader")
            .encoding("UTF-8")
            .resource(new ClassPathResource("csv/test.csv"))
            .linesToSkip(1)
            .lineMapper(lineMapper)
            .build();
        itemReader.afterPropertiesSet(); // 필수 설정값이 제대로 설정되었는지 검증

        return itemReader;
    }
    // ####### [END] read csv file #######

    // ####### [START] read jdbc(cursor) data #######
    // jdbc cursor 기반의 itemReader (잘 사용 안하는 듯)
    @Bean
    public Step jdbcStep() throws Exception {
        return stepBuilderFactory.get("jdbcStep")
            .<Person, Person>chunk(10)
            .reader(jdbcCursorItemReader())
            .writer(itemWriter())
            .build();
    }

    private JdbcCursorItemReader<Person> jdbcCursorItemReader() throws Exception {
        JdbcCursorItemReader<Person> itemReader = new JdbcCursorItemReaderBuilder<Person>()
            .name("jdbcCursorItemReader")
            .dataSource(dataSource)
            .sql("SELECT id, name, age, address FROM person")
            .rowMapper((rs, rowNum) ->
                new Person(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4)
                )
            )
            .build();
        itemReader.afterPropertiesSet();

        return itemReader;
    }
    // ####### [END] read jdbc(cursor) data #######

    // ####### [START] read jpa(cursor) data #######
    @Bean
    public Step jpaStep() throws Exception {
        return stepBuilderFactory.get("jpaStep")
            .<Person, Person>chunk(10)
            .reader(jpaCursorItemReader())
            .writer(itemWriter())
            .build();
    }

    private JpaCursorItemReader<Person> jpaCursorItemReader() throws Exception {
        JpaCursorItemReader<Person> itemReader = new JpaCursorItemReaderBuilder<Person>()
            .name("jpaCursorItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT p from Person p") // JPQL 문법으로
            .build();

        itemReader.afterPropertiesSet();

        return itemReader;


    }
    // ####### [END] read jpa(cursor) data #######
}
