package com.safetynet.alert.unitTests;

import com.safetynet.alert.model.*;
import com.safetynet.alert.model.DTO.ChildInfoDTO;
import com.safetynet.alert.model.DTO.FireInfoDTO;
import com.safetynet.alert.model.DTO.FirestationInfoDTO;
import com.safetynet.alert.model.DTO.PersonInfo2DTO;
import com.safetynet.alert.service.AddressService;
import com.safetynet.alert.service.FirestationService;
import com.safetynet.alert.service.PersonService;
import com.safetynet.alert.service.UrlsService;
import lombok.extern.slf4j.Slf4j;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Tag("UrlsTests")
@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest
public class UrlsServiceTest {

    @Autowired
    private UrlsService urlsService;

    @MockBean
    private PersonService personService;

    @MockBean
    private FirestationService firestationService;

    @MockBean
    private AddressService addressService;


    @Test
    void getPersonsCoveredByFirestationTest() {
        // GIVEN
        //persons covered by the researched firestation
        Address address = new Address("StreetTest", "ZipTest", "cityTest");
        Address address2 = new Address("StreetTest2", "ZipTest2", "cityTest2");
        Person person1 = new Person("firstNameTest1", "lastNameTest1");
        Person person2 = new Person("firstNameTest2", "lastNameTest2");
        Person person3 = new Person("firstNameTest3", "lastNameTest2");
        address.addPerson(person1);
        address.addPerson(person2);
        address2.addPerson(person3);
        LocalDate birthdate1 = LocalDate.of(2000, 12, 12);
        LocalDate birthdate2 = LocalDate.of(2015, 12, 12);
        MedicalRecords medicalRecords1 = new MedicalRecords();
        medicalRecords1.setBirthdate(birthdate1);
        medicalRecords1.addPerson(person1);
        MedicalRecords medicalRecords2 = new MedicalRecords();
        medicalRecords2.setBirthdate(birthdate2);
        medicalRecords2.addPerson(person2);
        Firestation firestation = new Firestation(1, new ArrayList<>());
        firestation.addAddress(address);
        doReturn(firestation).when(firestationService).getFirestationById(1);
        List<Person> allPersons = List.of(person1, person2, person3);
        doReturn(allPersons).when(personService).getPersons();
        // WHEN
        //the function getPersonsCoveredByFirestation is called
        FirestationInfoDTO result = urlsService.getPersonsCoveredByFirestation(1);
        // THEN
        //a FirestationInfoDTO object with correct information should be returned
        assertThat(result.getNumberOfAdults()).isEqualTo(1);
        assertThat(result.getNumberOfChildren()).isEqualTo(1);
        assertThat(result.getStationId()).isEqualTo(1);
        assertThat(result.getPersonsCoveredByStation().size()).isEqualTo(2);
        assertFalse(result.getPersonsCoveredByStation().get(0).isChild());
        assertTrue(result.getPersonsCoveredByStation().get(1).isChild());
        verify(firestationService, Mockito.times(1)).getFirestationById(1);
        verify(personService, Mockito.times(1)).getPersons();
    }

    @Test
    void getChildrenByAddressTest() {
        // GIVEN
        Address address = new Address("streetTest", "zipTest", "cityTest");
        Address address2 = new Address("StreetTest2", "ZipTest2", "cityTest2");
        Person person1 = new Person("firstNameTest1", "lastNameTest1");
        Person person2 = new Person("firstNameTest2", "lastNameTest2");
        Person person3 = new Person("firstNameTest3", "lastNameTest2");
        address.addPerson(person1);
        address.addPerson(person2);
        address2.addPerson(person3);
        LocalDate birthdate1 = LocalDate.of(2000, 12, 12);
        LocalDate birthdate2 = LocalDate.of(2015, 12, 12);
        MedicalRecords medicalRecords1 = new MedicalRecords();
        medicalRecords1.setBirthdate(birthdate1);
        medicalRecords1.addPerson(person1);
        MedicalRecords medicalRecords2 = new MedicalRecords();
        medicalRecords2.setBirthdate(birthdate2);
        medicalRecords2.addPerson(person2);
        List<Person> allPersons = List.of(person1, person2, person3);
        doReturn(address).when(addressService).getAddress("streetTest");
        doReturn(allPersons).when(personService).getPersons();
        // WHEN
        List<ChildInfoDTO> result = urlsService.getChildrenByAddress("streetTest");
        // THEN
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("firstNameTest2");
        assertThat(result.get(0).getLastName()).isEqualTo("lastNameTest2");
        assertThat(result.get(0).getAge()).isEqualTo(LocalDate.now().compareTo(birthdate2));
        assertThat(result.get(0).getHouseholdMembers().size()).isEqualTo(1);
        assertTrue(result.get(0).getHouseholdMembers().get(0).contains("firstNameTest1"));
        verify(addressService, Mockito.times(1)).getAddress("streetTest");
        verify(personService, Mockito.times(2)).getPersons();
    }

    @Test
    void getPhoneNumbersByFirestationTest() {
        // GIVEN
        Address address = new Address("streetTest", "zipTest", "cityTest");
        Address address2 = new Address("StreetTest2", "ZipTest2", "cityTest2");
        Person person1 = new Person("firstNameTest1", "lastNameTest1");
        Person person2 = new Person("firstNameTest2", "lastNameTest2");
        Person person3 = new Person("firstNameTest3", "lastNameTest2");
        address.addPerson(person1);
        address.addPerson(person2);
        address2.addPerson(person3);
        person1.setPhoneNumber("phoneNumberTest1");
        person2.setPhoneNumber("phoneNumberTest2");
        person3.setPhoneNumber("phoneNumberTest3");
        Firestation firestation = new Firestation(1, new ArrayList<>());
        firestation.addAddress(address);
        doReturn(firestation).when(firestationService).getFirestationById(1);
        // WHEN
        List<String> result = urlsService.getPhoneNumbersByFirestation(1);
        // THEN
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains("phoneNumberTest1", "phoneNumberTest2");
        assertThat(result).doesNotContain("phoneNumberTest3");
        verify(firestationService, Mockito.times(1)).getFirestationById(1);
    }

    @Test
    void getPersonsByAddressTest() {
        // GIVEN
        Address address = new Address("streetTest", "zipTest", "cityTest");
        Address address2 = new Address("StreetTest2", "ZipTest2", "cityTest2");
        Person person1 = new Person("firstNameTest1", "lastNameTest1");
        Person person2 = new Person("firstNameTest2", "lastNameTest2");
        Person person3 = new Person("firstNameTest3", "lastNameTest2");
        address.addPerson(person1);
        address.addPerson(person2);
        address2.addPerson(person3);
        person1.setPhoneNumber("phoneNumberTest1");
        person2.setPhoneNumber("phoneNumberTest2");
        person3.setPhoneNumber("phoneNumberTest3");
        LocalDate birthdate1 = LocalDate.of(2000, 12, 12);
        LocalDate birthdate2 = LocalDate.of(2015, 12, 12);

        MedicalRecords medicalRecords1 = new MedicalRecords();
        medicalRecords1.setBirthdate(birthdate1);
        medicalRecords1.addPerson(person1);
        MedicalRecords medicalRecords2 = new MedicalRecords();
        medicalRecords2.setBirthdate(birthdate2);
        medicalRecords2.addPerson(person2);
        medicalRecords1.addMedication(new Medication("medication1"));
        medicalRecords1.addMedication(new Medication("medication2"));
        medicalRecords2.addAllergy(new Allergy("allergy1"));
        Firestation firestation = new Firestation(1, new ArrayList<>());
        firestation.addAddress(address);
        doReturn(address).when(addressService).getAddress("streetTest");
        // WHEN
        FireInfoDTO result = urlsService.getPersonsByAddress("streetTest");
        // THEN
        assertThat(result.getAddress()).isEqualTo("streetTest");
        assertThat(result.getStation()).isEqualTo(1);
        assertThat(result.getPersonList().size()).isEqualTo(2);
        assertThat(result.getPersonList().get(0).getAllergies().size()).isEqualTo(0);
        assertThat(result.getPersonList().get(1).getAllergies().size()).isEqualTo(1);
        assertThat(result.getPersonList().get(0).getMedications().size()).isEqualTo(2);
        assertThat(result.getPersonList().get(1).getMedications().size()).isEqualTo(0);
        verify(addressService, Mockito.times(1)).getAddress("streetTest");
    }

    @Test
    void getHouseholdsByStationTest() {
        // GIVEN
        Address address = new Address(1, "streetTest", "zipTest", "cityTest", new ArrayList<>(), null);
        Address address2 = new Address(2, "streetTest2", "ZipTest2", "cityTest2", new ArrayList<>(), null);
        Address address3 = new Address("StreetTest3", "ZipTest3", "cityTest3");
        Person person1 = new Person("firstNameTest1", "lastNameTest1");
        Person person2 = new Person("firstNameTest2", "lastNameTest2");
        Person person3 = new Person("firstNameTest3", "lastNameTest2");
        Person person4 = new Person("firstNameTest4", "lastNameTest4");
        address.addPerson(person1);
        address.addPerson(person2);
        address2.addPerson(person3);
        address3.addPerson(person4);
        person1.setPhoneNumber("phoneNumberTest1");
        person2.setPhoneNumber("phoneNumberTest2");
        person3.setPhoneNumber("phoneNumberTest3");
        LocalDate birthdate1 = LocalDate.of(2000, 12, 12);
        LocalDate birthdate2 = LocalDate.of(2015, 12, 12);
        LocalDate birthdate3 = LocalDate.of(1982, 12, 12);
        MedicalRecords medicalRecords1 = new MedicalRecords();
        medicalRecords1.setBirthdate(birthdate1);
        medicalRecords1.addPerson(person1);
        MedicalRecords medicalRecords2 = new MedicalRecords();
        medicalRecords2.setBirthdate(birthdate2);
        medicalRecords2.addPerson(person2);
        medicalRecords1.addMedication(new Medication("medication1"));
        medicalRecords1.addMedication(new Medication("medication2"));
        medicalRecords2.addAllergy(new Allergy("allergy1"));
        MedicalRecords medicalRecords3 = new MedicalRecords();
        medicalRecords3.addPerson(person3);
        medicalRecords3.setBirthdate(birthdate3);
        Firestation firestation = new Firestation(1, new ArrayList<>());
        Firestation firestation2 = new Firestation(2, new ArrayList<>());
        firestation.addAddress(address);
        firestation2.addAddress(address2);
        doReturn(firestation).when(firestationService).getFirestationById(1);
        doReturn(firestation2).when(firestationService).getFirestationById(2);
        doReturn(address).when(addressService).getAddress("streetTest");
        doReturn(address2).when(addressService).getAddress("streetTest2");
        // WHEN
        List<FireInfoDTO> result = urlsService.getHouseholdsByStation(List.of(1, 2));
        // THEN
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getStation()).isEqualTo(1);
        assertThat(result.get(1).getStation()).isEqualTo(2);
        assertThat(result.get(0).getPersonList().get(0).getFirstName()).isEqualTo("firstNameTest1");
        assertThat(result.get(0).getPersonList().get(1).getFirstName()).isEqualTo("firstNameTest2");
        assertThat(result.get(1).getPersonList().get(0).getFirstName()).isEqualTo("firstNameTest3");
        verify(firestationService, Mockito.times(1)).getFirestationById(1);
        verify(firestationService, Mockito.times(1)).getFirestationById(2);
        verify(addressService, Mockito.times(1)).getAddress("streetTest");
        verify(addressService, Mockito.times(1)).getAddress("streetTest2");
    }

    @Test
    void getPersonsByNameTest() {
        // GIVEN
        Address address = new Address(1, "streetTest", "zipTest", "cityTest", new ArrayList<>(), null);
        Address address2 = new Address(2, "streetTest2", "ZipTest2", "cityTest2", new ArrayList<>(), null);
        Address address3 = new Address("StreetTest3", "ZipTest3", "cityTest3");
        Person person1 = new Person("firstNameTest", "lastNameTest");
        Person person2 = new Person("firstNameTest", "lastNameTest");
        Person person3 = new Person("firstNameTest3", "lastNameTest2");
        Person person4 = new Person("firstNameTest4", "lastNameTest4");
        address.addPerson(person1);
        address.addPerson(person2);
        address2.addPerson(person3);
        address3.addPerson(person4);
        person1.setPhoneNumber("phoneNumberTest1");
        person2.setPhoneNumber("phoneNumberTest2");
        person3.setPhoneNumber("phoneNumberTest3");
        LocalDate birthdate1 = LocalDate.of(2000, 12, 12);
        LocalDate birthdate2 = LocalDate.of(2015, 12, 12);
        LocalDate birthdate3 = LocalDate.of(1982, 12, 12);
        MedicalRecords medicalRecords1 = new MedicalRecords();
        medicalRecords1.setBirthdate(birthdate1);
        medicalRecords1.addPerson(person1);
        MedicalRecords medicalRecords2 = new MedicalRecords();
        medicalRecords2.setBirthdate(birthdate2);
        medicalRecords2.addPerson(person2);
        medicalRecords1.addMedication(new Medication("medication1"));
        medicalRecords1.addMedication(new Medication("medication2"));
        medicalRecords2.addAllergy(new Allergy("allergy1"));
        MedicalRecords medicalRecords3 = new MedicalRecords();
        medicalRecords3.addPerson(person3);
        medicalRecords3.setBirthdate(birthdate3);
        Firestation firestation = new Firestation(1, new ArrayList<>());
        Firestation firestation2 = new Firestation(2, new ArrayList<>());
        firestation.addAddress(address);
        firestation2.addAddress(address2);

        List<Person> personList = List.of(person1, person2);
        doReturn(personList).when(personService).getPersonsByName("firstNameTest", "lastNameTest");
        // WHEN
        List<PersonInfo2DTO> result = urlsService.getPersonsByName("firstNameTest", "lastNameTest");
        // THEN
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getPhoneNumber()).isEqualTo("phoneNumberTest1");
        assertThat(result.get(1).getAllergies().size()).isEqualTo(1);
        verify(personService, Mockito.times(1)).getPersonsByName("firstNameTest", "lastNameTest");
    }

    @Test
    void getMailsByCityTest() {
        // GIVEN
        Address address = new Address("streetTest", "zipTest", "cityTest");
        Address address2 = new Address("StreetTest2", "ZipTest", "cityTest");
        Address address3 = new Address("StreetTest3", "ZipTest3", "cityTest3");
        Person person1 = new Person("firstNameTest1", "lastNameTest1");
        Person person2 = new Person("firstNameTest2", "lastNameTest2");
        Person person3 = new Person("firstNameTest3", "lastNameTest2");
        address.addPerson(person1);
        address2.addPerson(person2);
        address3.addPerson(person3);
        person1.setMail("mailTest1");
        person2.setMail("mailTest2");
        person2.setMail("mailTest2");
        List<Person> personList = List.of(person1,person2,person3);
        doReturn(personList).when(personService).getPersons();
        // WHEN
        List<String> result = urlsService.getMailsByCity("cityTest");
        // THEN
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains("mailTest1", "mailTest2");
        assertThat(result).doesNotContain("mailTest3");
        verify(personService, Mockito.times(1)).getPersons();
    }
}
