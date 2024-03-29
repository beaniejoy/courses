package beaniejoy.io.springbatch.part4.batch;

import beaniejoy.io.springbatch.part4.entity.User;
import beaniejoy.io.springbatch.part4.repository.UserRepository;
import beaniejoy.io.springbatch.part5.entity.Order;
import java.time.LocalDate;
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
        users.addAll(makeUsers(0, 100, 1_000, 1));
        users.addAll(makeUsers(100, 200, 200_000, 2));
        users.addAll(makeUsers(200, 300, 300_000, 3));
        users.addAll(makeUsers(300, 400, 500_000, 4));

        return users;
    }

    private List<User> makeUsers(int start, int end, int totalAmount, int day) {
        return IntStream.range(start, end)
            .mapToObj(index -> {
                    User user = User.builder()
                        .username("test username" + index)
                        .build();

                    Order order = Order.builder()
                        .amount(totalAmount)
                        .createdDate(LocalDate.of(2023, 10, day))
                        .itemName("item" + index)
                        .build();

                    user.addOrder(order);

                    return user;
                }
            ).collect(Collectors.toList());
    }
}
