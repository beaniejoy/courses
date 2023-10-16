package beaniejoy.io.springbatch.part4.repository;

import beaniejoy.io.springbatch.part4.entity.User;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByUpdatedDate(LocalDate updatedDate);
}
