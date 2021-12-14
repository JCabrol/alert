package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.AttachedAddress;
import com.safetynet.alert.model.Firestation;
import com.safetynet.alert.model.MappingFirestationAddress;
import com.safetynet.alert.repository.FirestationRepository;
import com.safetynet.alert.service.FirestationService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;


@Tag("FirestationTests")
@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest(classes = FirestationService.class)
public class FirestationServiceTest {

    @Autowired
    private FirestationService firestationService;

    @MockBean
    private FirestationRepository firestationRepository;

    @Nested
    @DisplayName("getter tests:")
    class GetFirestationRepositoryTest {

        @DisplayName("GIVEN firestations returned by firestationRepository " +
                "WHEN function getFirestations() is called " +
                "THEN it returns the same list of firestations.")
        @Test
        public void getFirestationRepositoryTest() {
            //GIVEN
            //a list containing 3 firestations has to be returned when the personRepository mock is called with the function findAll

            firestationService.setFirestationRepository(firestationRepository);
            //WHEN
            //the tested function getFirestations is called
            FirestationRepository firestationRepository2=firestationService.getFirestationRepository();
            //THEN
            //the result should contain 3 firestations and should be the same as the list created first
            assertThat(firestationRepository2).isEqualTo(firestationRepository);
        }
    }

    @Nested
    @DisplayName("getFirestations() tests:")
    class getFirestationsTest {

        @DisplayName("GIVEN firestations returned by firestationRepository " +
                "WHEN function getFirestations() is called " +
                "THEN it returns the same list of firestations.")
        @Test
        public void getFirestationsWhenNonEmptyTest() {
            //GIVEN
            //a list containing 3 firestations has to be returned when the personRepository mock is called with the function findAll
            ArrayList<Firestation> AllFirestationsTest = new ArrayList<>();
            for (int numberOfFirestationsTest = 0; numberOfFirestationsTest < 3; numberOfFirestationsTest++) {
                Firestation firestation = new Firestation();
                firestation.setStationId(numberOfFirestationsTest);
                firestation.addAttachedAddress(new AttachedAddress("new address " + numberOfFirestationsTest));
                AllFirestationsTest.add(firestation);
            }
            when(firestationRepository.findAll()).thenReturn(AllFirestationsTest);
            //WHEN
            //the tested function getFirestations is called
            List<Firestation> result = firestationService.getFirestations();
            //THEN
            //the result should contain 3 firestations and should be the same as the list created first
            assertThat(result.size()).isEqualTo(3);
            assertThat(result.get(0).getStationId()).isEqualTo(0);
            assertThat(result.get(0).toString()).contains("new address 0");
            assertThat(result.get(1).getStationId()).isEqualTo(1);
            assertThat(result.get(1).toString()).contains("new address 1");
            assertThat(result.get(2).getStationId()).isEqualTo(2);
            assertThat(result.get(2).toString()).contains("new address 2");
            verify(firestationRepository, Mockito.times(1)).findAll();
        }

        @DisplayName("GIVEN an empty list returned by firestationRepository " +
                "WHEN function getFirestations() is called " +
                "THEN an EmptyFirestationsException is thrown.")
        @Test
        public void getFirestationsWhenEmptyTest() {
            //GIVEN
            //an empty list of firestations
            when(firestationRepository.findAll()).thenReturn(new ArrayList<>());
            //WHEN
            // the function getFirestations() is called
            //THEN
            // an EmptyFirestationsException is thrown
            assertThrows(EmptyFirestationsException.class, () -> firestationService.getFirestations());
            verify(firestationRepository, Mockito.times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getFirestationById() tests:")
    class GetFirestationByIdTest {

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function getFirestationById() is called " +
                "THEN the firestation should be found.")
        void getFirestationByIdTest() {
            // GIVEN
            //An existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            Firestation returnedFirestation = firestationService.getFirestationById(1);
            // THEN
            //the firestation should be found
            assertThat(returnedFirestation).isEqualTo(firestation);
            verify(firestationRepository, Mockito.times(1)).findById(1);
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation " +
                "WHEN the function getFirestationById() is called " +
                "THEN a FirestationNotFoundException should be thrown.")
        void getFirestationByIdNotExistingTest() {
            // GIVEN
            //a non-existing firestation
            doReturn(Optional.empty()).when(firestationRepository).findById(1);
            //WHEN
            //the tested function getFirestationById is called with parameter id = 1
            //THEN
            //a FirestationNotFoundException should be thrown
            assertThrows(FirestationNotFoundException.class, () -> firestationService.getFirestationById(1));
            verify(firestationRepository, Mockito.times(1)).findById(1);
        }
    }

    @Nested
    @DisplayName("getFirestationByAddress() tests:")
    class GetFirestationByAddressTest {

        @Test
        @DisplayName("GIVEN an existing address " +
                "WHEN the function getFirestationByAddress() is called " +
                "THEN the firestation should be found.")
        void getFirestationByAddressTest() {
            // GIVEN
            //an existing firestation with the researched address
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("Address test"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function  getPersonByName is called with FirstNameTest and LastNameTest
            List<Firestation> returnedFirestations = firestationService.getFirestationByAddress("Address test");
            // THEN
            //the person should be found
            assertThat(returnedFirestations).isEqualTo(firestations);
            verify(firestationRepository, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN an existing address written in different case " +
                "WHEN the function getFirestationByAddress() is called " +
                "THEN the firestation should be found.")
        void getFirestationByAddressWhenCaseIsDifferentTest() {
            // GIVEN
            //an existing firestation with the researched address
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function  getPersonByName is called with FirstNameTest and LastNameTest
            List<Firestation> returnedFirestations = firestationService.getFirestationByAddress("ADDRESS TEST");
            // THEN
            //the person should be found
            assertThat(returnedFirestations).isEqualTo(firestations);
            verify(firestationRepository, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN an existing address written with spaces " +
                "WHEN the function getFirestationByAddress() is called " +
                "THEN the firestation should be found.")
        void getFirestationByAddressWhenThereAreSpacesTest() {
            // GIVEN
            //an existing firestation with the researched address
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function  getPersonByName is called with FirstNameTest and LastNameTest
            List<Firestation> returnedFirestations = firestationService.getFirestationByAddress(" address  test  ");
            // THEN
            //the person should be found
            assertThat(returnedFirestations).isEqualTo(firestations);
            verify(firestationRepository, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN an existing address present in several firestations" +
                "WHEN the function getFirestationByAddress() is called " +
                "THEN firestations containing this address should be found.")
        void getFirestationByAddressWhenSeveralFirestationsTest() {
            // GIVEN
            //an existing firestation with the researched address
            Firestation firestation = new Firestation();
            Firestation firestation2 = new Firestation();
            Firestation firestation3 = new Firestation();
            firestation.setStationId(1);
            firestation2.setStationId(2);
            firestation3.setStationId(3);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            firestation.addAttachedAddress(new AttachedAddress("address test 2"));
            firestation2.addAttachedAddress(new AttachedAddress("address test"));
            firestation3.addAttachedAddress(new AttachedAddress("address test 2"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            firestations.add(firestation2);
            firestations.add(firestation3);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function  getPersonByName is called with FirstNameTest and LastNameTest
            List<Firestation> returnedFirestations = firestationService.getFirestationByAddress("address test");
            // THEN
            //the person should be found
            assertThat(returnedFirestations.size()).isEqualTo(2);
            assertThat(returnedFirestations).contains(firestation);
            assertThat(returnedFirestations).contains(firestation2);
            assertThat(returnedFirestations).doesNotContain(firestation3);
            verify(firestationRepository, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN a non-existing address " +
                "WHEN the function getFirestationByAddress() is called " +
                "THEN a FirestationNotFoundException should be thrown.")
        void getFirestationByAddressNotExistingTest() {
            // GIVEN
            //a non-existing address
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            firestation.addAttachedAddress(new AttachedAddress("address test 2"));
            Firestation firestation2 = new Firestation();
            firestation2.setStationId(2);
            firestation2.addAttachedAddress(new AttachedAddress("address test"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            firestations.add(firestation2);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function getPersonByName is called
            // THEN
            //a PersonNotFoundException should be thrown
            assertThrows(FirestationNotFoundException.class, () -> firestationService.getFirestationByAddress("address test 3"));
            verify(firestationRepository, Mockito.times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("FirestationToString tests:")
    class FirestationsToString {
        @Test
        @DisplayName("GIVEN a list of firestations " +
                "WHEN the function firestationsToString is called " +
                "THEN a String with all informations should be returned.")
        void firestationsToStringTest() {
            // GIVEN
            //a list of firestations
            Firestation firestation = new Firestation();
            Firestation firestation2 = new Firestation();
            Firestation firestation3 = new Firestation();
            firestation.setStationId(1);
            firestation2.setStationId(2);
            firestation3.setStationId(3);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            firestation.addAttachedAddress(new AttachedAddress("address test 2"));
            firestation2.addAttachedAddress(new AttachedAddress("address test 3"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            firestations.add(firestation2);
            firestations.add(firestation3);
            // WHEN
            //the tested function firestationToString is called with this list
            String result = firestationService.firestationsToString(firestations);
            // THEN
            //the right string with all information should be returned
            assertThat(result).contains("Firestation n°1 :\n- address test\n- address test 2\n");
            assertThat(result).contains("Firestation n°2 :\n- address test 3\n");
            assertThat(result).contains("Firestation n°3 :\nThere are no addresses attached to this firestation.\n");
        }
    }

    @Nested
    @DisplayName("addNewMapping tests:")
    class AddNewMappingTest {

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function addNewMapping() is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void addNewMappingWithExistingFirestationTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.addNewMapping(new MappingFirestationAddress(1, "address test"));
            // THEN
            //the firestation should be found
            assertThat(result).isEqualTo("The firestation number 1 was already existing,\n" +
                    "the address \"address test\" have been added to this firestation.");
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function addNewMapping() is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void addNewMappingWithNonExistingFirestationTest() {
            // GIVEN
            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
            when(firestationRepository.findById(1)).thenReturn(Optional.empty());
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.addNewMapping(new MappingFirestationAddress(1, "address test"));
            // THEN
            //the firestation should be found
            assertThat(result).isEqualTo("The firestation number 1 have been created,\n" +
                    " the address \"address test\" have been added to this firestation.");
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function addNewMapping() is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void addMappingAlreadyExistingTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            when(firestationRepository.findById(1)).thenReturn(Optional.of(firestation));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            // THEN
            //the firestation should be found
            assertThrows(MappingAlreadyExistingException.class, () -> firestationService.addNewMapping(new MappingFirestationAddress(1, "address test")));
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function addNewMapping() is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void addMappingNotCorrectInformationTest() {
            // GIVEN
            //an existing firestation
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            // THEN
            //the firestation should be found
            MappingFirestationAddress mapping = new MappingFirestationAddress();
            assertThrows(NotRightFormatToPostException.class, () -> firestationService.addNewMapping(mapping));
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(firestationRepository, Mockito.times(0)).findById(anyInt());
        }
    }

    @Nested
    @DisplayName("deleteFirestation tests:")
    class DeleteFirestationTest {

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void deleteFirestationTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            doNothing().when(firestationRepository).deleteById(1);
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            firestationService.deleteFirestation(1);
            // THEN
            //the firestation should be found
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(1)).deleteById(1);
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void deleteFirestationNonExistingTest() {
            // GIVEN
            //an existing firestation
            doReturn(Optional.empty()).when(firestationRepository).findById(1);
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            // THEN
            //the firestation should be found
            assertThrows(NothingToDeleteException.class, () -> firestationService.deleteFirestation(1));
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).deleteById(1);
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void deleteFirestationNonEmpty() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            // THEN
            //the firestation should be found
            assertThrows(FirestationNonEmptyException.class, () -> firestationService.deleteFirestation(1));
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).deleteById(1);
        }
    }

    @Nested
    @DisplayName("deleteAddress tests:")
    class DeleteAddressTest {

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void deleteAddressTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            doReturn(firestation).when(firestationRepository).save(any(Firestation.class));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.deleteAddress("address test");
            // THEN
            //the firestation should be found
            assertThat(result).contains("from firestation number 1.");
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(1)).save(any(Firestation.class));
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void deleteAddressNotSameCaseTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("ADDRESS TEST"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            doReturn(firestation).when(firestationRepository).save(any(Firestation.class));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.deleteAddress("address test");
            // THEN
            //the firestation should be found
            assertThat(result).contains("from firestation number 1.");
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(1)).save(any(Firestation.class));
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void deleteAddressWithSpacesTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("   address     test    "));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            doReturn(firestation).when(firestationRepository).save(any(Firestation.class));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.deleteAddress("address test");
            // THEN
            //the firestation should be found
            assertThat(result).contains("from firestation number 1.");
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(1)).save(any(Firestation.class));
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void deleteAddressSeveralFirestationsTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation1 = new Firestation();
            Firestation firestation2 = new Firestation();
            Firestation firestation3 = new Firestation();

            firestation1.setStationId(1);
            firestation2.setStationId(2);
            firestation3.setStationId(3);

            firestation1.addAttachedAddress(new AttachedAddress("address test"));
            firestation2.addAttachedAddress(new AttachedAddress("address test 2"));
            firestation3.addAttachedAddress(new AttachedAddress("address test 3"));
            firestation3.addAttachedAddress(new AttachedAddress("address test"));

            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation1);
            firestations.add(firestation2);
            firestations.add(firestation3);
            doReturn(firestations).when(firestationRepository).findAll();
            doReturn(firestation1).when(firestationRepository).save(any(Firestation.class));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.deleteAddress("address test");
            // THEN
            //the firestation should be found
            assertThat(result).contains("from firestation number 1.");
            assertThat(result).doesNotContain("from firestation number 2.");
            assertThat(result).contains("from firestation number 3.");
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(2)).save(any(Firestation.class));
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void deleteAddressNonExistingTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation1 = new Firestation();
            Firestation firestation2 = new Firestation();
            Firestation firestation3 = new Firestation();

            firestation1.setStationId(1);
            firestation2.setStationId(2);
            firestation3.setStationId(3);

            firestation1.addAttachedAddress(new AttachedAddress("address test"));
            firestation2.addAttachedAddress(new AttachedAddress("address test 2"));
            firestation3.addAttachedAddress(new AttachedAddress("address test 3"));
            firestation3.addAttachedAddress(new AttachedAddress("address test"));

            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation1);
            firestations.add(firestation2);
            firestations.add(firestation3);
            doReturn(firestations).when(firestationRepository).findAll();
            doReturn(firestation1).when(firestationRepository).save(any(Firestation.class));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            // THEN
            //the firestation should be found
            assertThrows(NothingToDeleteException.class, () -> firestationService.deleteAddress("address test 4"));
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(0)).save(any(Firestation.class));
        }
    }

    @Nested
    @DisplayName("isAttachedAddressToFirestation tests:")
    class IsAttachedAddressToFirestationTest {

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void isAttachedAddressToFirestationTrueTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            boolean result = firestationService.isAddressAttachedToFireStation(firestation,"address test");
            // THEN
            //the firestation should be found
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("GIVEN an existing firestation " +
                "WHEN the function deleteFirestation is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void isAttachedAddressToFirestationFalseTest() {
            // GIVEN
            //an existing firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAttachedAddress(new AttachedAddress("address test"));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            boolean result = firestationService.isAddressAttachedToFireStation(firestation,"address test 2");
            // THEN
            //the firestation should be found
            assertThat(result).isFalse();
        }
    }

}
