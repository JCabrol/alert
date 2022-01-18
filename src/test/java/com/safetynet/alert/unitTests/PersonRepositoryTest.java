package com.safetynet.alert.unitTests;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;


    @Nested
    @DisplayName("FindByFirstNameAndLastName tests:")
    class FindByFirstNameAndLastNameTest {

        @DisplayName("GIVEN an existing person " +
                "WHEN the function findByFirstNameAndLastName is called " +
                "THEN it returns a list containing this person.")
        @Transactional
        @Test
        public void findByFirstNameAndLastNameTest() {
            //GIVEN
            //WHEN
            List<Person> personTest = personRepository.findByFirstNameAndLastName("firstName1", "lastName1");
            //THEN
            assertFalse(personTest.isEmpty());
            assertThat(personTest.size()).isEqualTo(1);
            assertThat(personTest.get(0).getAddress().getStreet()).isEqualTo("addressTest1");
            assertThat(personTest.get(0).getAddress().getZip()).isEqualTo("11111");
            assertThat(personTest.get(0).getAddress().getCity()).isEqualTo("cityTest1");
            assertThat(personTest.get(0).getPhoneNumber()).isEqualTo("1111111111");
            assertThat(personTest.get(0).getMail()).isEqualTo("person1@mail.com");
        }

        @DisplayName("GIVEN a non-existing person " +
                "WHEN the function findByFirstNameAndLastName is called " +
                "THEN it returns an empty list.")
        @Transactional
        @Test
        public void findByFirstNameAndLastNameNotExistingTest() {
            //GIVEN
            //WHEN
            List<Person> personTest = personRepository.findByFirstNameAndLastName("FirstNameTest0", "LastNameTest0");
            //THEN
            assertTrue(personTest.isEmpty());
        }

        @DisplayName("GIVEN a non-existing first name with an existing last name" +
                "WHEN the function findByFirstNameAndLastName is called " +
                "THEN it returns an empty list.")
        @Transactional
        @Test
        public void findByFirstNameAndLastNameOnlyLastNameExistingTest() {
            //GIVEN
            //WHEN
            List<Person> personTest = personRepository.findByFirstNameAndLastName("FirstNameTest0", "LastNameTest1");
            //THEN
            assertTrue(personTest.isEmpty());
        }

        @DisplayName("GIVEN an existing first name with a non-existing last name" +
                "WHEN the function findByFirstNameAndLastName is called " +
                "THEN it returns an empty list.")
        @Transactional
        @Test
        public void findByFirstNameAndLastNameOnlyFirstNameExistingTest() {
            //GIVEN
            //WHEN
            List<Person> personTest = personRepository.findByFirstNameAndLastName("FirstNameTest1", "LastNameTest0");
            //THEN
            assertTrue(personTest.isEmpty());
        }
    }
}
