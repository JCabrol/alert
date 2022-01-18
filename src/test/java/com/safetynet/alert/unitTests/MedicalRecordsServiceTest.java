package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.model.DTO.MedicalRecordDTO;
import com.safetynet.alert.model.MedicalRecords;
import com.safetynet.alert.model.Medication;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.MedicalRecordsRepository;
import com.safetynet.alert.service.MedicalRecordsService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;


@Tag("MedicalRecordsTests")
@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest
public class MedicalRecordsServiceTest {

    @Autowired
    private MedicalRecordsService medicalRecordsService;

    @MockBean
    private MedicalRecordsRepository medicalRecordsRepository;

    @MockBean
    private PersonService personService;


    @Nested
    @DisplayName("getMedicalRecords() tests:")
    class getMedicalRecordsTest {

        @DisplayName("GIVEN an existing list of medicalRecords " +
                "WHEN function getMedicalRecords is called " +
                "THEN the same list of medicalRecords should be returned.")
        @Test
        public void getMedicalRecordsWhenNonEmptyTest() {
            //GIVEN
            //an existing list of medicalRecords
            MedicalRecords medicalRecords1 = new MedicalRecords();
            MedicalRecords medicalRecords2 = new MedicalRecords();
            Person person1 = new Person("firstNameTest1", "lastNameTest1");
            Person person2 = new Person("firstNameTest2", "lastNameTest2");
            medicalRecords1.addPerson(person1);
            medicalRecords2.addPerson(person2);
            LocalDate birthdate1 = LocalDate.of(1982, 11, 14);
            LocalDate birthdate2 = LocalDate.of(2013, 4, 18);
            medicalRecords1.setBirthdate(birthdate1);
            medicalRecords2.setBirthdate(birthdate2);
            List<MedicalRecords> medicalRecordsList = List.of(medicalRecords1, medicalRecords2);
            doReturn(medicalRecordsList).when(medicalRecordsRepository).findAll();
            //WHEN
            //the tested function getMedicalRecords is called
            List<MedicalRecordDTO> result = medicalRecordsService.getMedicalRecords();
            //THEN
            //the same list of medicalRecords should be returned
            assertThat(result.size()).isEqualTo(2);
            assertThat(result.get(0).getFirstName()).isEqualTo("firstNameTest1");
            assertThat(result.get(1).getFirstName()).isEqualTo("firstNameTest2");
            assertThat(result.get(0).getBirthdate()).isEqualTo("14-11-1982");
            assertThat(result.get(1).getBirthdate()).isEqualTo("18-04-2013");
            verify(medicalRecordsRepository, Mockito.times(1)).findAll();
        }

        @DisplayName("GIVEN an empty list of medicalRecords " +
                "WHEN function getMedicalRecords is called " +
                "THEN an EmptyMedicalrecordsException should be thrown.")
        @Test
        public void getMedicalRecordsWhenEmptyTest() {
            //GIVEN
            //an empty list of medical records
            when(medicalRecordsRepository.findAll()).thenReturn(new ArrayList<>());
            //WHEN
            // the tested function getMedicalRecords is called
            //THEN
            // an emptyMedicalrecordsException should be thrown
            assertThrows(EmptyObjectException.class, () -> medicalRecordsService.getMedicalRecords());
            verify(medicalRecordsRepository, Mockito.times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getMedicalrecordsByName() tests:")
    class GetMedicalRecordsByNameTest {

        @Test
        @DisplayName("GIVEN existing medicalRecords " +
                "WHEN the function getMedicalRecordsByName is called " +
                "THEN the medicalRecords should be found.")
        void getMedicalRecordsByNameTest() {
            // GIVEN
            //existing medicalRecords
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            MedicalRecords medicalRecords = new MedicalRecords();
            medicalRecords.setMedicalId(1);
            medicalRecords.addAllergy(new Allergy("allergy test"));
            medicalRecords.addMedication(new Medication("medication test"));
            LocalDate birthdate = LocalDate.of(1982, 11, 14);
            medicalRecords.setBirthdate(birthdate);
            Person personTest = new Person(firstName, lastName);
            medicalRecords.addPerson(personTest);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function getMedicalRecordsByName is called
            MedicalRecords returnedMedicalRecords = medicalRecordsService.getMedicalRecordsByName("firstNameTest", "lastNameTest");
            // THEN
            //the medicalRecords should be found
            assertThat(returnedMedicalRecords.getPerson().getFirstName()).isEqualTo(firstName);
            assertThat(returnedMedicalRecords.getPerson().getLastName()).isEqualTo(lastName);
            verify(personService, Mockito.times(1)).getPersonById("FIRSTNAMETESTLASTNAMETEST");
        }


        @Test
        @DisplayName("GIVEN non-existing medicalRecords " +
                "WHEN the function getMedicalRecordsByName is called " +
                "THEN a ObjectNotFoundException should be thrown.")
        void getMedicalRecordsByNameNotExistingTest() {
            // GIVEN
            //non-existing medicalRecords
            Person personTest = new Person("firstNameTest", "lastNameTest");
            doReturn(personTest).when(personService).getPersonById("FIRSTNAMETESTLASTNAMETEST");
            //WHEN
            //the tested function getMedicalRecordsByName is called
            //THEN
            //aa ObjectNotFoundException should be thrown
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> medicalRecordsService.getMedicalRecordsByName("firstNameTest", "lastNameTest"));
            assertEquals("There is no medical records found for the person firstNameTest lastNameTest.\n", exception.getMessage());
            verify(personService, Mockito.times(1)).getPersonById("FIRSTNAMETESTLASTNAMETEST");
        }
    }

    @Nested
    @DisplayName("addNewMedicalRecords tests:")
    class AddNewMedicalRecordsTest {

        @Test
        @DisplayName("GIVEN an existing person with no medical records " +
                "WHEN the function addNewMedicalRecords is called  " +
                "THEN new medical records should be created and attached to this person.")
        void addNewMedicalRecordsWithExistingPersonWithNoMedicalRecordsTest() {
            // GIVEN
            //an existing person with no medical records
            String firstName = "fistNameTest";
            String lastName = "lastNameTest";
            String birthdate = "14-11-1982";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            Person personTest = new Person(firstName, lastName);
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            doReturn(null).when(medicalRecordsRepository).save(any());
            // WHEN
            //the tested function addNewMedicalRecords is called
            MedicalRecords result = medicalRecordsService.addNewMedicalRecords(medicalRecordsTest);
            // THEN
            //new medical records should be created and attached to this person
            assertThat(result.getPerson()).isEqualTo(personTest);
            assertThat(result.getBirthdate()).isEqualTo(LocalDate.of(1982, 11, 14));
            assertThat(result.getMedications().size()).isEqualTo(2);
            assertThat(result.getMedications().get(0).getMedicationName()).isEqualTo(medications.get(0));
            assertThat(result.getMedications().get(1).getMedicationName()).isEqualTo(medications.get(1));
            assertThat(result.getAllergies().size()).isEqualTo(1);
            assertThat(result.getAllergies().get(0).getAllergyName()).isEqualTo(allergies.get(0));
            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN an existing person with medical records " +
                "WHEN the function addNewMedicalRecords is called  " +
                "THEN new informations should replace older information for this person's medical records.")
        void addNewMedicalRecordsWithExistingPersonWithMedicalRecordsTest() {
            // GIVEN
            //an existing person with medical records
            String firstName = "fistNameTest";
            String lastName = "lastNameTest";
            String newBirthdate = "14-11-1982";
            List<String> newMedication = new ArrayList<>();
            newMedication.add("newMedicationTest1");
            newMedication.add("newMedicationTest2");
            List<String> newAllergies = new ArrayList<>();
            newAllergies.add("newAllergyTest");
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, newBirthdate, newMedication, newAllergies);

            Person personTest = new Person(firstName, lastName);
            MedicalRecords medicalRecords = new MedicalRecords();
            medicalRecords.setBirthdate(LocalDate.of(2013, 4, 18));
            medicalRecords.addMedication(new Medication("previousMedicationTest"));
            medicalRecords.addAllergy(new Allergy("previousAllergyTest1"));
            medicalRecords.addAllergy(new Allergy("previousAllergyTest2"));
            medicalRecords.addPerson(personTest);

            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            doReturn(null).when(medicalRecordsRepository).save(any());
            // WHEN
            //the tested function addNewMedicalRecords is called
            MedicalRecords result = medicalRecordsService.addNewMedicalRecords(medicalRecordsTest);
            // THEN
            //new information should replace older information for this person's medical records
            assertThat(result.getMedicalId()).isEqualTo(medicalRecords.getMedicalId());
            assertThat(result.getPerson()).isEqualTo(personTest);
            assertThat(result.getBirthdate()).isEqualTo(LocalDate.of(1982, 11, 14));
            assertThat(result.getMedications().size()).isEqualTo(2);
            assertThat(result.getMedications().get(0).getMedicationName()).isEqualTo("newMedicationTest1");
            assertThat(result.getMedications().get(1).getMedicationName()).isEqualTo("newMedicationTest2");
            assertThat(result.getAllergies().size()).isEqualTo(1);
            assertThat(result.getAllergies().get(0).getAllergyName()).isEqualTo("newAllergyTest");
            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN a non-existing person " +
                "WHEN the function addNewMedicalRecords is called  " +
                "THEN new person and new medical records should be created and attached together.")
        void addNewMedicalRecordsWithNonExistingPersonTest() {
            // GIVEN
            //a non-existing person
            String firstName = "fistNameTest";
            String lastName = "lastNameTest";
            String birthdate = "14/11/1982";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            doThrow(ObjectNotFoundException.class).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            doReturn(null).when(medicalRecordsRepository).save(any());
            // WHEN
            //the tested function addNewMedicalRecords is called
            MedicalRecords result = medicalRecordsService.addNewMedicalRecords(medicalRecordsTest);
            // THEN
            //new person and new medical records should be created and attached together
            assertThat(result.getPerson()).isNotNull();
            assertThat(result.getPerson().getFirstName()).isEqualTo(firstName.toUpperCase());
            assertThat(result.getPerson().getLastName()).isEqualTo(lastName.toUpperCase());
            assertThat(result.getBirthdate()).isEqualTo(LocalDate.of(1982, 11, 14));
            assertThat(result.getMedications().size()).isEqualTo(2);
            assertThat(result.getMedications().get(0).getMedicationName()).isEqualTo(medications.get(0));
            assertThat(result.getMedications().get(1).getMedicationName()).isEqualTo(medications.get(1));
            assertThat(result.getAllergies().size()).isEqualTo(1);
            assertThat(result.getAllergies().get(0).getAllergyName()).isEqualTo(allergies.get(0));
            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN missing elements in the medical records to add " +
                "WHEN the function addNewMedicalRecords is called  " +
                "THEN given elements should be added and missing elements should be null.")
        void addNewMedicalRecordsWithMissingElementsTest() {
            // GIVEN
            //missing elements in the medical records to add
            String firstName = "fistNameTest";
            String lastName = "lastNameTest";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            Person personTest = new Person(firstName, lastName);
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO();
            medicalRecordsTest.setFirstName(firstName);
            medicalRecordsTest.setLastName(lastName);
            medicalRecordsTest.setMedications(medications);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            doReturn(null).when(medicalRecordsRepository).save(any());
            // WHEN
            //the tested function addNewMedicalRecords is called
            MedicalRecords result = medicalRecordsService.addNewMedicalRecords(medicalRecordsTest);
            // THEN
            //given elements should be added and missing elements should be null
            assertThat(result.getPerson()).isEqualTo(personTest);
            assertThat(result.getBirthdate()).isNull();
            assertThat(result.getMedications().size()).isEqualTo(2);
            assertThat(result.getMedications().get(0).getMedicationName()).isEqualTo(medications.get(0));
            assertThat(result.getMedications().get(1).getMedicationName()).isEqualTo(medications.get(1));
            assertThat(result.getAllergies().size()).isEqualTo(0);
            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN missing name in the medical records to add " +
                "WHEN the function addNewMedicalRecords is called  " +
                "THEN a NotRightFormatToPostException should be thrown.")
        void addNewMedicalRecordsWithMissingNameTest() {
            // GIVEN
            //missing name in the medical records to add
            String birthdate = "birthdate test";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO();
            medicalRecordsTest.setBirthdate(birthdate);
            medicalRecordsTest.setMedications(medications);
            medicalRecordsTest.setAllergies(allergies);
            doReturn(null).when(medicalRecordsRepository).save(any());
            // WHEN
            //the tested function addNewMedicalRecords is called
            // THEN
            //a NotRightFormatToPostException should be thrown
            assertThrows(NotRightFormatToPostException.class, () -> medicalRecordsService.addNewMedicalRecords(medicalRecordsTest));
            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
            verify(personService, Mockito.times(0)).getPersonById(any());
        }
        @Test
        @DisplayName("GIVEN birthdate not given in right format " +
                "WHEN the function addNewMedicalRecords is called" +
                "THEN a NotRightFormatToPostException should be thrown.")
        void addNewMedicalRecordsNotRightFormatTest() {
            // GIVEN
            //birthdate not given in right format
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String birthdate = "birthdateTest";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            Person personTest = new Person(firstName, lastName);
            MedicalRecords medicalRecords = new MedicalRecords();
            medicalRecords.setBirthdate(LocalDate.of(2013, 4, 18));
            medicalRecords.addMedication(new Medication("previousMedicationTest"));
            medicalRecords.addAllergy(new Allergy("previousAllergyTest1"));
            medicalRecords.addAllergy(new Allergy("previousAllergyTest2"));
            medicalRecords.addPerson(personTest);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function updateMedicalRecords is called
            // THEN
            //a NotRightFormatToPostException should be thrown
            Exception exception = assertThrows(NotRightFormatToPostException.class, () -> medicalRecordsService.addNewMedicalRecords(medicalRecordsTest));
            assertEquals("The birthdate should be given at the format \"dd/MM/yyyy\".\n", exception.getMessage());
            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN birthdate not given in right format " +
                "WHEN the function addNewMedicalRecords is called" +
                "THEN a NotRightFormatToPostException should be thrown.")
        void addNewMedicalRecordsBirthdateMonthToBigFormatTest() {
            // GIVEN
            //no information to update
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String birthdate = "14-51-1982";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            Person personTest = new Person(firstName, lastName);
            MedicalRecords medicalRecords = new MedicalRecords();
            medicalRecords.setBirthdate(LocalDate.of(2013, 4, 18));
            medicalRecords.addMedication(new Medication("previousMedicationTest"));
            medicalRecords.addAllergy(new Allergy("previousAllergyTest1"));
            medicalRecords.addAllergy(new Allergy("previousAllergyTest2"));
            medicalRecords.addPerson(personTest);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function updateMedicalRecords is called
            // THEN
            //a NotTheSamePersonException should be thrown
            Exception exception = assertThrows(NotRightFormatToPostException.class, () -> medicalRecordsService.addNewMedicalRecords(medicalRecordsTest));
            assertEquals("The birthdate should be given at the format \"dd/MM/yyyy\"\nInvalid value for MonthOfYear (valid values 1 - 12): 51", exception.getMessage());
            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }
    }

    @Nested
    @DisplayName("updateMedicalRecords tests:")
    class UpdateMedicalRecordsTest {

        @Test
        @DisplayName("GIVEN an existing person and all medical records information " +
                "WHEN the function updateMedicalRecords is called" +
                "THEN the new medical records should be added to this person's medical records.")
        void updateMedicalRecordsTest() {
            // GIVEN
            //an existing person and all medical records information
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String birthdate = "14/11/1982";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            Person personTest = new Person(firstName, lastName);
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            doReturn(null).when(medicalRecordsRepository).save(any());
            // WHEN
            //the tested function updateMedicalRecords is called
            String result = medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest);
            // THEN
            //the new medical records should be added to this person's medical records
            assertThat(result).isEqualTo("The medical records about the person FIRSTNAMETEST LASTNAMETEST have been updated.\n");
            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN an existing person and some medical records information " +
                "WHEN the function updateMedicalRecords is called" +
                "THEN the given medical records should be added to this person's medical records.")
        void updateMedicalRecordsSomeInformationTest() {
            // GIVEN
            //an existing person and some medical records information
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            Person personTest = new Person(firstName, lastName);
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO();
            medicalRecordsTest.setFirstName(firstName);
            medicalRecordsTest.setLastName(lastName);
            medicalRecordsTest.setMedications(medications);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            doReturn(null).when(medicalRecordsRepository).save(any());
            // WHEN
            //the tested function updateMedicalRecords is called
            String result = medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest);
            // THEN
            //the given medical records should be added to this person's medical records
            assertThat(result).isEqualTo("The medical records about the person FIRSTNAMETEST LASTNAMETEST have been updated.\n");
            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN a non-existing person " +
                "WHEN the function updateMedicalRecords is called" +
                "THEN a PersonNotFoundException should be thrown.")
        void updateMedicalRecordsNonExistingPersonTest() {
            // GIVEN
            //a non-existing person
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String birthdate = "birthdate test";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function updateMedicalRecords is called
            // THEN
            //a PersonNotFoundException should be thrown
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest));
            assertEquals("error message", exception.getMessage());
            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN a different name to update " +
                "WHEN the function updateMedicalRecords is called" +
                "THEN a NotTheSamePersonException should be thrown.")
        void updateMedicalRecordsNotTheSameNameTest() {
            // GIVEN
            //a different name to update
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String firstName2 = "firstNameTest2";
            String lastName2 = "lastNameTest2";
            String birthdate = "birthdate test";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            Person personTest = new Person(firstName2, lastName2);
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            doReturn(personTest).when(personService).getPersonById(firstName2.toUpperCase() + lastName2.toUpperCase());
            // WHEN
            //the tested function updateMedicalRecords is called
            // THEN
            //a NotTheSamePersonException should be thrown
            Exception exception = assertThrows(NotTheSamePersonException.class, () -> medicalRecordsService.updateMedicalRecord(firstName2, lastName2, medicalRecordsTest));
            assertEquals("It's not possible to update first name or last name.", exception.getMessage());
            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName2.toUpperCase() + lastName2.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN no information to update " +
                "WHEN the function updateMedicalRecords is called" +
                "THEN a NotTheSamePersonException should be thrown.")
        void updateMedicalRecordsNothingToUpdateTest() {
            // GIVEN
            //no information to update
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            Person personTest = new Person(firstName, lastName);
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO();
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function updateMedicalRecords is called
            // THEN
            //a NotTheSamePersonException should be thrown
            Exception exception = assertThrows(NothingToUpdateException.class, () -> medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest));
            assertEquals("The medical records about the person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " wasn't updated, there was no element to update.\n", exception.getMessage());
            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN birthdate not given in right format " +
                "WHEN the function updateMedicalRecords is called" +
                "THEN a NotRightFormatToPostException should be thrown.")
        void updateMedicalRecordsNotRightFormatTest() {
            // GIVEN
            //birthdate not given in right format
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String birthdate = "birthdateTest";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            Person personTest = new Person(firstName, lastName);
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function updateMedicalRecords is called
            // THEN
            //a NotRightFormatToPostException should be thrown
            Exception exception = assertThrows(NotRightFormatToPostException.class, () -> medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest));
            assertEquals("The birthdate should be given at the format \"dd/MM/yyyy\".\n", exception.getMessage());
            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN birthdate not given in right format " +
                "WHEN the function updateMedicalRecords is called" +
                "THEN a NotRightFormatToPostException should be thrown.")
        void updateMedicalRecordsBirthdateMonthToBigFormatTest() {
            // GIVEN
            //no information to update
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String birthdate = "14-51-1982";
            List<String> medications = new ArrayList<>();
            medications.add("medicationTest1");
            medications.add("medicationTest2");
            List<String> allergies = new ArrayList<>();
            allergies.add("allergyTest");
            Person personTest = new Person(firstName, lastName);
            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function updateMedicalRecords is called
            // THEN
            //a NotTheSamePersonException should be thrown
            Exception exception = assertThrows(NotRightFormatToPostException.class, () -> medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest));
            assertEquals("The birthdate should be given at the format \"dd/MM/yyyy\"\nInvalid value for MonthOfYear (valid values 1 - 12): 51", exception.getMessage());
            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }
    }

    @Nested
    @DisplayName("deleteMedicalRecords tests:")
    class DeleteMedicalRecordsTest {

        @Test
        @DisplayName("GIVEN an existing person with medical records  " +
                "WHEN the function deleteMedicalRecords is called" +
                "THEN a message indicating that this person's medical records are suppressed should be returned.")
        void deleteMedicalRecordsTest() {
            // GIVEN
            //an existing person with medical records
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            Person personTest = new Person(firstName, lastName);
            MedicalRecords medicalRecordsTest = new MedicalRecords();
            medicalRecordsTest.addPerson(personTest);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            doNothing().when(medicalRecordsRepository).delete(medicalRecordsTest);
            // WHEN
            //the tested function deleteMedicalRecords is called
            String result = medicalRecordsService.deleteMedicalRecords(firstName, lastName);
            // THEN
            //a message indicating that this person's medical records are suppressed should be returned
            assertThat(result).isEqualTo("The medical records about the person FIRSTNAMETEST LASTNAMETEST have been deleted.\n");
            verify(medicalRecordsRepository, Mockito.times(1)).delete(medicalRecordsTest);
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
        }

        @Test
        @DisplayName("GIVEN an existing person without medical records " +
                "WHEN  the function deleteMedicalRecords is called" +
                "THEN a NothingToDeleteException should be thrown.")
        void deleteMedicalRecordsNothingToDeleteTest() {
            // GIVEN
            //an existing person without medical records
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            Person personTest = new Person(firstName, lastName);
            doReturn(personTest).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function deleteMedicalRecords is called
            // THEN
            // a NothingToDeleteException should be thrown
            Exception exception = assertThrows(NothingToDeleteException.class, () -> medicalRecordsService.deleteMedicalRecords(firstName, lastName));
            assertEquals("The medical records about the person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " weren't found, so it couldn't have been deleted.\n", exception.getMessage());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            verify(medicalRecordsRepository, Mockito.times(0)).delete(any());
        }

        @Test
        @DisplayName("GIVEN an existing person without medical records " +
                "WHEN  the function deleteMedicalRecords is called" +
                "THEN a NothingToDeleteException should be thrown.")
        void deleteMedicalRecordsNonExistingPersonTest() {
            // GIVEN
            //a non-existing person
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(personService).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            // WHEN
            //the tested function deleteMedicalRecords is called
            // THEN
            //a PersonNotFoundException should be thrown
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> medicalRecordsService.deleteMedicalRecords(firstName, lastName));
            assertEquals("error message", exception.getMessage());
            verify(personService, Mockito.times(1)).getPersonById(firstName.toUpperCase() + lastName.toUpperCase());
            verify(medicalRecordsRepository, Mockito.times(0)).delete(any());
        }
    }
}

