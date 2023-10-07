package beaniejoy.io.springbatch.part3.listener;

import beaniejoy.io.springbatch.part3.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class SavePersonListener {
    // JobExecutionListener interface 구현을 통한 listener 적용
    public static class SavePersonJobExecutionListener implements JobExecutionListener {

        @Override
        public void beforeJob(JobExecution jobExecution) {
            log.info("beforeJob");
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream()
                .mapToInt(StepExecution::getWriteCount)
                .sum();

            log.info("afterJob : {}", sum);
        }
    }

    public static class SavePersonAnnotationJobExecutionListener {
        @BeforeJob
        public void beforeJob(JobExecution jobExecution) {
            log.info("annotation beforeJob");
        }

        @AfterJob
        public void afterJob(JobExecution jobExecution) {
            int sum = jobExecution.getStepExecutions().stream()
                .mapToInt(StepExecution::getWriteCount)
                .sum();

            log.info("annotation afterJob : {}", sum);
        }
    }

    public static class SavePersonStepExecutionListener {

        @BeforeStep
        public void beforeStep(StepExecution stepExecution) {
            log.info("annotation beforeStep");
        }

        @AfterStep
        public ExitStatus afterStep(StepExecution stepExecution) {
            log.info("annotation afterStep : {}", stepExecution.getWriteCount());
//            if (stepExecution.getWriteCount() == 0) {
//                return ExitStatus.FAILED;
//            }

            return stepExecution.getExitStatus();
        }
    }

    public static class ItemReaderListener {
        @BeforeRead
        public void beforeRead() {
            log.info("beforeRead");
        }

        @AfterRead
        public void afterRead(Person item) {
            log.info("afterRead {}", item.getName());
        }

        @OnReadError
        public void onReadError(Exception ex) {
            log.error("onReadError {}", ex.getMessage());
        }
    }

    public static class ChunkListener {
        @BeforeChunk
        public void beforeChunk(ChunkContext context) {
            log.info("beforeChunk");
        }

        @AfterChunk
        public void afterChunk(ChunkContext context) {
            log.info("afterChunk");
        }

        @AfterChunkError
        public void afterChunkError(ChunkContext context) {
            log.error("afterChunkError");
        }
    }
}
