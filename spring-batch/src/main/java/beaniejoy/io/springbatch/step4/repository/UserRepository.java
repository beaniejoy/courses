package beaniejoy.io.springbatch.step4.repository;

import beaniejoy.io.springbatch.step4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
