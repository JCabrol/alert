package com.safetynet.alert.unitTests;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

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

    @Test
    public void findByFirstNameAndLastNameTest() {
        Optional<Person> personTest = personRepository.findByFirstNameAndLastName("FIRSTNAME1", "LASTNAME1");
        assertTrue(personTest.isPresent());
//        assertThat(personTest.get().getAddress()).isEqualTo("1 main street");
//        assertThat(personTest.get().getCity()).isEqualTo("CITY1");
//        assertThat(personTest.get().getZip()).isEqualTo(1111);
        assertThat(personTest.get().getPhoneNumber()).isEqualTo("111-111-1111");
        assertThat(personTest.get().getMail()).isEqualTo("person1@mail.com");
    }

    @Test
    public void findByFirstNameAndLastNameNotExistingTest() {
        Optional<Person> personTest = personRepository.findByFirstNameAndLastName("FIRSTNAME5", "LASTNAME5");
        assertFalse(personTest.isPresent());
    }

    @Test
    public void findByFirstNameAndLastNameOnlyLastNameExistingTest() {
        Optional<Person> personTest = personRepository.findByFirstNameAndLastName("FIRSTNAME5", "LASTNAME1");
        assertFalse(personTest.isPresent());
    }

    @Test
    public void findByFirstNameAndLastNameOnlyFirstNameExistingTest() {
        Optional<Person> personTest = personRepository.findByFirstNameAndLastName("FIRSTNAME1", "LASTNAME5");
        assertFalse(personTest.isPresent());
    }
}
