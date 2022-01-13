package com.safetynet.alert.service;

import com.safetynet.alert.model.*;
import com.safetynet.alert.model.DTO.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter

@Service
@Slf4j

public class UrlsServiceImpl implements UrlsService{
    @Autowired
    private MedicalRecordsService medicalRecordsService;

    @Autowired
    private PersonService personService;

    @Autowired
    private FirestationService firestationService;

    @Autowired
    private AddressService addressService;

    @Override
    public FirestationInfoDTO getPersonsCoveredByFirestation(int stationNumber) {
        log.debug("The function getPersonsCoveredByFirestation in UrlsService is beginning.");
        List<Address> addresses = firestationService.getFirestationById(stationNumber).getAttachedAddresses();
        List<Person> personsCoveredByStation = personService.getPersons()
                .stream()
                .filter(person -> addresses.contains(person.getAddress())).collect(Collectors.toList());
        List<PersonFirestationDTO> personsDTOCoveredByFirestation =
                personsCoveredByStation.stream()
                        .map(person -> {
                            PersonFirestationDTO personFirestationDTO = new PersonFirestationDTO();
                            personFirestationDTO.setFirstName(person.getFirstName());
                            personFirestationDTO.setLastName(person.getLastName());
                            personFirestationDTO.setPhonenumber(person.getPhoneNumber());
                            personFirestationDTO.setAddress(person.getAddress().getStreet() + " - " + person.getAddress().getZip() + " " + person.getAddress().getCity());
                            personFirestationDTO.setChild(person.getMedicalRecords().getBirthdate().isAfter(LocalDate.now().minusYears(18)));
                            return personFirestationDTO;
                        })
                        .collect(Collectors.toList());

        int numberOfChildren = (int) personsDTOCoveredByFirestation
                .stream()
                .filter(PersonFirestationDTO::isChild)
                .count();

        int numberOfAdults = (int) personsDTOCoveredByFirestation
                .stream()
                .filter(person -> !person.isChild())
                .count();

        FirestationInfoDTO result = new FirestationInfoDTO(stationNumber, numberOfChildren, numberOfAdults, personsDTOCoveredByFirestation);
        log.debug("The function getPersonsCoveredByFirestation in UrlsService is ending without any exception.");
        return result;
    }

    @Override
    public List<ChildInfoDTO> getChildrenByAddress(String address) {
        log.debug("The function getChildrenByAddress in UrlsService is beginning.");
        Address addressSearched = addressService.getAddress(address);
        List<ChildInfoDTO> childrenLivingAtAddress =
                personService.getPersons()
                        .stream()
                        .filter(person -> person.getAddress().equals(addressSearched))
                        .filter(person -> person.getMedicalRecords().getBirthdate().isAfter(LocalDate.now().minusYears(18)))
                        .map(person -> {
                            ChildInfoDTO childInfoDTO = new ChildInfoDTO();
                            childInfoDTO.setFirstName(person.getFirstName());
                            childInfoDTO.setLastName(person.getLastName());
                            childInfoDTO.setAddress(person.getAddress().getStreet() + " - " + person.getAddress().getZip() + " " + person.getAddress().getCity());
                            childInfoDTO.setAge(LocalDate.now().compareTo(person.getMedicalRecords().getBirthdate()));
                            return childInfoDTO;
                        })
                        .collect(Collectors.toList());

        childrenLivingAtAddress = childrenLivingAtAddress.stream()
                .peek(c -> {
                    List<Person> householdMembers =
                            personService.getPersons()
                                    .stream()
                                    .filter(p -> p.getAddress().equals(addressSearched))
                                    .filter(p -> !((p.getFirstName().equals(c.getFirstName())) && (p.getLastName().equals(c.getLastName()))))
                                    .collect(Collectors.toList());
                    List<String> householdMembersString =
                            householdMembers
                                    .stream()
                                    .map(p -> p.getFirstName() + " " + p.getLastName())
                                    .collect(Collectors.toList());
                    c.setHouseholdMembers(householdMembersString);
                })
                .collect(Collectors.toList());
        log.debug("The function getChildrenByAddress in UrlsService is ending without any exception.");
        return childrenLivingAtAddress;
    }

    @Override
    public List<String> getPhoneNumbersByFirestation(int stationId) {
        log.debug("The function getPhoneNumbersByFirestation in UrlsService is beginning.");
        List<Address> attachedAddresses = firestationService.getFirestationById(stationId).getAttachedAddresses();
        List<Person> allPerson = attachedAddresses
        .stream()
                .map(Address::getPersonList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<String> allPhoneNumbers = allPerson
                .stream()
                .map(Person::getPhoneNumber)
                .distinct()
                .collect(Collectors.toList());
        log.debug("The function getPhoneNumbersByFirestation in UrlsService is ending without any exception.");
        return allPhoneNumbers;
    }

    @Override
    public FireInfoDTO getPersonsByAddress(String address) {
        log.debug("The function getPersonsByAddress in UrlsService is beginning.");
        Address addressFound = addressService.getAddress(address);
        List<Person> personsByAddress = addressFound.getPersonList();
        List<PersonInfoDTO> personInfoDTOList = personsByAddress.stream()
                .map(person->{
            PersonInfoDTO personInfoDTO = new PersonInfoDTO();
            personInfoDTO.setFirstName(person.getFirstName());
            personInfoDTO.setLastName(person.getLastName());
            personInfoDTO.setPhoneNumber(person.getPhoneNumber());
            personInfoDTO.setAge(LocalDate.now().compareTo(person.getMedicalRecords().getBirthdate()));
            personInfoDTO.setMedications(person.getMedicalRecords().getMedications()
                    .stream()
                    .map(Medication::getMedicationName)
                    .collect(Collectors.toList()));
            personInfoDTO.setAllergies(person.getMedicalRecords().getAllergies()
                    .stream()
                    .map(Allergy::getAllergyName)
                    .collect(Collectors.toList()));
            return personInfoDTO;
        })
                .collect(Collectors.toList());
        int stationId = addressFound.getFirestation().getStationId();
        FireInfoDTO result = new FireInfoDTO(address,stationId,personInfoDTOList);
        log.debug("The function getPersonsByAddress in UrlsService is ending without any exception.");
        return result;
    }

    @Override
    public List<FireInfoDTO> getHouseholdsByStation(List<Integer> stationNumbers) {
        log.debug("The function getHouseholdsByStation in UrlsService is beginning.");
        List<Firestation> firestationList = stationNumbers
                .stream()
                .map(number->firestationService.getFirestationById(number))
                .collect(Collectors.toList());
        List<Address> addressesList = firestationList
                .stream()
                .map(Firestation::getAttachedAddresses)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<FireInfoDTO> result = addressesList.stream().map(address->getPersonsByAddress(address.getStreet())).collect(Collectors.toList());
        log.debug("The function getHouseholdsByStation in UrlsService is ending without any exception.");
        return result;
    }

    @Override
    public PersonInfo2DTO getPersonsByName(String firstName, String lastName) {
        log.debug("The function getPersonsByName in UrlsService is beginning.");
        Person personFound = personService.getPersonByName(firstName, lastName);
        PersonInfo2DTO result = new PersonInfo2DTO();
        result.setFirstName(personFound.getFirstName());
        result.setLastName(personFound.getLastName());
        result.setAddress(personFound.getAddress().getStreet() + " - " + personFound.getAddress().getZip() + " " + personFound.getAddress().getCity());
        result.setAllergies(personFound.getMedicalRecords().getAllergies().stream().map(Allergy::getAllergyName).collect(Collectors.toList()));
        result.setMedications(personFound.getMedicalRecords().getMedications().stream().map(Medication::getMedicationName).collect(Collectors.toList()));
        result.setMail(personFound.getMail());
        result.setPhoneNumber(personFound.getPhoneNumber());
        log.debug("The function getPersonsByName in UrlsService is ending without any exception.");
        return result;
    }

    @Override
    public List<String> getMailsByCity(String city) {
        log.debug("The function getMailsByCity in UrlsService is beginning.");
        List<String> result = personService.getPersons().stream().filter(person->person.getAddress().getCity().equalsIgnoreCase(city)).map(Person::getMail).collect(Collectors.toList());
        log.debug("The function getMailsByCity in UrlsService is ending without any exception.");
        return result;
    }
}
