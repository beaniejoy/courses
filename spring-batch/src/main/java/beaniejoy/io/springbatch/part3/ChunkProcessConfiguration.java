package beaniejoy.io.springbatch.part3;

import io.micrometer.core.instrument.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ChunkProcessConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public ChunkProcessConfiguration(
        JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job chunkProcessingJob() {
        return jobBuilderFactory.get("chunkProcessingJob")
            .incrementer(new RunIdIncrementer())
            .start(this.taskBaseStep())
            .next(this.chunkBaseStep(null))
            .build();
    }

    @Bean
    @JobScope
    public Step chunkBaseStep(@Value("#{jobParameters[chunkSize]}") String value) {
        int chunkSize = StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : 10;
        return stepBuilderFactory.get("chunkBaseStep")
            .<String, String>chunk(chunkSize) // generic 으로 설정(<INPUT, OUTPUT>)
            .reader(itemReader()) // return INPUT
            .processor(itemProcessor()) // INPUT > OUTPUT
            .writer(itemWriter()) // chunk size 단위로 list 받아서 처리
            .build();
    }

    // 여기에서 들어오는 items의 크기는 chunkSize(10)이다.
    private ItemWriter<String> itemWriter() {
        return items -> log.info("chunk item size : {}", items.size());
    }

    // 만약 processor 단계에서 null return 되면 이 다음 writer 단계로 넘어가지 않는다.
    private ItemProcessor<String, String> itemProcessor() {
        return item -> item + ", Spring Batch";
    }

    private ItemReader<String> itemReader() {
        return new ListItemReader<>(getItems());
    }

    @Bean
    public Step taskBaseStep() {
        return stepBuilderFactory.get("taskBaseStep")
            .tasklet(this.tasklet())
            .build();
    }

    // tasklet으로 chunk 단위 실행하는 방법
    public Tasklet tasklet() {
        List<String> items = getItems();
        return (contribution, chunkContext) -> {
            StepExecution stepExecution = contribution.getStepExecution();
            JobParameters jobParameters = stepExecution.getJobParameters();

            // tasklet에서 사용할 수 있는 방법
            // chunkSize를 유연하게 parameter로 받아서 처리
            String value = jobParameters.getString("chunkSize", "10");
            int chunkSize = StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : 10;

            // readCount를 통해 index를 지정
            int fromIndex = stepExecution.getReadCount();
            int toIndex = fromIndex + chunkSize;

            if (fromIndex >= items.size()) {
                return RepeatStatus.FINISHED;
            }

            List<String> subList = items.subList(fromIndex, toIndex);

            log.info("task item size : {}", subList.size());

            stepExecution.setReadCount(toIndex);

            return RepeatStatus.CONTINUABLE;
        };
    }

    private List<String> getItems() {
        return IntStream.range(0, 100)
            .mapToObj(index -> index + " Hello")
            .collect(Collectors.toList());
    }
}
