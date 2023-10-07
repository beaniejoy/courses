package beaniejoy.io.springbatch.part3;

import beaniejoy.io.springbatch.part3.batch.CustomItemReader;
import beaniejoy.io.springbatch.part3.entity.Person;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ItemProcessorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public ItemProcessorConfiguration(
        JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job itemProcessorJob() {
        return jobBuilderFactory.get("itemProcessorJob")
            .incrementer(new RunIdIncrementer())
            .start(itemProcessorStep())
            .build();
    }

    @Bean
    public Step itemProcessorStep() {
        return stepBuilderFactory.get("itemProcessorStep")
            .<Person, Person>chunk(10)
            .reader(itemReader())
            .processor(itemProcessor())
            .writer(itemWriter())
            .build();
    }

    private ItemWriter<Person> itemWriter() {
        return items -> {
            log.info("item size: {}", items.size());
            items.forEach(item -> log.info("Person.ID : {}", item.getId()));
        };
    }

    private ItemReader<Person> itemReader() {
        return new CustomItemReader<>(getItems());
    }

    private List<Person> getItems() {
        return IntStream.range(0, 100)
            .mapToObj(
                index -> new Person(
                    index + 1,
                    "test name" + index,
                    "test age",
                    "test address")
            )
            .collect(Collectors.toList());
    }

    /**
     * chunkSize 10 > reader에서 하나의 덩어리로 10개를 읽어서 processor에서 filtering(짝수만)
     * writer에서 5씩만 처리
     * 즉, chunkSize 기준으로 filtering된 item들에 대해서만 처리되는 것으로 이해하면 된다.(10개 중 5개만 처리)
     */
    private ItemProcessor<Person, Person> itemProcessor() {
        return item -> {
            if (item.getId() % 2 == 0) {
                return item;
            }

            // writer는 processor에서의 null은 filtering 하고 나머지를 처리한다.
            return null;
        };
    }
}
