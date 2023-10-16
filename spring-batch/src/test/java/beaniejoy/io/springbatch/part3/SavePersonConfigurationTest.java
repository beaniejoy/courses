package beaniejoy.io.springbatch.part3;

import static org.assertj.core.api.Assertions.assertThat;

import beaniejoy.io.springbatch.TestConfiguration;
import beaniejoy.io.springbatch.part3.SavePersonConfiguration;
import beaniejoy.io.springbatch.part3.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@SpringBatchTest
@ContextConfiguration(classes = {SavePersonConfiguration.class, TestConfiguration.class})
public class SavePersonConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PersonRepository personRepository;

    // ParameterizedTest로도 각각 적용
    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource(value = {"true:99", "false:11"}, delimiter = ':')
    public void test_allow_duplicate(String allowDuplicate, int expectedCount) throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("allow_duplicate", allowDuplicate)
            .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStepExecutions().stream()
            .mapToInt(StepExecution::getWriteCount)
            .sum()
        )
            .isEqualTo(personRepository.count())
            .isEqualTo(expectedCount);
    }

    @Test
    public void test_step() {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("savePersonStep");

        assertThat(jobExecution.getStepExecutions().stream()
            .mapToInt(StepExecution::getWriteCount)
            .sum()
        )
            .isEqualTo(personRepository.count())
            .isEqualTo(11);
    }
}
