package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Address;
import com.safetynet.alert.model.DTO.FirestationDTO;
import com.safetynet.alert.model.Firestation;
import com.safetynet.alert.model.DTO.MappingFirestationAddressDTO;
import com.safetynet.alert.repository.AddressRepository;
import com.safetynet.alert.repository.FirestationRepository;
import com.safetynet.alert.service.AddressService;
import com.safetynet.alert.service.AddressServiceImpl;
import com.safetynet.alert.service.FirestationService;
import com.safetynet.alert.service.FirestationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.matchers.JUnitMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;


@Tag("FirestationTests")
@Slf4j
@ActiveProfiles("test")
//@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest
public class FirestationServiceTest {

    @Autowired
    private FirestationService firestationService;

    @Mock
    private FirestationRepository firestationRepository;

    @Mock
    private AddressRepository addressRepository;

//  @Autowired
//    private AddressService addressService;

    @Nested
    @DisplayName("getFirestations() tests:")
    class getFirestationsTest {

        @DisplayName("GIVEN a list of firestations returned by firestationRepository " +
                "WHEN function getFirestations() is called " +
                "THEN it returns the same list of firestations.")
        @Test
        public void getFirestationsWhenNonEmptyTest() {
            //GIVEN
            //a list containing 3 firestations
            ArrayList<Firestation> AllFirestationsTest = new ArrayList<>();
            for (int numberOfFirestationsTest = 0; numberOfFirestationsTest < 3; numberOfFirestationsTest++) {
                Firestation firestation = new Firestation();
                firestation.setStationId(numberOfFirestationsTest);
                firestation.addAddress(new Address("new address " + numberOfFirestationsTest,"1111"+numberOfFirestationsTest,"cityTest"));
                AllFirestationsTest.add(firestation);
            }
            when(firestationRepository.findAll()).thenReturn(AllFirestationsTest);
            //WHEN
            //the function getFirestations is called
            List<FirestationDTO> result = firestationService.getFirestations();
            //THEN
            //the result should be a list of 3 firestationDTO, containing correct information
            assertThat(result.size()).isEqualTo(3);
            assertThat(result.get(0).getFirestationNumber()).isEqualTo(0);
            assertThat(result.get(0).getAddressesList().get(0)).isEqualTo("new address 0 - 11110 cityTest");
            assertThat(result.get(0).getAddressesList().size()).isEqualTo(1);
            assertThat(result.get(1).getFirestationNumber()).isEqualTo(1);
            assertThat(result.get(1).getAddressesList().get(0)).isEqualTo("new address 1 - 11111 cityTest");
            assertThat(result.get(1).getAddressesList().size()).isEqualTo(1);
            assertThat(result.get(2).getFirestationNumber()).isEqualTo(2);
            assertThat(result.get(2).getAddressesList().get(0)).isEqualTo("new address 2 - 11112 cityTest");
            assertThat(result.get(2).getAddressesList().size()).isEqualTo(1);
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
    @DisplayName("getFirestationDTO() tests:")
    class GetFirestationDTOTest {

        @Test
        @DisplayName("GIVEN an existing address attached to a firestation " +
                "WHEN the function getFirestationDTO() is called " +
                "THEN the firestation should be found.")
        void getFirestationDTOExistingAddressTest() {
            // GIVEN
            //an existing firestation with the researched address
            Address address = new Address("address test", "12345", "cityTest");
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAddress(address);
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function  getFirestationDTO is called with this address
            FirestationDTO returnedFirestation = firestationService.getFirestationDTO("address test");
            // THEN
            //the firestation should be found and the corresponding firestationDTO should be returned
            assertThat(returnedFirestation.getFirestationNumber()).isEqualTo(1);
            assertThat(returnedFirestation.getAddressesList().size()).isEqualTo(1);
            assertThat(returnedFirestation.getAddressesList().get(0)).isEqualTo("address test - 12345 cityTest");
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(0)).findById(1);
        }

        @Test
        @DisplayName("GIVEN an existing address written in different case " +
                "WHEN the function getFirestationDTO() is called " +
                "THEN the firestation should be found.")
        void getFirestationDTOExistingAddressDifferentCaseTest() {
            // GIVEN
            //an existing firestation with the researched address
            Address address = new Address("address test", "12345", "cityTest");
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAddress(address);
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function getFirestationDTO is called with the same address written in different case
            FirestationDTO returnedFirestation = firestationService.getFirestationDTO("Address TEST");
            // THEN
            //the firestation should be found and the corresponding firestationDTO should be returned
            assertThat(returnedFirestation.getFirestationNumber()).isEqualTo(1);
            assertThat(returnedFirestation.getAddressesList().size()).isEqualTo(1);
            assertThat(returnedFirestation.getAddressesList().get(0)).isEqualTo("address test - 12345 cityTest");
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(0)).findById(1);
        }

        @Test
        @DisplayName("GIVEN an existing address written with spaces " +
                "WHEN the function getFirestationDTO() is called " +
                "THEN the firestation should be found.")
        void getFirestationDTOExistingAddressWithSpacesTest() {
            // GIVEN
            //an existing firestation with the researched address
            Address address = new Address("address test", "12345", "cityTest");
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAddress(address);
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function getFirestationDTO is called with this address written with spaces
            FirestationDTO returnedFirestation = firestationService.getFirestationDTO("  address    test   ");
            // THEN
            //the firestation should be found and the corresponding firestationDTO should be returned
            assertThat(returnedFirestation.getFirestationNumber()).isEqualTo(1);
            assertThat(returnedFirestation.getAddressesList().size()).isEqualTo(1);
            assertThat(returnedFirestation.getAddressesList().get(0)).isEqualTo("address test - 12345 cityTest");
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(0)).findById(1);
        }

        @Test
        @DisplayName("GIVEN a non-existing address " +
                "WHEN the function getFirestationDTO() is called " +
                "THEN a FirestationNotFoundException should be thrown.")
        void getFirestationDTOAddressNotExistingTest() {
            // GIVEN
            //a non-existing address
            Address address1 = new Address("address test 1", "12345", "cityTest");
            Address address2 = new Address("address test 2", "12345", "cityTest");
            Address address3 = new Address("address test 3", "12345", "cityTest");
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAddress(address1);
            firestation.addAddress(address2);
            Firestation firestation2 = new Firestation();
            firestation2.setStationId(2);
            firestation2.addAddress(address3);
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation);
            firestations.add(firestation2);
            doReturn(firestations).when(firestationRepository).findAll();
            // WHEN
            //the tested function getFirestationDTO is called
            // THEN
            //a FirestationNotFoundException should be thrown
            assertThrows(FirestationNotFoundException.class, () -> firestationService.getFirestationDTO("address test 4"));
            verify(firestationRepository, Mockito.times(1)).findAll();
            verify(firestationRepository, Mockito.times(0)).findById(1);
        }

        @Test
        @DisplayName("GIVEN an existing firestation number " +
                "WHEN the function getFirestationDTO() is called " +
                "THEN the firestation should be found.")
        void getFirestationDTOExistingId() {
            // GIVEN
            //an existing firestation with id number 1
            Address address = new Address("address test", "12345", "cityTest");
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAddress(address);
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            // WHEN
            //the tested function getFirestationDTO is called with "1""
            FirestationDTO returnedFirestation = firestationService.getFirestationDTO("1");
            // THEN
            //the firestation should be found and the corresponding firestationDTO should be returned
            assertThat(returnedFirestation.getFirestationNumber()).isEqualTo(1);
            assertThat(returnedFirestation.getAddressesList().size()).isEqualTo(1);
            assertThat(returnedFirestation.getAddressesList().get(0)).isEqualTo("address test - 12345 cityTest");
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).findAll();
        }
        @Test
        @DisplayName("GIVEN a non-existing firestation number " +
                "WHEN the function getFirestationDTO() is called " +
                "THEN a FirestationNotFoundException should be thrown with the expected error message.")
        void getFirestationDTONonExistingId() {
            // GIVEN
            //a non-existing firestation
            doReturn(Optional.empty()).when(firestationRepository).findById(1);
            // WHEN
            //the tested function getFirestationDTO is called
            // THEN
            //a FirestationNotFoundException should be thrown with the expected error message
            Exception exception = assertThrows(FirestationNotFoundException.class, () -> firestationService.getFirestationDTO("1"));
            assertEquals("The firestation with Id number 1 was not found.\n",exception.getMessage());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).findAll();
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
            firestation.addAddress(new Address("address test" ,"12345","cityTest"));
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            Firestation returnedFirestation = firestationService.getFirestationById(1);
            // THEN
            //the firestation should be found
            assertThat(returnedFirestation.getStationId()).isEqualTo(1);
            assertThat(returnedFirestation.getAttachedAddresses().size()).isEqualTo(1);
            assertThat(returnedFirestation.getAttachedAddresses().get(0).getStreet()).isEqualTo("address test");
            assertThat(returnedFirestation.getAttachedAddresses().get(0).getZip()).isEqualTo("12345");
            assertThat(returnedFirestation.getAttachedAddresses().get(0).getCity()).isEqualTo("cityTest");
            verify(firestationRepository, Mockito.times(1)).findById(1);
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation " +
                "WHEN the function getFirestationById() is called " +
                "THEN a FirestationNotFoundException should be thrown with the expected error message.")
        void getFirestationByIdNotExistingTest() {
            // GIVEN
            //a non-existing firestation
            doReturn(Optional.empty()).when(firestationRepository).findById(1);
            //WHEN
            //the tested function getFirestationById is called
            //THEN
            //a FirestationNotFoundException should be thrown with the expected error message
            Exception exception = assertThrows(FirestationNotFoundException.class, () -> firestationService.getFirestationById(1));
            assertEquals("The firestation with Id number 1 was not found.\n",exception.getMessage());
            verify(firestationRepository, Mockito.times(1)).findById(1);
        }
    }

    @Nested
    @DisplayName("addNewMapping tests:")
    class AddNewMappingTest {

        @Test
        @DisplayName("GIVEN an existing firestation and a new address " +
                "WHEN the function addNewMapping is called " +
                "THEN a message should be returned, indicating that the address have been added to the firestation.")
        void addNewMappingWithExistingFirestationAndNewAddressTest() {
            // GIVEN
            //an existing firestation and a new address
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
//            doThrow(AddressNotFoundException.class).when(addressService).getAddress(any());
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.addNewMapping(new MappingFirestationAddressDTO(1, "address test","11111","city test"));
            // THEN
            //the firestation should be found
            assertThat(result).isEqualTo("The address \"address test - 11111 CITY TEST\" have been added to the firestation number 1.\n");
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).existsById(any());

        }

        @Test
        @DisplayName("GIVEN a non-existing firestation with a new address" +
                "WHEN the function addNewMapping() is called with this firestation " +
                "THEN a message should be returned, indicating that the firestation was created and the address have been added to this firestation.")
        void addNewMappingWithNonExistingFirestationTest() {
            // GIVEN
            //a non-existing firestation with a new address
            doReturn(Optional.empty()).when(firestationRepository).findById(1);
            doReturn(null).when(addressRepository).findAll();
//            doThrow(AddressNotFoundException.class).when(addressService).getAddress(any());
            // WHEN
            //the function addNewMapping() is called with this firestation
            String result = firestationService.addNewMapping(new MappingFirestationAddressDTO(1, "address test","11111","city test"));
            // THEN
            // a message should be returned, indicating that the firestation was created and the address have been added to this firestation.
            assertThat(result).isEqualTo("The firestation number 1 have been created,\n" +
                    " the address \"address test - 11111 CITY TEST\" have been added to the firestation number 1.\n");
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).existsById(any());
        }

        @Test
        @DisplayName("GIVEN an address already attached to a firestation" +
                "WHEN the function addNewMapping() is called  " +
                "THEN a MappingAlreadyExistingException should be thrown with the expected error message")
        void addNewMappingWithAddressAlreadyAttachedTest() {
            // GIVEN
            //an address already attached to a firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            Address address = new Address();
            address.setStreet("address test");
            address.setZip("11111");
            firestation.addAddress(address);
            //doReturn().when(addressRepository).findAll();
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);

            MappingFirestationAddressDTO mapping = new MappingFirestationAddressDTO(1, "address test","11111","CITYTEST");
            // WHEN
            //the function addNewMapping() is called
            // THEN
            // a MappingAlreadyExistingException should be thrown with the expected error message
            Exception exception = assertThrows(MappingAlreadyExistingException.class, () -> firestationService.addNewMapping(mapping));
            assertEquals("The address \"address test - 11111 city test\" was already attached to the firestation number 1.\n",exception.getMessage());
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).existsById(any());
        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function addNewMapping() is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void addMappingAlreadyExistingTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
//            firestation.addAttachedAddress(new AttachedAddress("address test"));
//            when(firestationRepository.findById(1)).thenReturn(Optional.of(firestation));
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            // THEN
//            //the firestation should be found
//            assertThrows(MappingAlreadyExistingException.class, () -> firestationService.addNewMapping(new MappingFirestationAddressDTO(1, "address test")));
//            verify(firestationRepository, Mockito.times(0)).save(any());
//            verify(firestationRepository, Mockito.times(1)).findById(1);
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function addNewMapping() is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void addMappingNotCorrectInformationTest() {
//            // GIVEN
//            //an existing firestation
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            // THEN
//            //the firestation should be found
//            MappingFirestationAddressDTO mapping = new MappingFirestationAddressDTO();
//            assertThrows(NotRightFormatToPostException.class, () -> firestationService.addNewMapping(mapping));
//            verify(firestationRepository, Mockito.times(0)).save(any());
//            verify(firestationRepository, Mockito.times(0)).findById(anyInt());
//        }
    }

//    @Nested
//    @DisplayName("deleteFirestation tests:")
//    class DeleteFirestationTest {
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void deleteFirestationTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
//            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
//            doNothing().when(firestationRepository).deleteById(1);
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            firestationService.deleteFirestation(1);
//            // THEN
//            //the firestation should be found
//            verify(firestationRepository, Mockito.times(1)).findById(1);
//            verify(firestationRepository, Mockito.times(1)).deleteById(1);
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void deleteFirestationNonExistingTest() {
//            // GIVEN
//            //an existing firestation
//            doReturn(Optional.empty()).when(firestationRepository).findById(1);
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            // THEN
//            //the firestation should be found
//            assertThrows(NothingToDeleteException.class, () -> firestationService.deleteFirestation(1));
//            verify(firestationRepository, Mockito.times(1)).findById(1);
//            verify(firestationRepository, Mockito.times(0)).deleteById(1);
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void deleteFirestationNonEmpty() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
//            firestation.addAttachedAddress(new AttachedAddress("address test"));
//            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            // THEN
//            //the firestation should be found
//            assertThrows(FirestationNonEmptyException.class, () -> firestationService.deleteFirestation(1));
//            verify(firestationRepository, Mockito.times(1)).findById(1);
//            verify(firestationRepository, Mockito.times(0)).deleteById(1);
//        }
//    }
//
//    @Nested
//    @DisplayName("deleteAddress tests:")
//    class DeleteAddressTest {
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void deleteAddressTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
//            firestation.addAttachedAddress(new AttachedAddress("address test"));
//            List<Firestation> firestations = new ArrayList<>();
//            firestations.add(firestation);
//            doReturn(firestations).when(firestationRepository).findAll();
//            doReturn(firestation).when(firestationRepository).save(any(Firestation.class));
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            String result = firestationService.deleteAddress("address test");
//            // THEN
//            //the firestation should be found
//            assertThat(result).contains("from firestation number 1.");
//            verify(firestationRepository, Mockito.times(1)).findAll();
//            verify(firestationRepository, Mockito.times(1)).save(any(Firestation.class));
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void deleteAddressNotSameCaseTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
//            firestation.addAttachedAddress(new AttachedAddress("ADDRESS TEST"));
//            List<Firestation> firestations = new ArrayList<>();
//            firestations.add(firestation);
//            doReturn(firestations).when(firestationRepository).findAll();
//            doReturn(firestation).when(firestationRepository).save(any(Firestation.class));
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            String result = firestationService.deleteAddress("address test");
//            // THEN
//            //the firestation should be found
//            assertThat(result).contains("from firestation number 1.");
//            verify(firestationRepository, Mockito.times(1)).findAll();
//            verify(firestationRepository, Mockito.times(1)).save(any(Firestation.class));
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void deleteAddressWithSpacesTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
//            firestation.addAttachedAddress(new AttachedAddress("   address     test    "));
//            List<Firestation> firestations = new ArrayList<>();
//            firestations.add(firestation);
//            doReturn(firestations).when(firestationRepository).findAll();
//            doReturn(firestation).when(firestationRepository).save(any(Firestation.class));
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            String result = firestationService.deleteAddress("address test");
//            // THEN
//            //the firestation should be found
//            assertThat(result).contains("from firestation number 1.");
//            verify(firestationRepository, Mockito.times(1)).findAll();
//            verify(firestationRepository, Mockito.times(1)).save(any(Firestation.class));
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void deleteAddressSeveralFirestationsTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation1 = new Firestation();
//            Firestation firestation2 = new Firestation();
//            Firestation firestation3 = new Firestation();
//
//            firestation1.setStationId(1);
//            firestation2.setStationId(2);
//            firestation3.setStationId(3);
//
//            firestation1.addAttachedAddress(new AttachedAddress("address test"));
//            firestation2.addAttachedAddress(new AttachedAddress("address test 2"));
//            firestation3.addAttachedAddress(new AttachedAddress("address test 3"));
//            firestation3.addAttachedAddress(new AttachedAddress("address test"));
//
//            List<Firestation> firestations = new ArrayList<>();
//            firestations.add(firestation1);
//            firestations.add(firestation2);
//            firestations.add(firestation3);
//            doReturn(firestations).when(firestationRepository).findAll();
//            doReturn(firestation1).when(firestationRepository).save(any(Firestation.class));
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            String result = firestationService.deleteAddress("address test");
//            // THEN
//            //the firestation should be found
//            assertThat(result).contains("from firestation number 1.");
//            assertThat(result).doesNotContain("from firestation number 2.");
//            assertThat(result).contains("from firestation number 3.");
//            verify(firestationRepository, Mockito.times(1)).findAll();
//            verify(firestationRepository, Mockito.times(2)).save(any(Firestation.class));
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void deleteAddressNonExistingTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation1 = new Firestation();
//            Firestation firestation2 = new Firestation();
//            Firestation firestation3 = new Firestation();
//
//            firestation1.setStationId(1);
//            firestation2.setStationId(2);
//            firestation3.setStationId(3);
//
//            firestation1.addAttachedAddress(new AttachedAddress("address test"));
//            firestation2.addAttachedAddress(new AttachedAddress("address test 2"));
//            firestation3.addAttachedAddress(new AttachedAddress("address test 3"));
//            firestation3.addAttachedAddress(new AttachedAddress("address test"));
//
//            List<Firestation> firestations = new ArrayList<>();
//            firestations.add(firestation1);
//            firestations.add(firestation2);
//            firestations.add(firestation3);
//            doReturn(firestations).when(firestationRepository).findAll();
//            doReturn(firestation1).when(firestationRepository).save(any(Firestation.class));
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            // THEN
//            //the firestation should be found
//            assertThrows(NothingToDeleteException.class, () -> firestationService.deleteAddress("address test 4"));
//            verify(firestationRepository, Mockito.times(1)).findAll();
//            verify(firestationRepository, Mockito.times(0)).save(any(Firestation.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("isAttachedAddressToFirestation tests:")
//    class IsAttachedAddressToFirestationTest {
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void isAttachedAddressToFirestationTrueTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
//            firestation.addAttachedAddress(new AttachedAddress("address test"));
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            boolean result = firestationService.isAddressAttachedToFireStation(firestation,"address test");
//            // THEN
//            //the firestation should be found
//            assertThat(result).isTrue();
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing firestation " +
//                "WHEN the function deleteFirestation is called with this firestation " +
//                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
//        void isAttachedAddressToFirestationFalseTest() {
//            // GIVEN
//            //an existing firestation
//            Firestation firestation = new Firestation();
//            firestation.setStationId(1);
//            firestation.addAttachedAddress(new AttachedAddress("address test"));
//            // WHEN
//            //the tested function  getFirestationById is called with parameter id = 1
//            boolean result = firestationService.isAddressAttachedToFireStation(firestation,"address test 2");
//            // THEN
//            //the firestation should be found
//            assertThat(result).isFalse();
//        }
//    }

}
