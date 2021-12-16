package com.safetynet.alert.service;

import com.safetynet.alert.model.*;
import com.safetynet.alert.model.DTO.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter

@Service
@Slf4j

public class UrlsService {
    @Autowired
    private MedicalRecordsService medicalRecordsService;

    @Autowired
    private PersonService personService;

    @Autowired
    private FirestationService firestationService;


    public FirestationInfoDTO getPersonsCoveredByFirestation(int stationNumber) {

        List<String> addresses =
                firestationService.getFirestationById(stationNumber)
                        .getAttachedAddresses().stream()
                        .map(AttachedAddress::getAddress)
                        .collect(Collectors.toList());

        List<PersonDTO> personsCoveredByStation =
                personService.getPersons()
                        .stream()
                        .filter(w -> addresses.contains(w.getAddress()))
                        .map(p -> {
                            PersonDTO personDTO = new PersonDTO();
                            personDTO.setFirstName(p.getFirstName());
                            personDTO.setLastName(p.getLastName());
                            personDTO.setPhonenumber(p.getPhoneNumber());
                            personDTO.setAddress(p.getAddress() + " - " + p.getZip() + " " + p.getCity());
                            personDTO.setChild(p.getMedicalRecords().getBirthdate2().isAfter(LocalDate.now().minusYears(18)));
                            return personDTO;
                        })
                        .collect(Collectors.toList());

        int numberOfChildren = (int) personsCoveredByStation
                .stream()
                .filter(PersonDTO::isChild)
                .count();

        int numberOfAdults = (int) personsCoveredByStation
                .stream()
                .filter(p -> !p.isChild())
                .count();

        return new FirestationInfoDTO(stationNumber, numberOfChildren, numberOfAdults, personsCoveredByStation);
    }


    public List<ChildInfoDTO> getChildrenByAddress(String address) {
        List<ChildInfoDTO> childrenLivingAtAddress =
                personService.getPersons()
                        .stream()
                        .filter(p -> p.getAddress().equals(address))
                        .filter(p -> p.getMedicalRecords().getBirthdate2().isAfter(LocalDate.now().minusYears(18)))
                        .map(p -> {
                            ChildInfoDTO childInfoDTO = new ChildInfoDTO();
                            childInfoDTO.setFirstName(p.getFirstName());
                            childInfoDTO.setLastName(p.getLastName());
                            childInfoDTO.setAddress(p.getAddress() + " - " + p.getZip() + " " + p.getCity());
                            childInfoDTO.setAge(LocalDate.now().compareTo(p.getMedicalRecords().getBirthdate2()));
                            return childInfoDTO;
                        })
                        .collect(Collectors.toList());

        return childrenLivingAtAddress.stream()
                .peek(c -> {
                    List<Person> householdMembers =
                            personService.getPersons()
                                    .stream()
                                    .filter(p -> (p.getAddress() + " - " + p.getZip() + " " + p.getCity()).equals(c.getAddress()))
                                    .filter(p -> p.getLastName().equals(c.getLastName()))
                                    .filter(p -> !p.getFirstName().equals(c.getFirstName()))
                                    .collect(Collectors.toList());
                    List<PersonDTO> householdMembersDTO =
                            householdMembers
                                    .stream()
                                    .map(p -> {
                                        PersonDTO personDTO = new PersonDTO();
                                        personDTO.setFirstName(p.getFirstName());
                                        personDTO.setLastName(p.getLastName());
                                        personDTO.setPhonenumber(p.getPhoneNumber());
                                        personDTO.setAddress(p.getAddress() + " - " + p.getZip() + " " + p.getCity());
                                        personDTO.setChild(p.getMedicalRecords().getBirthdate2().isAfter(LocalDate.now().minusYears(18)));
                                        return personDTO;
                                    })
                                    .collect(Collectors.toList());
                    c.setHouseholdMembers(householdMembersDTO);
                })
                .collect(Collectors.toList());
    }

    public List<String> getPhoneNumbersByFirestation(int stationId) {
        Firestation firestation = firestationService.getFirestationById(stationId);
        List<AttachedAddress> attachedAddresses = firestation.getAttachedAddresses();
        List<Person> personsFromStation = new ArrayList<>();
        attachedAddresses.forEach(c->personsFromStation.addAll(personService.getPersonsByAddress(c.getAddress())));
        return personsFromStation.stream()
                .map(Person::getPhoneNumber)
                .distinct()
                .collect(Collectors.toList());
    }

    public FireInfoDTO getPersonsByAddress(String address) {
        List<Person> personsByAddress = personService.getPersonsByAddress(address);
        List<PersonInfoDTO> personInfoDTOList = personsByAddress.stream()
                .map(p->{
            PersonInfoDTO personInfoDTO = new PersonInfoDTO();
            personInfoDTO.setFirstName(p.getFirstName());
            personInfoDTO.setLastName(p.getLastName());
            personInfoDTO.setPhoneNumber(p.getPhoneNumber());
            personInfoDTO.setAge(LocalDate.now().compareTo(p.getMedicalRecords().getBirthdate2()));
            personInfoDTO.setMedications(p.getMedicalRecords().getMedications()
                    .stream()
                    .map(Medication::getMedicationName)
                    .collect(Collectors.toList()));
            personInfoDTO.setAllergies(p.getMedicalRecords().getAllergies()
                    .stream()
                    .map(Allergy::getAllergyName)
                    .collect(Collectors.toList()));
            return personInfoDTO;
        })
                .collect(Collectors.toList());
        int stationId = firestationService.getFirestationByAddress(address).get(0).getStationId();
        return new FireInfoDTO(address,stationId,personInfoDTOList);
    }
}
