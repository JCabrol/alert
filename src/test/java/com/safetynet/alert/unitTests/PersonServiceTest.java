package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Address;
import com.safetynet.alert.model.DTO.PersonDTO;
import com.safetynet.alert.model.MedicalRecords;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import com.safetynet.alert.service.AddressService;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Tag("PersonTests")
@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest
public class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private AddressService addressService;

    @Nested
    @DisplayName("getPersons() tests:")
    class getPersonsTest {

        @DisplayName("GIVEN persons returned by personRepository " +
                "WHEN function getPersons() is called " +
                "THEN it returns the same persons.")
        @Test
        public void getPersonsWhenNonEmptyTest() {
            //GIVEN
            //a list containing 3 persons
            ArrayList<Person> AllPersonsTest = new ArrayList<>();
            for (int numberOfPersonsTest = 0; numberOfPersonsTest < 3; numberOfPersonsTest++) {
                Person person = new Person("idtest"+numberOfPersonsTest,
                        "FIRSTNAME" + numberOfPersonsTest,
                        "LASTNAME" + numberOfPersonsTest,
                        new Address(" main street", "1234" + numberOfPersonsTest, "CITY" + numberOfPersonsTest),
                        "123456789" + numberOfPersonsTest,
                        "person" + numberOfPersonsTest + "@mail.com",
                        new MedicalRecords());
                AllPersonsTest.add(person);
            }
            when(personRepository.findAll()).thenReturn(AllPersonsTest);
            //WHEN
            //the tested function getPersons is called
            List<Person> result = personService.getPersons();
            //THEN
            //the result should contain 3 persons and should be the same as the list created first
            assertThat(result.size()).isEqualTo(3);
            assertThat(result).isEqualTo(AllPersonsTest);
            verify(personRepository, Mockito.times(1)).findAll();
        }

        @DisplayName("GIVEN an empty list returned by personRepository " +
                "WHEN function getPersons() is called " +
                "THEN an EmptyObjectException should be thrown with the expected error message.")
        @Test
        public void getPersonsWhenEmptyTest() {
            //GIVEN
            //an empty list of persons
            when(personRepository.findAll()).thenReturn(new ArrayList<>());
            //WHEN
            // the function getPersons() is called
            //THEN
            // an EmptyObjectException should be thrown with the expected error message
           Exception exception = assertThrows(EmptyObjectException.class, () -> personService.getPersons());
            assertEquals("There are no persons registered.\n", exception.getMessage());
            verify(personRepository, Mockito.times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getPersonsDTO() tests:")
    class getPersonsDTOTest {

        @DisplayName("GIVEN persons returned by personRepository " +
                "WHEN function getPersonsDTO() is called " +
                "THEN it returns a list with the corresponding personsDTO.")
        @Test
        public void getPersonsDTOWhenNonEmptyTest() {
            //GIVEN
            //a list containing 3 persons
            ArrayList<Person> AllPersonsTest = new ArrayList<>();
            for (int numberOfPersonsTest = 0; numberOfPersonsTest < 3; numberOfPersonsTest++) {
                Person person = new Person("idtest"+numberOfPersonsTest,
                        "FIRSTNAME" + numberOfPersonsTest,
                        "LASTNAME" + numberOfPersonsTest,
                        new Address(" main street", "1234" + numberOfPersonsTest, "CITY" + numberOfPersonsTest),
                        "123456789" + numberOfPersonsTest,
                        "person" + numberOfPersonsTest + "@mail.com",
                        new MedicalRecords());
                AllPersonsTest.add(person);
            }
            when(personRepository.findAll()).thenReturn(AllPersonsTest);
            //WHEN
            //the tested function getPersons is called
            List<PersonDTO> result = personService.getPersonsDTO();
            //THEN
            //the result should contain 3 persons and should be the same as the list created first
            assertThat(result.size()).isEqualTo(3);
            assertThat(result.get(0).getFirstName()).isEqualToIgnoringCase(AllPersonsTest.get(0).getFirstName());
            assertThat(result.get(1).getFirstName()).isEqualToIgnoringCase(AllPersonsTest.get(1).getFirstName());
            assertThat(result.get(2).getFirstName()).isEqualToIgnoringCase(AllPersonsTest.get(2).getFirstName());
            assertThat(result.get(0).getLastName()).isEqualToIgnoringCase(AllPersonsTest.get(0).getLastName());
            assertThat(result.get(1).getLastName()).isEqualToIgnoringCase(AllPersonsTest.get(1).getLastName());
            assertThat(result.get(2).getLastName()).isEqualToIgnoringCase(AllPersonsTest.get(2).getLastName());
            verify(personRepository, Mockito.times(1)).findAll();
        }

        @DisplayName("GIVEN an empty list returned by personRepository " +
                "WHEN function getPersonsDTO() is called " +
                "THEN an EmptyObjectException should be thrown with the expected error message.")
        @Test
        public void getPersonsDTOWhenEmptyTest() {
            //GIVEN
            //an empty list of persons
            when(personRepository.findAll()).thenReturn(new ArrayList<>());
            //WHEN
            // the function getPersonsDTO() is called
            //THEN
            // an EmptyObjectException should be thrown with the expected error message
            Exception exception = assertThrows(EmptyObjectException.class, () -> personService.getPersonsDTO());
            assertEquals("There are no persons registered.\n", exception.getMessage());
            verify(personRepository, Mockito.times(1)).findAll();
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
            //an existing person
            Person person  = new Person("idTest", "FIRSTNAME", "LASTNAME", new Address(" address test", "1234 test", "CITYTEST" ), "1234567890" , "person@mail.com", new MedicalRecords());
            doReturn(Optional.of(person)).when(personRepository).findById("idTest");
            // WHEN
            //the function getPersonById() is called
            Person returnedPerson = personService.getPersonById("idTest");
            // THEN
            //the person should be found
            assertThat(returnedPerson).isEqualTo(person);
            verify(personRepository, Mockito.times(1)).findById("idTest");
        }

        @Test
        @DisplayName("GIVEN a non-existing person " +
                "WHEN the function getPersonById() is called " +
                "THEN an ObjectNotFoundException should be thrown with the expected error message.")
        void getPersonByIdNotExistingTest() {
            // GIVEN
            //a non-existing person
            doReturn(Optional.empty()).when(personRepository).findById("idTest");
            //WHEN
            //the function getPersonById() is called
            //THEN
            //an ObjectNotFoundException should be thrown with the expected error message
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> personService.getPersonById("idTest"));
            assertEquals("The person with id idTest was not found.\n", exception.getMessage());
            verify(personRepository, Mockito.times(1)).findById("idTest");
        }
    }

    @Nested
    @DisplayName("getPersonDTOById() tests:")
    class GetPersonDTOByIdTest {

        @Test
        @DisplayName("GIVEN an existing person " +
                "WHEN the function getPersonDTOById() is called " +
                "THEN the corresponding personDTO should be returned.")
        void getPersonDTOByIdTest() {
            // GIVEN
            //an existing person
            Person person  = new Person("idTest", "FIRSTNAME", "LASTNAME", new Address(" address test", "1234 test", "CITYTEST" ), "1234567890" , "person@mail.com", new MedicalRecords());
            doReturn(Optional.of(person)).when(personRepository).findById("idTest");
            // WHEN
            //the function getPersonById() is called
            PersonDTO returnedPerson = personService.getPersonDTOById("idTest");
            // THEN
            //the corresponding personDTO should be returned
            assertThat(returnedPerson.getFirstName()).isEqualTo(person.getFirstName());
            assertThat(returnedPerson.getLastName()).isEqualTo(person.getLastName());
            assertThat(returnedPerson.getMail()).isEqualTo(person.getMail());
            assertThat(returnedPerson.getPhoneNumber()).isEqualTo(person.getPhoneNumber());
            assertThat(returnedPerson.getAddress()).isEqualTo(person.getAddress().getStreet());
            assertThat(returnedPerson.getZip()).isEqualTo(person.getAddress().getZip());
            assertThat(returnedPerson.getCity()).isEqualTo(person.getAddress().getCity());
            verify(personRepository, Mockito.times(1)).findById("idTest");
        }

        @Test
        @DisplayName("GIVEN a non-existing person " +
                "WHEN the function getPersonDTOById() is called " +
                "THEN an ObjectNotFoundException should be thrown with the expected error message.")
        void getPersonDTOByIdNotExistingTest() {
            // GIVEN
            //a non-existing person
            doReturn(Optional.empty()).when(personRepository).findById("idTest");
            //WHEN
            //the function getPersonById() is called
            //THEN
            //an ObjectNotFoundException should be thrown with the expected error message
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> personService.getPersonDTOById("idTest"));
            assertEquals("The person with id idTest was not found.\n", exception.getMessage());
            verify(personRepository, Mockito.times(1)).findById("idTest");
        }
    }



    @Nested
    @DisplayName("createPerson() tests:")
    class CreatePersonTest {

        @Test
        @DisplayName("GIVEN a personDTO with all information and a non-existing address " +
                "WHEN the function createPerson() is called " +
                "THEN the person returned should correspond.")
        void createPersonWithAllInformationAndNonExistingAddressTest() {
            // GIVEN
            // a personDTO with all information and a non-existing address
            Address address = new Address("address test", "11111", "CITYTEST");
            Person person = new Person("idTest",
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    address,
                    "1234567890",
                    "personTest@mail.com",
                    new MedicalRecords());
            PersonDTO personDTO = new PersonDTO("FirstNameTest",
                    "LastNameTest",
                    "address test", "11111", "cityTest",
                    "1234567890",
                    "personTest@mail.com");
            doReturn(person).when(personRepository).save(any(Person.class));
            doReturn(new ArrayList<>()).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            doThrow(ObjectNotFoundException.class).when(addressService).getAddress("address test", "11111", "CITYTEST");
            // WHEN
            //the tested function createPerson is called
            Person returnedPerson = personService.createPerson(personDTO);
            // THEN
            //the person returned should correspond.
            assertThat(returnedPerson).isNotNull();
            //his first name, last name and city should have been put to upper case
            assertThat(returnedPerson.getFirstName()).isEqualTo("FIRSTNAMETEST");
            assertThat(returnedPerson.getLastName()).isEqualTo("LASTNAMETEST");
            assertThat(returnedPerson.getAddress().getCity()).isEqualTo("CITYTEST");
            //other fields shouldn't have been changed
            assertThat(returnedPerson.getAddress().getStreet()).isEqualTo("address test");
            assertThat(returnedPerson.getAddress().getZip()).isEqualTo("11111");
            assertThat(returnedPerson.getPhoneNumber()).isEqualTo("1234567890");
            assertThat(returnedPerson.getMail()).isEqualTo("personTest@mail.com");
            verify(personRepository, Mockito.times(1)).save(any(Person.class));
            verify(personRepository, Mockito.times(1)).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            verify(addressService, Mockito.times(1)).getAddress("address test", "11111", "CITYTEST");
        }

        @Test
        @DisplayName("GIVEN a personDTO with all information and an existing address " +
                "WHEN the function createPerson() is called " +
                "THEN the person returned should correspond.")
        void createPersonWithAllInformationAndExistingAddressTest() {
            // GIVEN
            //a personDTO with all information and an existing address
            Address address = new Address("address test", "11111", "CITYTEST");
            Person person = new Person("idTest",
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    address,
                    "1234567890",
                    "personTest@mail.com",
                    new MedicalRecords());
            PersonDTO personDTO = new PersonDTO("FirstNameTest",
                    "LastNameTest",
                    "address test", "11111", "cityTest",
                    "1234567890",
                    "personTest@mail.com");
            doReturn(person).when(personRepository).save(any(Person.class));
            doReturn(new ArrayList<>()).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            doReturn(address).when(addressService).getAddress("address test", "11111", "CITYTEST");
            // WHEN
            //the tested function createPerson is called
            Person returnedPerson = personService.createPerson(personDTO);
            // THEN
            //the person returned should correspond.
            assertThat(returnedPerson).isNotNull();
            //his first name, last name and city should have been put to upper case
            assertThat(returnedPerson.getFirstName()).isEqualTo("FIRSTNAMETEST");
            assertThat(returnedPerson.getLastName()).isEqualTo("LASTNAMETEST");
            assertThat(returnedPerson.getAddress().getCity()).isEqualTo("CITYTEST");
            //other fields shouldn't have been changed
            assertThat(returnedPerson.getAddress().getStreet()).isEqualTo("address test");
            assertThat(returnedPerson.getAddress().getZip()).isEqualTo("11111");
            assertThat(returnedPerson.getPhoneNumber()).isEqualTo("1234567890");
            assertThat(returnedPerson.getMail()).isEqualTo("personTest@mail.com");
            verify(personRepository, Mockito.times(1)).save(any(Person.class));
            verify(personRepository, Mockito.times(1)).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            verify(addressService, Mockito.times(1)).getAddress("address test", "11111", "CITYTEST");
        }

        @Test
        @DisplayName("GIVEN a person with only required informations " +
                "WHEN the function createPerson() is called " +
                "THEN the person returned is not null " +
                "and the firstname and the lastname have been put to upper case " +
                "and other fields are empty.")
        void createPersonWithOnlyRequiredInformationTest() {
            // GIVEN
            // a personDTO with all information and a non-existing address
            Person person = new Person("FIRSTNAMETEST", "LASTNAMETEST");
            PersonDTO personDTO = new PersonDTO("FirstNameTest",
                    "LastNameTest",
                    null, null, null,
                    null,
                    null);
            doReturn(person).when(personRepository).save(any(Person.class));
            doReturn(new ArrayList<>()).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            // WHEN
            //the tested function createPerson is called
            Person returnedPerson = personService.createPerson(personDTO);
            // THEN
            //the person returned should correspond.
            assertThat(returnedPerson).isNotNull();
            assertThat(returnedPerson.getFirstName()).isEqualTo("FIRSTNAMETEST");
            assertThat(returnedPerson.getLastName()).isEqualTo("LASTNAMETEST");
            assertThat(returnedPerson.getAddress()).isEqualTo(null);
            assertThat(returnedPerson.getPhoneNumber()).isEqualTo(null);
            assertThat(returnedPerson.getMail()).isEqualTo(null);
            verify(personRepository, Mockito.times(1)).save(any(Person.class));
            verify(personRepository, Mockito.times(1)).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            verify(addressService, Mockito.times(0)).getAddress(anyString(), anyString(), anyString());
        }

    @Test
    @DisplayName("GIVEN a person already existing " +
            "WHEN the function createPerson() is called " +
            "THEN an ObjectAlreadyExitingException should be thrown")
    void createPersonAlreadyExistingTest() {
        // GIVEN
        //a person is already existing
        Address address = new Address("address test", "11111", "CITYTEST");
        Person person = new Person("idTest",
                "FIRSTNAMETEST",
                "LASTNAMETEST",
                address,
                "1234567890",
                "personTest@mail.com",
                new MedicalRecords());
        PersonDTO personDTO = new PersonDTO("FirstNameTest",
                "LastNameTest",
                "address test", "11111", "cityTest",
                "1234567890",
                "personTest@mail.com");
        doReturn(List.of(person)).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
        // WHEN
        //the tested function createPerson is called
        // THEN
        //an ObjectAlreadyExitingException should be thrown
        Exception exception = assertThrows(ObjectAlreadyExistingException.class, () -> personService.createPerson(personDTO));
        assertEquals("The person FIRSTNAMETEST LASTNAMETEST was already existing, so it couldn't have been created.\n", exception.getMessage());
        verify(personRepository, Mockito.times(0)).save(any());
        verify(personRepository, Mockito.times(2)).findByFirstNameAndLastName("FIRSTNAMETEST", "LASTNAMETEST");
    }

        @Test
        @DisplayName("GIVEN a different person with the same name " +
                "WHEN the function createPerson() is called " +
                "THEN the person should be created")
        void createPersonSameNameTest() {
            // GIVEN
            //a person is already existing
            Address address = new Address("address test", "11111", "CITYTEST");
            Person person = new Person("idTest",
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    address,
                    "1234567890",
                    "personTest@mail.com",
                    new MedicalRecords());
            PersonDTO personDTO = new PersonDTO("FirstNameTest",
                    "LastNameTest",
                    "address test2", "11111", "cityTest",
                    null,
                    null);
            doReturn(List.of(person)).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            // WHEN
            //the tested function createPerson is called
            Person returnedPerson = personService.createPerson(personDTO);
            // THEN
            //the person should be created
            assertThat(returnedPerson).isNotNull();
            assertThat(returnedPerson.getFirstName()).isEqualTo("FIRSTNAMETEST");
            assertThat(returnedPerson.getLastName()).isEqualTo("LASTNAMETEST");
            assertThat(returnedPerson.getAddress()).isNotEqualTo(address);
            assertThat(returnedPerson.getPhoneNumber()).isEqualTo(null);
            assertThat(returnedPerson.getMail()).isEqualTo(null);
            verify(personRepository, Mockito.times(1)).save(any(Person.class));
            verify(personRepository, Mockito.times(2)).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            verify(addressService, Mockito.times(1)).getAddress(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("GIVEN a person without required informations " +
                "WHEN the function createPerson() is called " +
                "THEN a NotRightFormatToPostException should be thrown")
        void createPersonWithoutRequiredInformationTest() {
            // GIVEN
            //a person without required information is given
            PersonDTO personDTO = new PersonDTO();
            // WHEN
            //the tested function createPerson is called
            // THEN
            //a NotRightFormatToPostException should be thrown
           Exception exception = assertThrows(NotRightFormatToPostException.class, () -> personService.createPerson(personDTO));
            assertEquals("There is something missing in the request :\nto post a new person there should be at least a \"firstName\" and a \"lastName\" fields.\n", exception.getMessage());
            verify(personRepository, Mockito.times(0)).save(any());
            verify(personRepository, Mockito.times(0)).findByFirstNameAndLastName("FIRSTNAMETEST", "LASTNAMETEST");
        }
    }

    @Nested
    @DisplayName("updatePerson() tests:")
    class UpdatePersonTest {

        @Test
        @DisplayName("GIVEN a person with all updatable information and non-existing address " +
                "WHEN the function updatePerson() is called " +
                "THEN the modified person should be returned.")
        void updatePersonAllInformationNonExistingAddressTest() {
            // GIVEN
            //a person with all updatable information and non-existing address
            Address address = new Address("address test2", "22222", "CITYTEST2");
            PersonDTO existingPerson = new PersonDTO(
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    "address test", "11111", "CITYTEST",
                    "1234567890",
                    "personTest@mail.com");
            PersonDTO personWithUpdatingInformation = new PersonDTO("FirstNameTest",
                    "LastNameTest",
                    "address test2", "22222", "cityTest2",
                    "876543210",
                    "personTest@mail.com");
            Person personToSave = new Person("FIRSTNAMETESTLASTNAMETEST",
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    address,
                    "876543210",
                    "personTest@mail.com",
                    null);
            doReturn(Optional.of(personToSave)).when(personRepository).findById("FIRSTNAMETESTLASTNAMETEST");
            doReturn(personToSave).when(personRepository).save(personToSave);
            doThrow(ObjectNotFoundException.class).when(addressService).getAddress("address test2", "22222", "CITYTEST2");
            // WHEN
            //the function updatePerson() is called
            Person personResult = personService.updatePerson(existingPerson, personWithUpdatingInformation);
            // THEN
            //the modified person should be returned.
            assertThat(personResult).isEqualTo(personToSave);
            verify(personRepository, Mockito.times(1)).findById("FIRSTNAMETESTLASTNAMETEST");
            verify(personRepository, Mockito.times(1)).save(personToSave);
            verify(addressService, Mockito.times(1)).getAddress("address test2", "22222", "CITYTEST2");
        }

        @Test
        @DisplayName("GIVEN a person with all updatable information and existing address " +
                "WHEN the function updatePerson() is called " +
                "THEN the modified person should be returned.")
        void updatePersonAllInformationExistingAddressTest() {
            // GIVEN
            //a person with all updatable information and existing address
            Address address = new Address("address test2", "22222", "CITYTEST2");
            PersonDTO existingPerson = new PersonDTO(
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    "address test", "11111", "CITYTEST",
                    "1234567890",
                    "personTest@mail.com");
            PersonDTO personWithUpdatingInformation = new PersonDTO("FirstNameTest",
                    "LastNameTest",
                    "address test2", "22222", "cityTest2",
                    "876543210",
                    "personTest@mail.com");
            Person personToSave = new Person("FIRSTNAMETESTLASTNAMETEST",
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    address,
                    "876543210",
                    "personTest@mail.com",
                    null);
            doReturn(Optional.of(personToSave)).when(personRepository).findById("FIRSTNAMETESTLASTNAMETEST");
            doReturn(personToSave).when(personRepository).save(personToSave);
            doReturn(address).when(addressService).getAddress("address test2", "22222", "CITYTEST2");
            // WHEN
            //the function updatePerson() is called
            Person personResult = personService.updatePerson(existingPerson, personWithUpdatingInformation);
            // THEN
            //the modified person should be returned.
            assertThat(personResult).isEqualTo(personToSave);
            verify(personRepository, Mockito.times(1)).findById("FIRSTNAMETESTLASTNAMETEST");
            verify(personRepository, Mockito.times(1)).save(personToSave);
            verify(addressService, Mockito.times(1)).getAddress("address test2", "22222", "CITYTEST2");
        }

        @Test
        @DisplayName("GIVEN a person with only some updatable information" +
                "WHEN the function updatePerson() is called " +
                "THEN the modified person should be returned.")
        void updatePersonSomeUpdatableInformationTest() {
            // GIVEN
            //a person with only some updatable information
            Address address = new Address("address test2", "22222", "CITYTEST2");
            PersonDTO existingPerson = new PersonDTO(
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    "address test", "11111", "CITYTEST",
                    "1234567890",
                    "personTest@mail.com");
            PersonDTO personWithUpdatingInformation = new PersonDTO(null,
                    null,
                    null, null, null,
                    "876543210",
                    "personTest@mail.com");
            Person personToSave = new Person("FIRSTNAMETESTLASTNAMETEST",
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    address,
                    "876543210",
                    "personTest@mail.com",
                    null);
            doReturn(Optional.of(personToSave)).when(personRepository).findById("FIRSTNAMETESTLASTNAMETEST");
            doReturn(personToSave).when(personRepository).save(personToSave);
            // WHEN
            //the function updatePerson() is called
            Person personResult = personService.updatePerson(existingPerson, personWithUpdatingInformation);
            // THEN
            //the modified person should be returned.
            assertThat(personResult).isEqualTo(personToSave);
            verify(personRepository, Mockito.times(1)).findById("FIRSTNAMETESTLASTNAMETEST");
            verify(personRepository, Mockito.times(1)).save(personToSave);
            verify(addressService, Mockito.times(0)).getAddress(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("GIVEN a different firstName or lastName between the person to update and the given informations to update " +
                "WHEN the function updatePerson() is called " +
                "THEN a NotTheSamePersonException should be thrown with the expected error message")
        void updatePersonWithDifferentNameTest() {
            // GIVEN
            // a different firstName or lastName between the person to update and the given information to update
            PersonDTO existingPerson = new PersonDTO(
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    "address test", "11111", "CITYTEST",
                    "1234567890",
                    "personTest@mail.com");
            PersonDTO personWithUpdatingInformation = new PersonDTO("differentFirstName",
                    "differentLastName",
                    null, null, null,
                    "876543210",
                    "personTest@mail.com");
            // WHEN
            //the function updatePerson() is called
            // THEN
            // NotTheSamePersonException should be thrown with the expected error message
            Exception exception = assertThrows(NotTheSamePersonException.class, () -> personService.updatePerson(existingPerson, personWithUpdatingInformation));
            assertEquals("It's not possible to update person first name or person last name,\n" +
                    "so the person called FIRSTNAMETEST LASTNAMETEST cannot be updated to a person called DIFFERENTFIRSTNAME DIFFERENTLASTNAME.\n", exception.getMessage());
            verify(personRepository, Mockito.times(0)).findById("FIRSTNAMETESTLASTNAMETEST");
            verify(personRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(0)).getAddress(anyString(), anyString(), anyString());
        }


        @Test
        @DisplayName("GIVEN a person without any information to update " +
                "WHEN the function updatePerson() is called " +
                "THEN a NothingToUpdateException should be thrown")
        void updatePersonWithoutUpdatableInformationTest() {
            // GIVEN
            //  a person without any information to update

            PersonDTO existingPerson = new PersonDTO(
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    "address test", "11111", "CITYTEST",
                    "1234567890",
                    "personTest@mail.com");
            PersonDTO personWithUpdatingInformation = new PersonDTO("firstNameTest",
                    "LastNameTest",
                    null, null, null,
                    null,
                    null);
            Person personToSave = new Person("FIRSTNAMETESTLASTNAMETEST",
                    "FIRSTNAMETEST",
                    "LASTNAMETEST",
                    null,
                    "876543210",
                    "personTest@mail.com",
                    null);
            doReturn(Optional.of(personToSave)).when(personRepository).findById("FIRSTNAMETESTLASTNAMETEST");
            doReturn(personToSave).when(personRepository).save(personToSave);
            // WHEN
            //the function updatePerson() is called
            // THEN
            //a NothingToUpdateException should be thrown
            Exception exception = assertThrows(NothingToUpdateException.class, () -> personService.updatePerson(existingPerson, personWithUpdatingInformation));
            assertEquals("The person FIRSTNAMETEST LASTNAMETEST wasn't updated, there was no element to update.\n", exception.getMessage());
            verify(personRepository, Mockito.times(1)).findById("FIRSTNAMETESTLASTNAMETEST");
            verify(personRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(0)).getAddress(anyString(), anyString(), anyString());
        }
    }

        @Nested
        @DisplayName("deletePersonById() tests:")
        class DeletePersonByIdTest {

            @Test
            @DisplayName("GIVEN an existing person " +
                    "WHEN the function deletePersonById() is called " +
                    "THEN the repository methods findById and deleteById are both invocated one time with right arguments.")
            void deletePersonByIdExistingTest() {
                // GIVEN
                // an existing person
                Person person = new Person("idTest", "FirstNameTest", "LastNameTest", null, "phoneNumberTest", "mailTest", null);
                doReturn(Optional.of(person)).when(personRepository).findById("idTest");
                doNothing().when(personRepository).deleteById("idTest");
                // WHEN
                //the function deletePersonById() is called
                personService.deletePersonById("idTest");
                // THEN
                // the repository methods findById and deleteById are both invocated one time with right arguments
                verify(personRepository, Mockito.times(1)).findById("idTest");
                verify(personRepository, Mockito.times(1)).deleteById("idTest");

            }

            @Test
            @DisplayName("GIVEN a non-existing person " +
                    "WHEN the function deletePersonById() is called " +
                    "THEN a PersonNotFoundException should be thrown with the expected error message.")
            void deletePersonByIdNonExistingTest() {
                // GIVEN
                // a non-existing person
                doReturn(Optional.empty()).when(personRepository).findById(any());
                // WHEN
                //the function deletePersonById() is called
                // THEN
                // a PersonNotFoundException should be thrown with the expected error message
                Exception exception = assertThrows(ObjectNotFoundException.class, () -> personService.deletePersonById("idTest"));
                assertEquals("The person with id idTest was not found, so it cannot have been deleted.\n", exception.getMessage());
                verify(personRepository, Mockito.times(1)).findById("idTest");
                verify(personRepository, Mockito.times(0)).deleteById(any());

            }
        }

    @Nested
    @DisplayName("getPersonsByName() tests:")
    class GetPersonsByNameTest {

        @Test
        @DisplayName("GIVEN existing person " +
                "WHEN the function getPersonsByName() is called " +
                "THEN a list containing this person should be returned.")
        void getPersonsByNameExistingTest() {
            // GIVEN
            // an existing person
            Person person = new Person("idTest", "FirstNameTest", "LastNameTest", null, "phoneNumberTest", "mailTest", null);
            List<Person> personList =List.of(person);
            doReturn(personList).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            // WHEN
            //the function getPersonsByName() is called
            List<Person> resultList = personService.getPersonsByName("FirstNameTest","LastNameTest");
            // THEN
            // a list containing this person should be returned.
            assertThat(resultList).isEqualTo(personList);
            verify(personRepository, Mockito.times(1)).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
        }

        @Test
        @DisplayName("GIVEN a non-existing person " +
                "WHEN the function getPersonsByName() is called " +
                "THEN a PersonNotFoundException should be thrown with the expected error message.")
        void getPersonsByNameNonExistingTest() {
            // GIVEN
            // a non-existing person
            doReturn(new ArrayList<>()).when(personRepository).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
            // WHEN
            //the function getPersonsByName() is called
            // THEN
            // a PersonNotFoundException should be thrown with the expected error message
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> personService.getPersonsByName("FirstNameTest","LastNameTest"));
            assertEquals("The person FIRSTNAMETEST LASTNAMETEST was not found.\n", exception.getMessage());
            verify(personRepository, Mockito.times(1)).findByFirstNameAndLastName("FIRSTNAMETEST","LASTNAMETEST");
        }
    }
}