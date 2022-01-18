package com.safetynet.alert.unitTests;


import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Address;
import com.safetynet.alert.model.DTO.FirestationDTO;
import com.safetynet.alert.model.DTO.MappingFirestationAddressDTO;
import com.safetynet.alert.model.Firestation;
import com.safetynet.alert.repository.FirestationRepository;
import com.safetynet.alert.service.AddressService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;


@Tag("FirestationTests")
@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest
public class FirestationServiceTest {

    @Autowired
    private FirestationService firestationService;

    @MockBean
    private FirestationRepository firestationRepository;

    @MockBean
    private AddressService addressService;

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
                "THEN an EmptyObjectException is thrown.")
        @Test
        public void getFirestationsWhenEmptyTest() {
            //GIVEN
            //an empty list of firestations
            when(firestationRepository.findAll()).thenReturn(new ArrayList<>());
            //WHEN
            // the function getFirestations() is called
            //THEN
            // an EmptyObjectException is thrown
            assertThrows(EmptyObjectException.class, () -> firestationService.getFirestations());
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
            assertThrows(ObjectNotFoundException.class, () -> firestationService.getFirestationDTO("address test 4"));
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
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> firestationService.getFirestationDTO("1"));
            assertEquals("The firestation with Id number 1 was not found.\n", exception.getMessage());
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
            firestation.addAddress(new Address("address test", "12345", "cityTest"));
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
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> firestationService.getFirestationById(1));
            assertEquals("The firestation with Id number 1 was not found.\n", exception.getMessage());
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
            doThrow(ObjectNotFoundException.class).when(addressService).getAddress(any(), any(), any());
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.addNewMapping(new MappingFirestationAddressDTO(1, "address test", "11111", "city test"));
            // THEN
            //the firestation should be found
            assertThat(result).isEqualTo("The address \"address test - 11111 CITY TEST\" have been added to the firestation number 1.\n");
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(addressService, Mockito.times(1)).getAddress(any(), any(), any());

        }

        @Test
        @DisplayName("GIVEN a non-existing firestation with a new address" +
                "WHEN the function addNewMapping() is called with this firestation " +
                "THEN a message should be returned, indicating that the firestation was created and the address have been added to this firestation.")
        void addNewMappingWithNonExistingFirestationTest() {
            // GIVEN
            //a non-existing firestation with a new address
            doReturn(Optional.empty()).when(firestationRepository).findById(1);
            doThrow(ObjectNotFoundException.class).when(addressService).getAddress(any(), any(), any());
            // WHEN
            //the function addNewMapping() is called with this firestation
            String result = firestationService.addNewMapping(new MappingFirestationAddressDTO(1, "address test", "11111", "city test"));
            // THEN
            // a message should be returned, indicating that the firestation was created and the address have been added to this firestation.
            assertThat(result).isEqualTo("The firestation number 1 have been created,\n" +
                    " the address \"address test - 11111 CITY TEST\" have been added to the firestation number 1.\n");
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(addressService, Mockito.times(1)).getAddress(any(), any(), any());
        }

        @Test
        @DisplayName("GIVEN an address already attached to a firestation" +
                "WHEN the function addNewMapping() is called  " +
                "THEN an ObjectAlreadyExistingException should be thrown with the expected error message")
        void addNewMappingWithAddressAlreadyAttachedTest() {
            // GIVEN
            //an address already attached to a firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            Address address = new Address();
            address.setStreet("address test");
            address.setZip("11111");
            address.setCity("CITYTEST");
            firestation.addAddress(address);
            doReturn(address).when(addressService).getAddress("address test", "11111", "CITYTEST");
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);

            MappingFirestationAddressDTO mapping = new MappingFirestationAddressDTO(1, "address test", "11111", "CITYTEST");
            // WHEN
            //the function addNewMapping() is called
            // THEN
            // a ObjectAlreadyExistingException should be thrown with the expected error message
            Exception exception = assertThrows(ObjectAlreadyExistingException.class, () -> firestationService.addNewMapping(mapping));
            assertEquals("The address \"address test - 11111 CITYTEST\" was already attached to the firestation number 1.\n", exception.getMessage());
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(firestationRepository, Mockito.times(0)).findById(1);
            verify(addressService, Mockito.times(1)).getAddress("address test", "11111", "CITYTEST");
        }

        @Test
        @DisplayName("GIVEN an address already attached to another firestation" +
                "WHEN the function addNewMapping() is called  " +
                "THEN an ObjectAlreadyExistingException should be thrown with the expected error message")
        void addNewMappingWithAddressAlreadyAttachedToAnotherFirestationTest() {
            // GIVEN
            //an address already attached to a firestation
            Firestation firestation1 = new Firestation();
            firestation1.setStationId(1);
            Address address = new Address();
            address.setStreet("address test");
            address.setZip("11111");
            address.setCity("CITYTEST");
            firestation1.addAddress(address);
            Firestation firestation2 = new Firestation();
            firestation2.setStationId(2);
            doReturn(address).when(addressService).getAddress("address test", "11111", "CITYTEST");
            doReturn(Optional.of(firestation2)).when(firestationRepository).findById(2);

            MappingFirestationAddressDTO mappingFirestationAddressDTO = new MappingFirestationAddressDTO(2, "address test", "11111", "CITYTEST");
            // WHEN
            //the function addNewMapping() is called
            // THEN
            // a MappingAlreadyExistingException should be thrown with the expected error message
            Exception exception = assertThrows(ObjectAlreadyExistingException.class, () -> firestationService.addNewMapping(mappingFirestationAddressDTO));
            assertEquals("The address \"address test - 11111 CITYTEST\" is already attached to the firestation number 1,\n"
                    + "so it can't be attached to the firestation number 2.\n"
                    + "If you want to change an address' mapping, please update it.\n", exception.getMessage());
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(firestationRepository, Mockito.times(0)).findById(1);
            verify(addressService, Mockito.times(1)).getAddress("address test", "11111", "CITYTEST");
        }

        @Test
        @DisplayName("GIVEN an existing firestation and a non-existing address " +
                "WHEN the function addNewMapping() is called with this firestation " +
                "THEN the returned message should indicate that the firestation was existing and the address have been added to this firestation.")
        void addMappingAlreadyExistingTest() {
            // GIVEN
            //an existing firestation and a non-existing address
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            MappingFirestationAddressDTO mappingFirestationAddressDTO = new MappingFirestationAddressDTO(1, "address test", "11111", "city test");
            doThrow(ObjectNotFoundException.class).when(addressService).getAddress(any(), any(), any());
            when(firestationRepository.findById(1)).thenReturn(Optional.of(firestation));
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.addNewMapping(mappingFirestationAddressDTO);
            // THEN
            //the firestation should be found
            assertThat(result).isEqualTo("The address \"address test - 11111 CITY TEST\" have been added to the firestation number 1.\n");
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(addressService, Mockito.times(1)).getAddress(any(), any(), any());
        }

        @Test
        @DisplayName("GIVEN missing information " +
                "WHEN the function addNewMapping() is called " +
                "THEN a \"NotRightFormatToPostException\" is thrown with the expected error message.")
        void addMappingMissingInformationTest() {
            // GIVEN
            //missing information
            MappingFirestationAddressDTO mapping = new MappingFirestationAddressDTO();
            // WHEN
            //the function addNewMapping() is called
            // THEN
            //a "NotRightFormatToPostException" is thrown with the expected error message
            Exception exception = assertThrows(NotRightFormatToPostException.class, () -> firestationService.addNewMapping(mapping));
            assertEquals("There is something missing in the request :\nto post a new mapping there should be at least a \"number\" and an \"address\" fields.\n", exception.getMessage());
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(firestationRepository, Mockito.times(0)).findById(anyInt());
            verify(addressService, Mockito.times(0)).getAddress(any(), any(), any());
        }

        @Test
        @DisplayName("GIVEN only non exiting firestation number " +
                "WHEN the function addNewMapping() is called " +
                "THEN a new firestation is created and the expected information message is returned.")
        void addMappingCreateFirestationTest() {
            // GIVEN
            //only non exiting firestation number
            MappingFirestationAddressDTO mapping = new MappingFirestationAddressDTO(1, null, null, null);
            doReturn(false).when(firestationRepository).existsById(1);
            // WHEN
            //the function addNewMapping() is called
            String result = firestationService.addNewMapping(mapping);
            // THEN
            //a new firestation is created and the expected information message is returned
            assertThat(result).isEqualTo("The Firestation number 1 have been created.\n" +
                    "This Firestation isn't attached to any address yet.\n");
            verify(firestationRepository, Mockito.times(1)).save(any(Firestation.class));
            verify(firestationRepository, Mockito.times(1)).existsById(1);
            verify(firestationRepository, Mockito.times(0)).findById(anyInt());
            verify(addressService, Mockito.times(0)).getAddress(any(), any(), any());
        }

        @Test
        @DisplayName("GIVEN an address with only street " +
                "WHEN the function addNewMapping is called " +
                "THEN default zip and city are added.")
        void addNewMappingAddressOnlyStreetTest() {
            // GIVEN
            //an existing firestation and a new address
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            doThrow(ObjectNotFoundException.class).when(addressService).getAddress(any(), any(), any());
            // WHEN
            //the tested function  getFirestationById is called with parameter id = 1
            String result = firestationService.addNewMapping(new MappingFirestationAddressDTO(1, "address test", null, null));
            // THEN
            //the firestation should be found
            assertThat(result).isEqualTo("The address \"address test - 97451 CULVER\" have been added to the firestation number 1.\n");
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(addressService, Mockito.times(1)).getAddress(any(), any(), any());

        }


    }

    @Nested
    @DisplayName("deleteFirestationOrAddress tests:")
    class DeleteFirestationOrAddressTest {

        @Test
        @DisplayName("GIVEN an existing firestation number with no attached address" +
                "WHEN the function deleteFirestationOrAddress is called" +
                "THEN the expected message should be returned.")
        void deleteFirestationOrAddressExistingNumberTest() {
            // GIVEN
            //an existing firestation number with no attached address
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            doNothing().when(firestationRepository).deleteById(1);
            // WHEN
            //the function deleteFirestationOrAddress is called
            String result = firestationService.deleteFirestationOrAddress("1");
            // THEN
            //the expected message should be returned
            assertThat(result).isEqualTo("The Firestation number 1 have been deleted");
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(1)).deleteById(1);
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(0)).getAddress(anyString());
        }

        @Test
        @DisplayName("GIVEN an existing firestation number with attached addresses" +
                "WHEN the function deleteFirestationOrAddress is called" +
                "THEN a FirestationNonEmptyException should be thrown with the expected error message.")
        void deleteFirestationOrAddressExistingNumberWithAddressTest() {
            // GIVEN
            //an existing firestation number with attached addresses
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            Address address1 = new Address("address test 1", "zip test", "city test");
            Address address2 = new Address("address test 2", "zip test", "city test");
            firestation.addAddress(address1);
            firestation.addAddress(address2);
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            doNothing().when(firestationRepository).deleteById(1);
            // WHEN
            //the function deleteFirestationOrAddress is called
            // THEN
            //a FirestationNonEmptyException should be thrown with the expected error message
            Exception exception = assertThrows(FirestationNonEmptyException.class, () -> firestationService.deleteFirestationOrAddress("1"));
            assertEquals("The firestation number 1 cannot be deleted,\n" +
                    "some addresses are still attached to this firestation:\n" + "address test 1 - zip test CITY TEST\n" + "address test 2 - zip test CITY TEST\n"
                    + "Please reassign this addresses to another firestation before deleting.", exception.getMessage());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).deleteById(any());
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(0)).getAddress(anyString());
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation number" +
                "WHEN the function deleteFirestationOrAddress is called" +
                "THEN a NothingToDeleteException should be thrown with the expected error message.")
        void deleteFirestationOrAddressNonExistingTest() {
            // GIVEN
            //a non-existing firestation number
            doReturn(Optional.empty()).when(firestationRepository).findById(1);

            // WHEN
            //the function deleteFirestationOrAddress is called
            // THEN
            //a NothingToDeleteException should be thrown with the expected error message
            Exception exception = assertThrows(NothingToDeleteException.class, () -> firestationService.deleteFirestationOrAddress("1"));
            assertEquals("The firestation number 1 was not found," +
                    "\nso it couldn't have been deleted", exception.getMessage());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).deleteById(any());
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(0)).getAddress(anyString());
        }

        @Test
        @DisplayName("GIVEN an existing address attached to a firestation" +
                "WHEN the function deleteFirestationOrAddress is called" +
                "THEN the expected message should be returned.")
        void deleteFirestationOrAddressExistingAddressTest() {
            // GIVEN
            //an existing address attached to a firestation
            Address address1 = new Address("address test 1", "zip test", "city test");
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            firestation.addAddress(address1);

            doReturn(address1).when(addressService).getAddress("address test 1");
            // WHEN
            //the function deleteFirestationOrAddress is called
            String result = firestationService.deleteFirestationOrAddress("address test 1");
            // THEN
            //the expected message should be returned
            assertThat(result).isEqualTo("The address address test 1 - zip test city test have been removed from firestation number 1.\n");
            verify(firestationRepository, Mockito.times(0)).findById(any());
            verify(firestationRepository, Mockito.times(0)).deleteById(any());
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(addressService, Mockito.times(1)).getAddress("address test 1");
        }

        @Test
        @DisplayName("GIVEN an existing address not attached to any firestation" +
                "WHEN the function deleteFirestationOrAddress is called" +
                "THEN a NothingToDeleteException should be thrown with the expected error message.")
        void deleteFirestationOrAddressNothingToDeleteTest() {
            // GIVEN
            //an existing address not attached to any firestation
            Address address1 = new Address("address test 1", "zip test", "city test");
            doReturn(address1).when(addressService).getAddress("address test 1");
            // WHEN
            //the function deleteFirestationOrAddress is called
            // THEN
            // a NothingToDeleteException should be thrown with the expected error message
            Exception exception = assertThrows(NothingToDeleteException.class, () -> firestationService.deleteFirestationOrAddress("address test 1"));
            assertEquals("The address address test 1 - zip test city test wasn't attached to any firestation, so no mapping couldn't have been deleted.\n", exception.getMessage());
            verify(firestationRepository, Mockito.times(0)).findById(any());
            verify(firestationRepository, Mockito.times(0)).deleteById(any());
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(1)).getAddress("address test 1");
        }

        @Test
        @DisplayName("GIVEN a non-existing address" +
                "WHEN the function deleteFirestationOrAddress is called" +
                "THEN a ObjectNotFoundException should be thrown with the expected error message.")
        void deleteFirestationOrAddressNoExistingTest() {
            // GIVEN
            //a non-existing address
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(addressService).getAddress("address test 1");
            // WHEN
            //the function deleteFirestationOrAddress is called
            // THEN
            // a ObjectNotFoundException should be thrown with the expected error message
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> firestationService.deleteFirestationOrAddress("address test 1"));
            assertEquals("error message", exception.getMessage());
            verify(firestationRepository, Mockito.times(0)).findById(any());
            verify(firestationRepository, Mockito.times(0)).deleteById(any());
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(1)).getAddress("address test 1");
        }
    }

    @Nested
    @DisplayName("updateMapping tests:")
    class UpdateMappingTest {

        @Test
        @DisplayName("GIVEN an existing address and firestation" +
                "WHEN the function updateMapping is called" +
                "THEN the expected message should be returned.")
        void updateMappingTest() {
            // GIVEN
            //an existing address and firestation
            Firestation firestation = new Firestation();
            firestation.setStationId(1);
            Address address = new Address("address test", "zip test", "city test");
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            doReturn(address).when(addressService).getAddress("address test");
            MappingFirestationAddressDTO mappingFirestationAddressDTO = new MappingFirestationAddressDTO(1, "address test", null, null);
            // WHEN
            //the function updateMapping is called
            String result = firestationService.updateMapping(mappingFirestationAddressDTO);
            // THEN
            //the expected message should be returned
            assertThat(result).isEqualTo("The address address test - zip test city test have been attached to firestation number 1.\n");
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(1)).save(any());
            verify(addressService, Mockito.times(1)).getAddress("address test");
        }

        @Test
        @DisplayName("GIVEN an existing address already attached to a firestation" +
                "WHEN the function updateMapping is called" +
                "THEN the expected message should be returned.")
        void updateMappingAddressAttachedTest() {
            // GIVEN
            //an existing address already attached to a firestation
            Firestation firestation = new Firestation(1, new ArrayList<>());
            Firestation firestation2 = new Firestation(2, new ArrayList<>());
            Address address = new Address("address test", "zip test", "city test");
            firestation2.addAddress(address);
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            doReturn(address).when(addressService).getAddress("address test");
            MappingFirestationAddressDTO mappingFirestationAddressDTO = new MappingFirestationAddressDTO(1, "address test", null, null);
            // WHEN
            //the function updateMapping is called
            String result = firestationService.updateMapping(mappingFirestationAddressDTO);
            // THEN
            //the expected message should be returned
            assertThat(result).isEqualTo("The address address test - zip test city test have been removed from firestation number 2.\n"
                    + "The address address test - zip test city test have been attached to firestation number 1.\n");
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(2)).save(any());
            verify(addressService, Mockito.times(1)).getAddress("address test");
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation" +
                "WHEN the function updateMapping is called" +
                "THEN an ObjectNotFoundException should be thrown with the expected error message.")
        void updateMappingFirestationNonExistingTest() {
            // GIVEN
            //a non-existing firestation
            doReturn(Optional.empty()).when(firestationRepository).findById(1);
            MappingFirestationAddressDTO mappingFirestationAddressDTO = new MappingFirestationAddressDTO(1, "address test", null, null);
            // WHEN
            //the function updateMapping is called
            // THEN
            //an ObjectNotFoundException should be thrown with the expected error message
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> firestationService.updateMapping(mappingFirestationAddressDTO));
            assertEquals("The Firestation number 1 was not found," +
                    "\nso the new mapping couldn't have been done.\n", exception.getMessage());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(0)).getAddress(anyString());
        }

        @Test
        @DisplayName("GIVEN a non-existing address" +
                "WHEN the function updateMapping is called" +
                "THEN an ObjectNotFoundException should be thrown with the expected error message.")
        void updateMappingAddressNonExistingTest() {
            // GIVEN
            //a non-existing address
            Firestation firestation = new Firestation(1, new ArrayList<>());
            doReturn(Optional.of(firestation)).when(firestationRepository).findById(1);
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(addressService).getAddress("address test");
            MappingFirestationAddressDTO mappingFirestationAddressDTO = new MappingFirestationAddressDTO(1, "address test", null, null);
            // WHEN
            //the function updateMapping is called
            // THEN
            //an ObjectNotFoundException should be thrown with the expected error message
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> firestationService.updateMapping(mappingFirestationAddressDTO));
            assertEquals("error message", exception.getMessage());
            verify(firestationRepository, Mockito.times(1)).findById(1);
            verify(firestationRepository, Mockito.times(0)).save(any());
            verify(addressService, Mockito.times(1)).getAddress("address test");
        }
    }
}
