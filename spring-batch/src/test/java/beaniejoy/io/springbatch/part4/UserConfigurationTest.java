package beaniejoy.io.springbatch.part4;

import static org.assertj.core.api.Assertions.assertThat;

import beaniejoy.io.springbatch.TestConfiguration;
import beaniejoy.io.springbatch.part4.entity.User;
import beaniejoy.io.springbatch.part4.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@SpringBatchTest
@ContextConfiguration(classes = {UserConfiguration.class, TestConfiguration.class})
public class UserConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // updatedDate로 Level 상향 건수를 판단하는 것은 부정확하긴 하다.
        List<User> users = userRepository.findAllByUpdatedDate(LocalDate.now());
        int size = users.size();

        assertThat(
            jobExecution.getStepExecutions().stream()
                .filter(x -> x.getStepName().equals("userLevelUpStep"))
                .mapToInt(StepExecution::getWriteCount)
                .sum()
        )
            .isEqualTo(size)
            .isEqualTo(300);

        assertThat(userRepository.count()).isEqualTo(400);
    }

}
