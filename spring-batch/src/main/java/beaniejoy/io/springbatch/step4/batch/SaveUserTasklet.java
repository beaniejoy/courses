package beaniejoy.io.springbatch.step4.batch;

import beaniejoy.io.springbatch.step4.entity.User;
import beaniejoy.io.springbatch.step4.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class SaveUserTasklet implements Tasklet {

    private final UserRepository userRepository;

    public SaveUserTasklet(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RepeatStatus execute(
        StepContribution contribution,
        ChunkContext chunkContext
    ) throws Exception {
        List<User> users = createUsers();

        Collections.shuffle(users);

        userRepository.saveAll(users);

        return RepeatStatus.FINISHED;
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        users.addAll(makeUsers(0, 100, 1_000));
        users.addAll(makeUsers(100, 200, 200_000));
        users.addAll(makeUsers(200, 300, 300_000));
        users.addAll(makeUsers(300, 400, 500_000));

        return users;
    }

    private List<User> makeUsers(int start, int end, int totalAmount) {
        return IntStream.range(start, end)
            .mapToObj(index -> User.builder()
                .username("test username" + index)
                .totalAmount(totalAmount)
                .build()
            ).collect(Collectors.toList());
    }
}
