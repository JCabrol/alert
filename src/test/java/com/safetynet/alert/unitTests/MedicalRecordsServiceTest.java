//package com.safetynet.alert.unitTests;
//
//import com.safetynet.alert.exceptions.*;
//import com.safetynet.alert.model.*;
//import com.safetynet.alert.model.DTO.MedicalRecordDTO;
//import com.safetynet.alert.repository.MedicalRecordsRepository;
//import com.safetynet.alert.service.MedicalRecordsService;
//import com.safetynet.alert.service.PersonService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
//
//
//@Tag("MedicalRecordsTests")
//@Slf4j
//@ActiveProfiles("test")
//@DirtiesContext(classMode = AFTER_CLASS)
//@SpringBootTest(classes = MedicalRecordsService.class)
//public class MedicalRecordsServiceTest {
//
//    @Autowired
//    private MedicalRecordsService medicalRecordsService;
//
//    @MockBean
//    private MedicalRecordsRepository medicalRecordsRepository;
//
//    @MockBean
//    private PersonService personService;
//
//    @Nested
//    @DisplayName("getter tests:")
//    class GetMedicalRecordsRepositoryTest {
//
//        @DisplayName("GIVEN a medicalRecordsRepository set to a medicalRecordsService" +
//                "WHEN the getter getMedicalrecordsRepository() is called " +
//                "THEN it returns this medicalRecordsRepository.")
//        @Test
//        public void getMedicalRecordsRepositoryTest() {
//            //GIVEN
//            medicalRecordsService.setMedicalRecordsRepository(medicalRecordsRepository);
//            //WHEN
//            MedicalRecordsRepository medicalRecordsRepository2 = medicalRecordsService.getMedicalRecordsRepository();
//            //THEN
//            assertThat(medicalRecordsRepository2).isEqualTo(medicalRecordsRepository);
//        }
//
//        @DisplayName("GIVEN a PersonService set to a medicalRecordsService" +
//                "WHEN the getter getPersonService() is called " +
//                "THEN it returns this PersonService.")
//        @Test
//        public void getPersonServiceTest() {
//            //GIVEN
//            medicalRecordsService.setPersonService(personService);
//            //WHEN
//            PersonService personService2 = medicalRecordsService.getPersonService();
//            //THEN
//            assertThat(personService2).isEqualTo(personService);
//        }
//
//    }
//
//    @Nested
//    @DisplayName("getMedicalRecords() tests:")
//    class getMedicalRecordsTest {
//
//        @DisplayName("GIVEN an existing list of medicalRecords " +
//                "WHEN function getMedicalRecords is called " +
//                "THEN the same list of medicalRecords should be returned.")
//        @Test
//        public void getMedicalRecordsWhenNonEmptyTest() {
//            //GIVEN
//            //an existing list of medicalRecords
//            ArrayList<MedicalRecords> AllMedicalRecordsTest = new ArrayList<>();
//            for (int numberOfMedicalRecordsTest = 0; numberOfMedicalRecordsTest < 3; numberOfMedicalRecordsTest++) {
//                MedicalRecords medicalRecords = new MedicalRecords();
//                medicalRecords.setMedicalId(numberOfMedicalRecordsTest);
//                medicalRecords.addAllergy(new Allergy("allergy test " + numberOfMedicalRecordsTest));
//                medicalRecords.addMedication(new Medication("medication test " + numberOfMedicalRecordsTest));
//                medicalRecords.setBirthdate("01/01/200" + numberOfMedicalRecordsTest);
//                medicalRecords.addPerson(new Person("firstNameTest" + numberOfMedicalRecordsTest, "lastNameTest" + numberOfMedicalRecordsTest));
//                AllMedicalRecordsTest.add(medicalRecords);
//            }
//            when(medicalRecordsRepository.findAll()).thenReturn(AllMedicalRecordsTest);
//            //WHEN
//            //the tested function getMedicalRecords is called
//            List<MedicalRecords> result = medicalRecordsService.getMedicalRecords();
//            //THEN
//            //the same list of medicalRecords should be returned
//            assertThat(result.size()).isEqualTo(3);
//            assertThat(result.get(0).getMedicalId()).isEqualTo(0);
//            assertThat(result.get(0).getBirthdate()).isEqualTo("01/01/2000");
//            assertThat(result.get(0).getPerson().getFirstName()).isEqualTo("firstNameTest0");
//            assertThat(result.get(0).getPerson().getLastName()).isEqualTo("lastNameTest0");
//            assertThat(result.get(0).toString()).contains("allergy test 0", "medication test 0");
//            assertThat(result.get(1).getMedicalId()).isEqualTo(1);
//            assertThat(result.get(1).getBirthdate()).isEqualTo("01/01/2001");
//            assertThat(result.get(1).getPerson().getFirstName()).isEqualTo("firstNameTest1");
//            assertThat(result.get(1).getPerson().getLastName()).isEqualTo("lastNameTest1");
//            assertThat(result.get(1).toString()).contains("allergy test 1", "medication test 1");
//            assertThat(result.get(2).getMedicalId()).isEqualTo(2);
//            assertThat(result.get(2).getBirthdate()).isEqualTo("01/01/2002");
//            assertThat(result.get(2).getPerson().getFirstName()).isEqualTo("firstNameTest2");
//            assertThat(result.get(2).getPerson().getLastName()).isEqualTo("lastNameTest2");
//            assertThat(result.get(2).toString()).contains("allergy test 2", "medication test 2");
//            verify(medicalRecordsRepository, Mockito.times(1)).findAll();
//        }
//
//        @DisplayName("GIVEN an empty list of medicalRecords " +
//                "WHEN function getMedicalRecords is called " +
//                "THEN an EmptyMedicalrecordsException should be thrown.")
//        @Test
//        public void getMedicalRecordsWhenEmptyTest() {
//            //GIVEN
//            //an empty list of medical records
//            when(medicalRecordsRepository.findAll()).thenReturn(new ArrayList<>());
//            //WHEN
//            // the tested function getMedicalRecords is called
//            //THEN
//            // an emptyMedicalrecordsException should be thrown
//            assertThrows(EmptyMedicalRecordsException.class, () -> medicalRecordsService.getMedicalRecords());
//            verify(medicalRecordsRepository, Mockito.times(1)).findAll();
//        }
//    }
//
//
//    @Nested
//    @DisplayName("getMedicalrecordsByName() tests:")
//    class GetMedicalRecordsByNameTest {
//
//        @Test
//        @DisplayName("GIVEN existing medicalRecords " +
//                "WHEN the function getMedicalRecordsByName is called " +
//                "THEN the medicalRecords should be found.")
//        void getMedicalRecordsByNameTest() {
//            // GIVEN
//            //existing medicalRecords
//            MedicalRecords medicalRecords = new MedicalRecords();
//            medicalRecords.setMedicalId(1);
//            medicalRecords.addAllergy(new Allergy("allergy test"));
//            medicalRecords.addMedication(new Medication("medication test"));
//            medicalRecords.setBirthdate("01/01/2001");
//            Person personTest = new Person("firstNameTest", "lastNameTest");
//            medicalRecords.addPerson(personTest);
//            doReturn(personTest).when(personService).getPersonByName("firstNameTest", "lastNameTest");
//            // WHEN
//            //the tested function getMedicalRecordsByName is called
//            MedicalRecords returnedMedicalRecords = medicalRecordsService.getMedicalRecordsByName("firstNameTest", "lastNameTest");
//            // THEN
//            //the medicalRecords should be found
//            assertThat(returnedMedicalRecords).isEqualTo(medicalRecords);
//            verify(personService, Mockito.times(1)).getPersonByName("firstNameTest", "lastNameTest");
//        }
//
//
//        @Test
//        @DisplayName("GIVEN non-existing medicalRecords " +
//                "WHEN the function getMedicalRecordsByName is called " +
//                "THEN a MedicalRecordsNotFoundException should be thrown.")
//        void getMedicalRecordsByNameNotExistingTest() {
//            // GIVEN
//            //non-existing medicalRecords
//            Person personTest = new Person("firstNameTest", "lastNameTest");
//            doReturn(personTest).when(personService).getPersonByName("firstNameTest", "lastNameTest");
//            //WHEN
//            //the tested function getMedicalRecordsByName is called
//            //THEN
//            //aa MedicalRecordsNotFoundException should be thrown
//            assertThrows(MedicalRecordsNotFoundException.class, () -> medicalRecordsService.getMedicalRecordsByName("firstNameTest", "lastNameTest"));
//            verify(personService, Mockito.times(1)).getPersonByName("firstNameTest", "lastNameTest");
//        }
//    }
//
//    @Nested
//    @DisplayName("addNewMedicalRecords tests:")
//    class AddNewMedicalRecordsTest {
//
//        @Test
//        @DisplayName("GIVEN an existing person with no medical records " +
//                "WHEN the function addNewMedicalRecords is called  " +
//                "THEN new medical records should be created and attached to this person.")
//        void addNewMedicalRecordsWithExistingPersonWithNoMedicalRecordsTest() {
//            // GIVEN
//            //an existing person with no medical records
//            String firstName = "fistNameTest";
//            String lastName = "lastNameTest";
//            String birthdate = "birthdate test";
//            List<String> medications = new ArrayList<>();
//            medications.add("medicationTest1");
//            medications.add("medicationTest2");
//            List<String> allergies = new ArrayList<>();
//            allergies.add("allergyTest");
//            Person personTest = new Person(firstName, lastName);
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
//            doReturn(personTest).when(personService).getPersonByName(firstName, lastName);
//            doReturn(null).when(medicalRecordsRepository).save(any());
//            // WHEN
//            //the tested function addNewMedicalRecords is called
//            MedicalRecords result = medicalRecordsService.addNewMedicalRecords(medicalRecordsTest);
//            // THEN
//            //new medical records should be created and attached to this person
//            assertThat(result.getPerson()).isEqualTo(personTest);
//            assertThat(result.getBirthdate()).isEqualTo(birthdate);
//            assertThat(result.getMedications().size()).isEqualTo(2);
//            assertThat(result.getMedications().get(0).getMedicationName()).isEqualTo(medications.get(0));
//            assertThat(result.getMedications().get(1).getMedicationName()).isEqualTo(medications.get(1));
//            assertThat(result.getAllergies().size()).isEqualTo(1);
//            assertThat(result.getAllergies().get(0).getAllergyName()).isEqualTo(allergies.get(0));
//            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName, lastName);
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing person with medical records " +
//                "WHEN the function addNewMedicalRecords is called  " +
//                "THEN new informations should replace older information for this person's medical records.")
//        void addNewMedicalRecordsWithExistingPersonWithMedicalRecordsTest() {
//            // GIVEN
//            //an existing person with medical records
//            String firstName = "fistNameTest";
//            String lastName = "lastNameTest";
//            String newBirthdate = "new birthdate test";
//            List<String> newMedication = new ArrayList<>();
//            newMedication.add("newMedicationTest1");
//            newMedication.add("newMedicationTest2");
//            List<String> newAllergies = new ArrayList<>();
//            newAllergies.add("newAllergyTest");
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, newBirthdate, newMedication, newAllergies);
//
//            Person personTest = new Person(firstName, lastName);
//            MedicalRecords medicalRecords = new MedicalRecords();
//            String previousBirthdate = "previous birthdate test";
//            medicalRecords.setBirthdate(previousBirthdate);
//            medicalRecords.addMedication(new Medication("previousMedicationTest"));
//            medicalRecords.addAllergy(new Allergy("previousAllergyTest1"));
//            medicalRecords.addAllergy(new Allergy("previousAllergyTest2"));
//            medicalRecords.addPerson(personTest);
//
//            doReturn(personTest).when(personService).getPersonByName(firstName, lastName);
//            doReturn(null).when(medicalRecordsRepository).save(any());
//            // WHEN
//            //the tested function addNewMedicalRecords is called
//            MedicalRecords result = medicalRecordsService.addNewMedicalRecords(medicalRecordsTest);
//            // THEN
//            //new information should replace older information for this person's medical records
//            assertThat(result.getMedicalId()).isEqualTo(medicalRecords.getMedicalId());
//            assertThat(result.getPerson()).isEqualTo(personTest);
//            assertThat(result.getBirthdate()).isEqualTo(newBirthdate);
//            assertThat(result.getMedications().size()).isEqualTo(2);
//            assertThat(result.getMedications().get(0).getMedicationName()).isEqualTo("newMedicationTest1");
//            assertThat(result.getMedications().get(1).getMedicationName()).isEqualTo("newMedicationTest2");
//            assertThat(result.getAllergies().size()).isEqualTo(1);
//            assertThat(result.getAllergies().get(0).getAllergyName()).isEqualTo("newAllergyTest");
//            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName, lastName);
//        }
//
//        @Test
//        @DisplayName("GIVEN a non-existing person " +
//                "WHEN the function addNewMedicalRecords is called  " +
//                "THEN new person and new medical records should be created and attached together.")
//        void addNewMedicalRecordsWithNonExistingPersonTest() {
//            // GIVEN
//            //a non-existing person
//            String firstName = "fistNameTest";
//            String lastName = "lastNameTest";
//            String birthdate = "birthdate test";
//            List<String> medications = new ArrayList<>();
//            medications.add("medicationTest1");
//            medications.add("medicationTest2");
//            List<String> allergies = new ArrayList<>();
//            allergies.add("allergyTest");
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
//            doThrow(PersonNotFoundException.class).when(personService).getPersonByName(firstName, lastName);
//            doReturn(null).when(medicalRecordsRepository).save(any());
//            // WHEN
//            //the tested function addNewMedicalRecords is called
//            MedicalRecords result = medicalRecordsService.addNewMedicalRecords(medicalRecordsTest);
//            // THEN
//            //new person and new medical records should be created and attached together
//            assertThat(result.getPerson()).isNotNull();
//            assertThat(result.getPerson().getFirstName()).isEqualTo(firstName.toUpperCase());
//            assertThat(result.getPerson().getLastName()).isEqualTo(lastName.toUpperCase());
//            assertThat(result.getBirthdate()).isEqualTo(birthdate);
//            assertThat(result.getMedications().size()).isEqualTo(2);
//            assertThat(result.getMedications().get(0).getMedicationName()).isEqualTo(medications.get(0));
//            assertThat(result.getMedications().get(1).getMedicationName()).isEqualTo(medications.get(1));
//            assertThat(result.getAllergies().size()).isEqualTo(1);
//            assertThat(result.getAllergies().get(0).getAllergyName()).isEqualTo(allergies.get(0));
//            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName, lastName);
//        }
//
//        @Test
//        @DisplayName("GIVEN missing elements in the medical records to add " +
//                "WHEN the function addNewMedicalRecords is called  " +
//                "THEN given elements should be added and missing elements should be null.")
//        void addNewMedicalRecordsWithMissingElementsTest() {
//            // GIVEN
//            //missing elements in the medical records to add
//            String firstName = "fistNameTest";
//            String lastName = "lastNameTest";
//            List<String> medications = new ArrayList<>();
//            medications.add("medicationTest1");
//            medications.add("medicationTest2");
//            Person personTest = new Person(firstName, lastName);
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO();
//            medicalRecordsTest.setFirstName(firstName);
//            medicalRecordsTest.setLastName(lastName);
//            medicalRecordsTest.setMedications(medications);
//            doReturn(personTest).when(personService).getPersonByName(firstName, lastName);
//            doReturn(null).when(medicalRecordsRepository).save(any());
//            // WHEN
//            //the tested function addNewMedicalRecords is called
//            MedicalRecords result = medicalRecordsService.addNewMedicalRecords(medicalRecordsTest);
//            // THEN
//            //given elements should be added and missing elements should be null
//            assertThat(result.getPerson()).isEqualTo(personTest);
//            assertThat(result.getBirthdate()).isEqualTo(null);
//            assertThat(result.getMedications().size()).isEqualTo(2);
//            assertThat(result.getMedications().get(0).getMedicationName()).isEqualTo(medications.get(0));
//            assertThat(result.getMedications().get(1).getMedicationName()).isEqualTo(medications.get(1));
//            assertThat(result.getAllergies().size()).isEqualTo(0);
//            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName, lastName);
//        }
//
//        @Test
//        @DisplayName("GIVEN missing name in the medical records to add " +
//                "WHEN the function addNewMedicalRecords is called  " +
//                "THEN a NotRightFormatToPostException should be thrown.")
//        void addNewMedicalRecordsWithMissingNameTest() {
//            // GIVEN
//            //missing name in the medical records to add
//            String birthdate = "birthdate test";
//            List<String> medications = new ArrayList<>();
//            medications.add("medicationTest1");
//            medications.add("medicationTest2");
//            List<String> allergies = new ArrayList<>();
//            allergies.add("allergyTest");
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO();
//            medicalRecordsTest.setBirthdate(birthdate);
//            medicalRecordsTest.setMedications(medications);
//            medicalRecordsTest.setAllergies(allergies);
//            doReturn(null).when(medicalRecordsRepository).save(any());
//            // WHEN
//            //the tested function addNewMedicalRecords is called
//            // THEN
//            //a NotRightFormatToPostException should be thrown
//            assertThrows(NotRightFormatToPostException.class, () -> medicalRecordsService.addNewMedicalRecords(medicalRecordsTest));
//            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
//            verify(personService, Mockito.times(0)).getPersonByName(any(), any());
//        }
//    }
//
//    @Nested
//    @DisplayName("updateMedicalRecords tests:")
//    class UpdateMedicalRecordsTest {
//
//        @Test
//        @DisplayName("GIVEN an existing person and all medical records information " +
//                "WHEN the function updateMedicalRecords is called" +
//                "THEN the new medical records should be added to this person's medical records.")
//        void updateMedicalRecordsTest() {
//            // GIVEN
//            //an existing person and all medical records information
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            String birthdate = "birthdate test";
//            List<String> medications = new ArrayList<>();
//            medications.add("medicationTest1");
//            medications.add("medicationTest2");
//            List<String> allergies = new ArrayList<>();
//            allergies.add("allergyTest");
//            Person personTest = new Person(firstName, lastName);
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
//            doReturn(personTest).when(personService).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            doReturn(null).when(medicalRecordsRepository).save(any());
//            // WHEN
//            //the tested function updateMedicalRecords is called
//            String result = medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest);
//            // THEN
//            //the new medical records should be added to this person's medical records
//            assertThat(result).isEqualTo("The medical records about the person FIRSTNAMETEST LASTNAMETEST have been updated with following items :\n"
//                    + "- the birthdate : birthdate test\n"
//                    + "- the medications : [medicationTest1, medicationTest2]\n"
//                    + "- the allergies : [allergyTest]\n");
//            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing person and some medical records information " +
//                "WHEN the function updateMedicalRecords is called" +
//                "THEN the given medical records should be added to this person's medical records.")
//        void updateMedicalRecordsSomeInformationTest() {
//            // GIVEN
//            //an existing person and some medical records information
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            List<String> medications = new ArrayList<>();
//            medications.add("medicationTest1");
//            medications.add("medicationTest2");
//            Person personTest = new Person(firstName, lastName);
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO();
//            medicalRecordsTest.setFirstName(firstName);
//            medicalRecordsTest.setLastName(lastName);
//            medicalRecordsTest.setMedications(medications);
//            doReturn(personTest).when(personService).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            doReturn(null).when(medicalRecordsRepository).save(any());
//            // WHEN
//            //the tested function updateMedicalRecords is called
//            String result = medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest);
//            // THEN
//            //the given medical records should be added to this person's medical records
//            assertThat(result).isEqualTo("The medical records about the person FIRSTNAMETEST LASTNAMETEST have been updated with following items :\n"
//                    + "- the medications : [medicationTest1, medicationTest2]\n");
//            verify(medicalRecordsRepository, Mockito.times(1)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//        }
//
//        @Test
//        @DisplayName("GIVEN a non-existing person " +
//                "WHEN the function updateMedicalRecords is called" +
//                "THEN a PersonNotFoundException should be thrown.")
//        void updateMedicalRecordsNonExistingPersonTest() {
//            // GIVEN
//            //a non-existing person
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            String birthdate = "birthdate test";
//            List<String> medications = new ArrayList<>();
//            medications.add("medicationTest1");
//            medications.add("medicationTest2");
//            List<String> allergies = new ArrayList<>();
//            allergies.add("allergyTest");
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
//            doThrow(PersonNotFoundException.class).when(personService).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            // WHEN
//            //the tested function updateMedicalRecords is called
//            // THEN
//            //a PersonNotFoundException should be thrown
//            assertThrows(PersonNotFoundException.class, () -> medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest));
//            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//        }
//
//        @Test
//        @DisplayName("GIVEN a different name to update " +
//                "WHEN the function updateMedicalRecords is called" +
//                "THEN a NotTheSamePersonException should be thrown.")
//        void updateMedicalRecordsNotTheSameNameTest() {
//            // GIVEN
//            //a different name to update
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            String firstName2 = "firstNameTest2";
//            String lastName2 = "lastNameTest2";
//            String birthdate = "birthdate test";
//            List<String> medications = new ArrayList<>();
//            medications.add("medicationTest1");
//            medications.add("medicationTest2");
//            List<String> allergies = new ArrayList<>();
//            allergies.add("allergyTest");
//            Person personTest = new Person(firstName2, lastName2);
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
//            doReturn(personTest).when(personService).getPersonByName(firstName2.toUpperCase(), lastName2.toUpperCase());
//            // WHEN
//            //the tested function updateMedicalRecords is called
//            // THEN
//            //a NotTheSamePersonException should be thrown
//            assertThrows(NotTheSamePersonException.class, () -> medicalRecordsService.updateMedicalRecord(firstName2, lastName2, medicalRecordsTest));
//            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName2.toUpperCase(), lastName2.toUpperCase());
//        }
//
//        @Test
//        @DisplayName("GIVEN no information to update " +
//                "WHEN the function updateMedicalRecords is called" +
//                "THEN a NotTheSamePersonException should be thrown.")
//        void updateMedicalRecordsNothingToUpdateTest() {
//            // GIVEN
//            //no information to update
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            Person personTest = new Person(firstName, lastName);
//            MedicalRecordDTO medicalRecordsTest = new MedicalRecordDTO();
//            doReturn(personTest).when(personService).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            // WHEN
//            //the tested function updateMedicalRecords is called
//            // THEN
//            //a NotTheSamePersonException should be thrown
//            assertThrows(NothingToUpdateException.class, () -> medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordsTest));
//            verify(medicalRecordsRepository, Mockito.times(0)).save(any());
//            verify(personService, Mockito.times(1)).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//        }
//    }
//
//    @Nested
//    @DisplayName("deleteMedicalRecords tests:")
//    class DeleteMedicalRecordsTest {
//
//        @Test
//        @DisplayName("GIVEN an existing person with medical records  " +
//                "WHEN the function deleteMedicalRecords is called" +
//                "THEN a message indicating that this person's medical records are suppressed should be returned.")
//        void deleteMedicalRecordsTest() {
//            // GIVEN
//            //an existing person with medical records
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            Person personTest = new Person(firstName, lastName);
//            MedicalRecords medicalRecordsTest = new MedicalRecords();
//            medicalRecordsTest.addPerson(personTest);
//            doReturn(personTest).when(personService).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            doNothing().when(medicalRecordsRepository).delete(medicalRecordsTest);
//            // WHEN
//            //the tested function deleteMedicalRecords is called
//            String result = medicalRecordsService.deleteMedicalRecords(firstName, lastName);
//            // THEN
//            //a message indicating that this person's medical records are suppressed should be returned
//            assertThat(result).isEqualTo("The medical records about the person FIRSTNAMETEST LASTNAMETEST have been deleted.\n");
//            verify(medicalRecordsRepository, Mockito.times(1)).delete(medicalRecordsTest);
//            verify(personService, Mockito.times(1)).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing person without medical records " +
//                "WHEN  the function deleteMedicalRecords is called" +
//                "THEN a NothingToDeleteException should be thrown.")
//        void deleteMedicalRecordsNothingToDeleteTest() {
//            // GIVEN
//            //an existing person without medical records
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            Person personTest = new Person(firstName, lastName);
//            doReturn(personTest).when(personService).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            // WHEN
//            //the tested function deleteMedicalRecords is called
//            // THEN
//            // a NothingToDeleteException should be thrown
//            assertThrows(NothingToDeleteException.class, () -> medicalRecordsService.deleteMedicalRecords(firstName, lastName));
//            verify(personService, Mockito.times(1)).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            verify(medicalRecordsRepository, Mockito.times(0)).delete(any());
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing person without medical records " +
//                "WHEN  the function deleteMedicalRecords is called" +
//                "THEN a NothingToDeleteException should be thrown.")
//        void deleteMedicalRecordsNonExistingPersonTest() {
//            // GIVEN
//            //a non-existing person
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            doThrow(PersonNotFoundException.class).when(personService).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            // WHEN
//            //the tested function deleteMedicalRecords is called
//            // THEN
//            //a PersonNotFoundException should be thrown
//            assertThrows(PersonNotFoundException.class, () -> medicalRecordsService.deleteMedicalRecords(firstName, lastName));
//            verify(personService, Mockito.times(1)).getPersonByName(firstName.toUpperCase(), lastName.toUpperCase());
//            verify(medicalRecordsRepository, Mockito.times(0)).delete(any());
//        }
//    }
//
//    @Nested
//    @DisplayName("MedicalRecordsToString tests:")
//    class MedicalRecordsToString {
//        @Test
//        @DisplayName("GIVEN a list of medical records " +
//                "WHEN the function medicalRecordsToString is called " +
//                "THEN a String with all informations should be returned.")
//        void medicalRecordsToStringTest() {
//            // GIVEN
//            //a list of medical records
//            MedicalRecords medicalRecords1 = new MedicalRecords();
//            MedicalRecords medicalRecords2 = new MedicalRecords();
//            MedicalRecords medicalRecords3 = new MedicalRecords();
//            medicalRecords1.addPerson(new Person("firstNameTest1", "lastNameTest1"));
//            medicalRecords2.addPerson(new Person("firstNameTest2", "lastNameTest2"));
//            medicalRecords3.addPerson(new Person("firstNameTest3", "lastNameTest3"));
//            List<MedicalRecords> medicalRecordsList = new ArrayList<>();
//            medicalRecordsList.add(medicalRecords1);
//            medicalRecordsList.add(medicalRecords2);
//            medicalRecordsList.add(medicalRecords3);
//            // WHEN
//            //the tested function firestationToString is called with this list
//            String result = medicalRecordsService.medicalRecordsToString(medicalRecordsList);
//            // THEN
//            //the right string with all information should be returned
//            assertThat(result).contains("Firestation n°1 :\n- address test\n- address test 2\n");
//            assertThat(result).contains("Firestation n°2 :\n- address test 3\n");
//            assertThat(result).contains("Firestation n°3 :\nThere are no addresses attached to this firestation.\n");
//        }
//    }
//
//}
//
