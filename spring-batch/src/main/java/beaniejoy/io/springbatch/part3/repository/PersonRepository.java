package beaniejoy.io.springbatch.part3.repository;

import beaniejoy.io.springbatch.part3.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {

}
