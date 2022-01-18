package com.safetynet.alert.repository;

import com.safetynet.alert.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, String> {

    List<Person> findByFirstNameAndLastName(String firstName, String lastName);

}
