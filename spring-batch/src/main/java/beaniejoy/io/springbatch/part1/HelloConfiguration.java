package beaniejoy.io.springbatch.part1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class HelloConfiguration {

    // spring-batch processing에 의해 자동을 bean 생성
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public HelloConfiguration(
        JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get("helloJob")
            .incrementer(new RunIdIncrementer()) // 매번 다른 run.id로 실행 > 매번 새로운 JobInstance 실행
            .start(this.helloStep())
            .build();
    }

    // Step: Job의 실행 단위
    // Job : Step = 1 : N 관계
    @Bean
    public Step helloStep() {
        return stepBuilderFactory.get("helloStep")
            .tasklet((contribution, chunkContext) -> {
                log.info("hello spring batch");
                return RepeatStatus.FINISHED;
            }).build();
    }
}
