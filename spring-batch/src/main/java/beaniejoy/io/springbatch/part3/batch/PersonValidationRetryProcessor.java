package beaniejoy.io.springbatch.part3.batch;

import beaniejoy.io.springbatch.part3.entity.Person;
import beaniejoy.io.springbatch.part3.exception.NotFoundNameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

@Slf4j
public class PersonValidationRetryProcessor implements ItemProcessor<Person, Person> {

    private final RetryTemplate retryTemplate;

    public PersonValidationRetryProcessor() {
        this.retryTemplate = new RetryTemplateBuilder()
            .maxAttempts(3)
            .retryOn(NotFoundNameException.class)
            .withListener(new SavePersonRetryListener())
            .build();
    }

    @Override
    public Person process(Person item) throws Exception {
        // RetryCallback에서 오류가 발생해야 retry attempt 횟수에 따라 재시도를 실행할 것이다.
        // (RecoveryCallback은 RetryCallback에서 attempt 횟수만큼 시도했는데 에러 발생시 동작하게 된다.)
        // onError listener에서 감지를 하게 된다.
        return this.retryTemplate.execute(context -> {
            // RetryCallback
            if (item.isNotEmptyName()) {
                return item;
            }

            throw new NotFoundNameException();
        }, context -> {
            // RecoveryCallback
            return item.unknownName();
        });
    }

    public static class SavePersonRetryListener implements RetryListener {

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
            // true > retry가 실행이 된다. false면 retry 시도하지 않는다.
            return true;
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("close");
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
            log.info("onError");
        }
    }

}
