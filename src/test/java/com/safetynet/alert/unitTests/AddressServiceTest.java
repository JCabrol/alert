package com.safetynet.alert.unitTests;


import com.safetynet.alert.exceptions.ObjectNotFoundException;
import com.safetynet.alert.model.Address;
import com.safetynet.alert.repository.AddressRepository;
import com.safetynet.alert.service.AddressService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Tag("AddressTests")
@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest
public class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @MockBean
    private AddressRepository addressRepository;

    @Nested
    @DisplayName("getAddress() tests:")
    class GetAddressTest {

        @DisplayName("GIVEN an existing address with street, zip and city  " +
                "WHEN the function getAddress is called " +
                "THEN the address should be returned.")
        @Test
        public void getAddressTest() {
            //GIVEN
            Address address = new Address("streetTest", "zipTest", "cityTest");
            doReturn(List.of(address)).when(addressRepository).findAll();
            //WHEN
            Address result = addressService.getAddress("streetTest", "zipTest", "cityTest");
            //THEN
            assertThat(result).isEqualTo(address);
            verify(addressRepository, Mockito.times(1)).findAll();
        }

        @DisplayName("GIVEN a non existing address  " +
                "WHEN the function getAddress is called " +
                "THEN an ObjectNotFoundException should be thrown.")
        @Test
        public void getAddressNonExistingTest() {
            //GIVEN
            doReturn(List.of()).when(addressRepository).findAll();
            //WHEN
            //THEN
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> addressService.getAddress("streetTest", "zipTest", "cityTest"));
            assertEquals("The address streetTest - zipTest cityTest was not found.\n", exception.getMessage());
            verify(addressRepository, Mockito.times(1)).findAll();
        }

        @DisplayName("GIVEN an existing address with street, zip and city  " +
                "WHEN the function getAddress is called " +
                "THEN the address should be returned.")
        @Test
        public void getAddressWithStreetTest() {
            //GIVEN
            Address address = new Address("streetTest", "zipTest", "cityTest");
            doReturn(List.of(address)).when(addressRepository).findAll();
            //WHEN
            Address result = addressService.getAddress("streetTest");
            //THEN
            assertThat(result).isEqualTo(address);
            verify(addressRepository, Mockito.times(1)).findAll();
        }

        @DisplayName("GIVEN a non existing address  " +
                "WHEN the function getAddress is called " +
                "THEN an ObjectNotFoundException should be thrown.")
        @Test
        public void getAddressNonExistingWithStreetTest() {
            //GIVEN
            doReturn(List.of()).when(addressRepository).findAll();
            //WHEN
            //THEN
            Exception exception = assertThrows(ObjectNotFoundException.class, () -> addressService.getAddress("streetTest"));
            assertEquals("The address streetTest was not found.\n", exception.getMessage());
            verify(addressRepository, Mockito.times(1)).findAll();
        }
    }
}