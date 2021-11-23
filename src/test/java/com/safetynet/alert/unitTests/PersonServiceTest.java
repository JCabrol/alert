package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import com.safetynet.alert.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

        @DisplayName("GIVEN an empty list returned by personRepository " +
                "WHEN function getPersons() is called " +
                "THEN an EmptyPersonsException is thrown.")
        @Test
        public void getPersonsWhenEmptyTest() {
            //GIVEN
            //an empty list of persons
            when(personRepository.findAll()).thenReturn(new ArrayList<>());
            //WHEN
            // the function getPersons() is called
            //THEN
            // an EmptyPersonsException is thrown
            assertThrows(EmptyPersonsException.class, () -> personService.getPersons());
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
            Person returnedPerson = personService.getPersonById(0);
            // THEN
            //the person should be found
            assertThat(returnedPerson).isEqualTo(person);
        }

        @Test
        @DisplayName("GIVEN a non existing person " +
                "WHEN the function getPersonById() is called " +
                "THEN a PersonNotFoundException should be thrown.")
        void getPersonByIdNotExistingTest() {
            // GIVEN
            //an empty result has to be returned when the personRepository mock is called with the function findById
            doReturn(Optional.empty()).when(personRepository).findById(1);
            //WHEN
            //the tested function getPersonById is called with parameter id = 1
            //THEN
            //a PersonNotFoundException should be thrown
            assertThrows(PersonNotFoundException.class, () -> personService.getPersonById(1));
        }
    }

    @Nested
    @DisplayName("getPersonByName() tests:")
    class GetPersonByNameTest {

        @Test
        @DisplayName("GIVEN an existing person " +
                "WHEN the function getPersonByName() is called " +
                "THEN the person should be found.")
        void getPersonByNameTest() {
            // GIVEN
            //a person with FirstNameTest and LastNameTest has to be returned when the personRepository mock is called with the function findById
            Person person = new Person(0, "FirstNameTest", "LastNameTest", "address test",
                    "cityTest", 1111, "phoneNumberTest", "mailTest");
            doReturn(Optional.of(person)).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST", "LASTNAMETEST");
            // WHEN
            //the tested function  getPersonByName is called with FirstNameTest and LastNameTest
            Person returnedPerson = personService.getPersonByName("FirstNameTest", "LastNameTest");
            // THEN
            //the person should be found
            assertThat(returnedPerson).isEqualTo(person);
        }

        @Test
        @DisplayName("GIVEN a non existing person " +
                "WHEN the function getPersonByName() is called " +
                "THEN a PersonNotFoundException should be thrown.")
        void getPersonByIdNotExistingTest() {
            // GIVEN
            ////an empty result has to be returned when the personRepository mock is called with the function findByFirstNameAndLastName
            doReturn(Optional.empty()).when(personRepository).findByFirstNameAndLastName("firstName", "lastName");
            // WHEN
            //the tested function getPersonByName is called
            // THEN
            //a PersonNotFoundException should be thrown
            assertThrows(PersonNotFoundException.class, () -> personService.getPersonByName("firstName", "lastName"));
        }
    }

    @Nested
    @DisplayName("createPerson() tests:")
    class CreatePersonTest {

        @Test
        @DisplayName("GIVEN a person with all informations " +
                "WHEN the function createPerson() is called " +
                "THEN the person returned is not null " +
                "and the firstname, lastname and city have been put to upper case " +
                "and other fields are not changed.")
        void createPersonWithAllInformationTest() {
            // GIVEN
            //a person with all information has to be returned when the personRepository mock is called with the function save
            Person person = new Person(0, "FirstNameTest", "LastNameTest", "address test",
                    "cityTest", 1111, "phoneNumberTest", "mailTest");
            doReturn(person).when(personRepository).save(any());
            // WHEN
            //the tested function createPerson is called
            Person returnedPerson = personService.createPerson(person);
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
                "WHEN the function createPerson() is called " +
                "THEN the person returned is not null " +
                "and the firstname and the lastname have been put to upper case " +
                "and other fields are empty.")
        void createPersonWithOnlyRequiredInformationTest() {
            // GIVEN
            //a person with only firstName and lastName has to be returned when the personRepository mock is called with the function save
            Person person = new Person("FirstNameTest", "LastNameTest");
            doReturn(person).when(personRepository).save(any());
            // WHEN
            //the tested function createPerson is called
            Person returnedPerson = personService.createPerson(person);
            // THEN
            //the created person should be returned
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

        @Test
        @DisplayName("GIVEN a person already existing " +
                "WHEN the function createPerson() is called " +
                "THEN a PersonAlreadyExitingException should be thrown")
        void createPersonAlreadyExistingTest() {
            // GIVEN
            //a person is already existing
            Person person = new Person("FirstNameTest", "LastNameTest");
            doReturn(Optional.of(person)).when(personRepository).findByFirstNameAndLastName(any(), any());
            // WHEN
            //the tested function createPerson is called
            // THEN
            //a PersonAlreadyExitingException should be thrown
            assertThrows(PersonAlreadyExistingException.class, () -> personService.createPerson(person));
        }

        @Test
        @DisplayName("GIVEN a person without required informations " +
                "WHEN the function createPerson() is called " +
                "THEN a NotRightFormatToPostException should be thrown")
        void createPersonWithoutRequiredInformationTest() {
            // GIVEN
            //a person without required information is given
            Person person = new Person();
            doReturn(person).when(personRepository).save(any());
            // WHEN
            //the tested function createPerson is called
            // THEN
            //a NotRightFormatToPostException should be thrown
            assertThrows(NotRightFormatToPostException.class, () -> personService.createPerson(person));
        }
    }

    @Nested
    @DisplayName("updatePerson() tests:")
    class UpdatePersonTest {

        @Test
        @DisplayName("GIVEN a person with all updatable informations " +
                "WHEN the function updatePerson() is called " +
                "THEN the informations have been changed for the new informations and the message returned contains person's name and all items and information changed.")
        void updatePersonWithAllInformationTest() {
            // GIVEN
            //a person with all updatable information
            Person existingPerson = new Person(0, "FirstNameTest", "LastNameTest", "address test original",
                    "cityTest original", 1111, "phoneNumberTest original", "mailTest original");
            Person personWithUpdatingInformation = new Person(1, "FirstNameTest", "LastNameTest", "address test changed",
                    "cityTest changed", 2222, "phoneNumberTest changed", "mailTest changed");
            doReturn(personWithUpdatingInformation).when(personRepository).save(any());
            // WHEN
            //the function updatePerson() is called
            String result = personService.updatePerson(existingPerson, personWithUpdatingInformation);
            // THEN
            //the information have been changed for the new information and the message returned contains person's name and all items and information changed
            assertThat(result).isNotNull();
            assertThat(result).contains("address").contains("city").contains("zip").contains("phone number").contains("mail");
            assertThat(result).contains("FIRSTNAMETEST").contains("LASTNAMETEST").contains("changed");
            assertThat(result).doesNotContain("original");
        }

        @Test
        @DisplayName("GIVEN a person with only some updatable informations " +
                "WHEN the function updatePerson() is called " +
                "THEN the given informations have been updated and the others are still the same and the returned message contains person's name and only updated items and informations.")
        void updatePersonWithOnlySomeInformationTest() {
            // GIVEN
            //a person with all updatable information
            Person existingPerson = new Person(0, "FirstNameTest", "LastNameTest", "address test original",
                    "cityTest original", 1111, "phoneNumberTest original", "mailTest original");
            Person personToUpdate = new Person();
            personToUpdate.setFirstName("FirstNameTest");
            personToUpdate.setLastName("LastNameTest");
            personToUpdate.setAddress("address test changed");
            personToUpdate.setMail("mailTest changed");
            doReturn(personToUpdate).when(personRepository).save(any());
            // WHEN
            //the function updatePerson() is called
            String result = personService.updatePerson(existingPerson, personToUpdate);
            // THEN
            // the given information have been updated and the others are still the same and the returned message contains person's name and only updated items and information
            assertThat(result).isNotNull();
            assertThat(result).contains("address").contains("mail");
            assertThat(result).doesNotContain("city").doesNotContain("zip").doesNotContain("phone number");
            assertThat(result).contains("FIRSTNAMETEST").contains("LASTNAMETEST").contains("address test changed").contains("mailTest changed");
            assertThat(result).doesNotContain("original");
        }

        @Test
        @DisplayName("GIVEN a different firstName or lastName between the person to update and the given informations to update " +
                "WHEN the function updatePerson() is called " +
                "THEN a NotTheSamePersonException should be thrown")
        void updatePersonWithDifferentNameTest() {
            // GIVEN
            // a different firstName or lastName between the person to update and the given information to update
            Person person = new Person("FirstNameTest", "LastNameTest");
            Person personWithUpdatingInformation = new Person(1, "FirstNameTest2", "LastNameTest2", "address test changed",
                    "cityTest changed", 2222, "phoneNumberTest changed", "mailTest changed");
            doReturn(Optional.of(person)).when(personRepository).findByFirstNameAndLastName(any(), any());
            // WHEN
            //the function updatePerson() is called
            // THEN
            //a NotTheSamePersonException should be thrown
            assertThrows(NotTheSamePersonException.class, () -> personService.updatePerson(person, personWithUpdatingInformation));
        }


        @Test
        @DisplayName("GIVEN a person without any information to update " +
                "WHEN the function updatePerson() is called " +
                "THEN a NothingToUpdateException should be thrown")
        void updatePersonWithoutUpdatableInformationTest() {
            // GIVEN
            //  a person without any information to update
            Person person = new Person("FirstNameTest", "LastNameTest");
            Person personWithUpdatingInformation = new Person();
            doReturn(Optional.of(person)).when(personRepository).findByFirstNameAndLastName(any(), any());
            // WHEN
            //the function updatePerson() is called
            // THEN
            //a NothingToUpdateException should be thrown
            assertThrows(NothingToUpdateException.class, () -> personService.updatePerson(person, personWithUpdatingInformation));
        }
    }

    @Nested
    @DisplayName("deletePersonByName() tests:")
    class deletePersonByNameTest {

        @Test
        @DisplayName("GIVEN an existing person " +
                "WHEN the function deletePersonByName() is called with its first name and last name " +
                "THEN the repository methods findByFirstNameAndLastName and deleteById are both invocated one time with right arguments.")
        void existPersonByNameExistingTest() {
            // GIVEN
            // an existing person
            Person person = new Person(0, "FirstNameTest", "LastNameTest", "address test",
                    "cityTest", 1111, "phoneNumberTest", "mailTest");
            doReturn(Optional.of(person)).when(personRepository).findByFirstNameAndLastName(any(), any());
            doNothing().when(personRepository).deleteById(any());
            // WHEN
            //the tested function deletePersonByName() is called with its first name and last name
            personService.deletePersonByName("FirstNameTest", "LastNameTest");
            // THEN
            // the repository methods findByFirstNameAndLastName and deleteById are both invocated one time with right arguments
            verify(personRepository, Mockito.times(1)).findByFirstNameAndLastName("FIRSTNAMETEST", "LASTNAMETEST");
            verify(personRepository, Mockito.times(1)).deleteById(0);

        }

        @Test
        @DisplayName("GIVEN a non-existing person " +
                "WHEN the function deletePersonByName() is called with its first name and last name " +
                "THEN a PersonNotFoundException should be thrown.")
        void existPersonByNameNonExistingTest() {
            // GIVEN
            // a non-existing person
            doReturn(Optional.empty()).when(personRepository).findByFirstNameAndLastName(any(), any());
            // WHEN
            //the function deletePersonByName() is called with its first name and last name
            // THEN
            // a PersonNotFoundException should be thrown
            assertThrows(PersonNotFoundException.class, () -> personService.deletePersonByName("firstName", "lastName"));
        }
    }

    @Nested
    @DisplayName("existPersonByName() tests:")
    class ExistPersonByNameTest {

        @Test
        @DisplayName("GIVEN an existing person " +
                "WHEN the function existPersonByName() is called with its first name and last name " +
                "THEN it should return \"true\".")
        void existPersonByNameExistingTest() {
            // GIVEN
            // an existing person
            Person person = new Person("FirstNameTest", "LastNameTest");
            doReturn(Optional.of(person)).when(personRepository).findByFirstNameAndLastName(any(), any());
            // WHEN
            //the tested function existPersonByName() is called with its first name and last name
            boolean result = personService.existPersonByName("FirstNameTest", "LastNameTest");
            // THEN
            // it should return "true"
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN a non-existing person " +
                "WHEN the function existPersonByName() is called with its first name and last name " +
                "THEN it should return \"false\".")
        void existPersonByNameNonExistingTest() {
            // GIVEN
            // a non-existing person
            doReturn(Optional.empty()).when(personRepository).findByFirstNameAndLastName(any(), any());
            // WHEN
            //the tested function existPersonByName() is called with its first name and last name
            boolean result = personService.existPersonByName("FirstNameTest", "LastNameTest");
            // THEN
            // it should return "false"
            assertThat(result).isFalse();
        }
    }


}