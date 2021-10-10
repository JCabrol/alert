package com.safetynet.alert.unitTests;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import com.safetynet.alert.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Tag("PersonTests")
@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest(classes = PersonService.class)
public class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @MockBean
    private PersonRepository personRepository;

    @Nested
    @DisplayName("getPersons() tests:")
    class getPersonsTest {

        @DisplayName("GIVEN persons returned by personRepository " +
                "WHEN function getPersons() is called " +
                "THEN it returns the same persons.")
        @Test
        public void getPersonsWhenNonEmptyTest() {
            //GIVEN
            //a list containing 3 persons has to be returned when the personRepository mock is called with the function findAll
            ArrayList<Person> AllPersonsTest = new ArrayList<>();
            for (int numberOfPersonsTest = 0; numberOfPersonsTest < 3; numberOfPersonsTest++) {
                Person person = new Person(numberOfPersonsTest, "FIRSTNAME" + numberOfPersonsTest, "LASTNAME" + numberOfPersonsTest, numberOfPersonsTest + " main street", "CITY" + numberOfPersonsTest, 1000 * numberOfPersonsTest + 100 * numberOfPersonsTest + 10 * numberOfPersonsTest + numberOfPersonsTest, "" + numberOfPersonsTest + numberOfPersonsTest + numberOfPersonsTest + "-" + numberOfPersonsTest + numberOfPersonsTest + numberOfPersonsTest + "-" + numberOfPersonsTest + numberOfPersonsTest + numberOfPersonsTest + numberOfPersonsTest, "person" + numberOfPersonsTest + "@mail.com");
                AllPersonsTest.add(person);
            }
            when(personRepository.findAll()).thenReturn(AllPersonsTest);
            //WHEN
            //the tested function getPersons is called
            Iterable<Person> result = personService.getPersons();
            //THEN
            //the result should contain 3 persons and should be the same as the list created first
            assertThat(result.spliterator().getExactSizeIfKnown()).isEqualTo(3);
            assertThat(result).isEqualTo(AllPersonsTest);
        }

        @DisplayName("GIVEN no person returned by personRepository " +
                "WHEN function getPersons() is called " +
                "THEN it returns null.")
        @Test
        public void getPersonsWhenEmptyTest() {
            //GIVEN
            //nothing has to be returned when the personRepository mock is called with the function findAll
            when(personRepository.findAll()).thenReturn(null);
            //WHEN
            //he tested function getPersons is called
            Iterable<Person> result = personService.getPersons();
            //THEN
            //the result should be null
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getPersonById() tests:")
    class GetPersonByIdTest {

        @Test
        @DisplayName("GIVEN an existing person " +
                "WHEN the function getPersonById() is called " +
                "THEN the person should be found.")
        void getPersonByIdTest() {
            // GIVEN
            //a person with ID = 0 has to be returned when the personRepository mock is called with the function findById
            Person person = new Person(0, "FirstNameTest", "LastNameTest", "address test",
                    "cityTest", 1111, "phoneNumberTest", "mailTest");
            doReturn(Optional.of(person)).when(personRepository).findById(0);

            // WHEN
            //the tested function  getPersonById is called with parameter id = 0
            Optional<Person> returnedPerson = personService.getPersonById(0);

            // THEN
            //the person should be found
            assertThat(returnedPerson).isPresent();
            assertThat(returnedPerson.get()).isEqualTo(person);
        }

        @Test
        @DisplayName("GIVEN a non existing person " +
                "WHEN the function getPersonById() is called " +
                "THEN the person shouldn't be found.")
        void getPersonByIdNotExistingTest() {
            // GIVEN
            //an empty result has to be returned when the personRepository mock is called with the function findById
            doReturn(Optional.empty()).when(personRepository).findById(1);
            // WHEN
            //the tested function getPersonById is called with parameter id = 1
            Optional<Person> returnedPerson = personService.getPersonById(1);
            // THEN
            //nothing should be returned
            Assertions.assertFalse(returnedPerson.isPresent());
        }
    }

    @Nested
    @DisplayName("getPersonByName() tests:")
    class GetPersonByNameTest {

        @Test
        @DisplayName("GIVEN an existing person " +
                "WHEN the function getPersonByName() is called " +
                "THEN the person should be found.")
        void getPersonByIdTest() {
            // GIVEN
            //a person has to be returned when the personRepository mock is called with the function findByFirstNameAndLastName
            Person person = new Person(0, "FirstNameTest", "LastNameTest", "address test",
                    "cityTest", 1111, "phoneNumberTest", "mailTest");
            doReturn(Optional.of(person)).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST", "LASTNAMETEST");

            // WHEN
            //the tested function getPersonByName is called
            Optional<Person> returnedPerson = personService.getPersonByName("FirstNameTest", "LastNameTest");

            // THEN
            //the person should be found
            assertThat(returnedPerson).isPresent();
            assertThat(returnedPerson.get()).isEqualTo(person);
        }

        @Test
        @DisplayName("GIVEN a non existing person " +
                "WHEN the function getPersonByName() is called " +
                "THEN the person shouldn't be found.")
        void getPersonByIdNotExistingTest() {
            // GIVEN
            ////an empty result has to be returned when the personRepository mock is called with the function findByFirstNameAndLastName
            doReturn(Optional.empty()).when(personRepository).findByFirstNameAndLastName("firstName", "lastName");
            // WHEN
            //the tested function getPersonByName is called
            Optional<Person> returnedPerson = personService.getPersonByName("firstName", "lastName");
            // THEN
            //no person should be found
            Assertions.assertFalse(returnedPerson.isPresent());
        }
    }

    @Nested
    @DisplayName("savePerson() tests:")
    class SavePersonTest {

        @Test
        @DisplayName("GIVEN a person with all informations " +
                "WHEN the function savePerson() is called " +
                "THEN the person returned is not null " +
                "and the firstname, lastname and city have been put to upper case " +
                "and other fields are not changed.")
        void savePersonWithAllInformationsTest() {
            // GIVEN
            //a person with all information has to be returned when the personRepository mock is called with the function save
            Person person = new Person(0, "FirstNameTest", "LastNameTest", "address test",
                    "cityTest", 1111, "phoneNumberTest", "mailTest");
            doReturn(person).when(personRepository).save(any());
            // WHEN
            //the tested function savePerson is called
            Person returnedPerson = personService.savePerson(person);
            // THEN
            //the saved person should be returned
            assertThat(returnedPerson).isNotNull();
            //his first name, last name and city should have been put to upper case
            assertThat(returnedPerson.getFirstName()).isEqualTo("FIRSTNAMETEST");
            assertThat(returnedPerson.getLastName()).isEqualTo("LASTNAMETEST");
            assertThat(returnedPerson.getCity()).isEqualTo("CITYTEST");
            //other fields shouldn't have been changed
            assertThat(returnedPerson.getAddress()).isEqualTo("address test");
            assertThat(returnedPerson.getZip()).isEqualTo(1111);
            assertThat(returnedPerson.getPhoneNumber()).isEqualTo("phoneNumberTest");
            assertThat(returnedPerson.getMail()).isEqualTo("mailTest");
        }

        @Test
        @DisplayName("GIVEN a person with only required informations " +
                "WHEN the function savePerson() is called " +
                "THEN the person returned is not null " +
                "and the firstname, lastname have been put to upper case " +
                "and other fields are empty.")
        void savePersonWithRequiredInformationsTest() {
            // GIVEN
            //a person with only firstName and lastName has to be returned when the personRepository mock is called with the function save
            Person person = new Person("FirstNameTest", "LastNameTest");
            doReturn(person).when(personRepository).save(any());
            // WHEN
            //the tested function savePerson is called
            Person returnedPerson = personService.savePerson(person);
            // THEN
            //the saved person should be returned
            assertThat(returnedPerson).isNotNull();
            //his first name and last name should have been put to upper case
            assertThat(returnedPerson.getFirstName()).isEqualTo("FIRSTNAMETEST");
            assertThat(returnedPerson.getLastName()).isEqualTo("LASTNAMETEST");
            //other fields should be empty
            assertThat(returnedPerson.getId()).isEqualTo(0);
            assertThat(returnedPerson.getCity()).isNull();
            assertThat(returnedPerson.getAddress()).isNull();
            assertThat(returnedPerson.getZip()).isEqualTo(0);
            assertThat(returnedPerson.getPhoneNumber()).isNull();
            assertThat(returnedPerson.getMail()).isNull();
        }
    }
}