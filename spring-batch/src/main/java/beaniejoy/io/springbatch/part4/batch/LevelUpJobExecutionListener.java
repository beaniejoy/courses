package beaniejoy.io.springbatch.part4.batch;

import beaniejoy.io.springbatch.part4.entity.User;
import beaniejoy.io.springbatch.part4.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class LevelUpJobExecutionListener implements JobExecutionListener {

    private final UserRepository userRepository;

    public LevelUpJobExecutionListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        List<User> users = userRepository.findAllByUpdatedDate(LocalDate.now());

        long time = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();

        log.info("회원 등급 업데이트 배치 프로그램");
        log.info("------------------------");
        log.info("총 데이터 처리 {}건, 처리시간 {}millis", users.size(), time);
    }
}
