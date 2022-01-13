package com.safetynet.alert.controller;

import com.safetynet.alert.model.DTO.ChildInfoDTO;
import com.safetynet.alert.model.DTO.FireInfoDTO;
import com.safetynet.alert.model.DTO.FirestationInfoDTO;
import com.safetynet.alert.model.DTO.PersonInfo2DTO;
import com.safetynet.alert.service.UrlsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@Api("Specific CRUD operations about SafetyNet Alert.")
@Slf4j
public class UrlsController {

    @Autowired
    private UrlsService urlsService;

    /**
     * Read - Get a list of all the persons and also the number of adults and the number of children covered by a firestation.
     *
     * @param stationId  - the number of the station in which information are researched.
     * @return - A FirestationInfoDTO object containing information about the persons covered by the firestation researched.
     */
    @Transactional
    @GetMapping("/firestation")
    public ResponseEntity<FirestationInfoDTO> getPersonsCoveredByStation(@RequestParam int stationId) {
        log.debug("The function getPersonsCoveredByStation in UrlsController is beginning.");
        FirestationInfoDTO personsCoveredByStation = urlsService.getPersonsCoveredByFirestation(stationId);
        log.debug("The function getPersonsCoveredByStation in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(personsCoveredByStation, HttpStatus.OK);
    }

    /**
     * Read - Get a list of children living at the researched address and the members of their household.
     *
     * @param address  - a String which corresponds to the researched address.
     * @return - A list of ChildInfoDTO objects, each one containing information about the children living at the researched address and his household.
     */
    @Transactional
    @GetMapping("/childAlert")
    public ResponseEntity<List<ChildInfoDTO>> getChildrenByAddress(@RequestParam String address) {
        log.debug("The function getChildrenByAddress in UrlsController is beginning.");
        List<ChildInfoDTO> childrenByAddress = urlsService.getChildrenByAddress(address);
        log.debug("The function getChildrenByAddress in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(childrenByAddress, HttpStatus.OK);
    }

    /**
     * Read - Get a list phone numbers belonging to the persons covered by a firestation.
     *
     * @param stationId  - the firestation number.
     * @return - A list of String which are all the phone numbers of the person covered by the researched firestation
     */
    @Transactional
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneNumbersByFirestation(@RequestParam int stationId) {
        log.debug("The function getPhoneNumbersByFirestation in UrlsController is beginning.");
        List<String> phoneNumbersByFirestation = urlsService.getPhoneNumbersByFirestation(stationId);
        log.debug("The function getPhoneNumbersByFirestation in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(phoneNumbersByFirestation, HttpStatus.OK);
    }

    /**
     * Read - Get a list of person living at an address and information about them (phone number, age and medical records).
     *
     * @param address  - a String that represents the researched address.
     * @return - A FireInfoDTO object containing information about the person found.
     */
    @Transactional
    @GetMapping("/fire")
    public ResponseEntity<FireInfoDTO> getPersonsByAddress(@RequestParam String address) {
        log.debug("The function getPersonsByAddress in UrlsController is beginning.");
        FireInfoDTO personsByAddress = urlsService.getPersonsByAddress(address);
        log.debug("The function getPersonsByAddress in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(personsByAddress, HttpStatus.OK);
    }

    /**
     * Read - Get a list of person living at an address and information about them (phone number, age and medical records)
     * for all the persons covered by firestations.
     *
     * @param stations  - a list of Integer which are the numbers of the researched firestations.
     * @return - A list of FireInfoDTO objects containing information about persons found at each address covered by the researched firestations.
     */
    @Transactional
    @GetMapping("/flood")
    public ResponseEntity<List<FireInfoDTO>> getHouseholdsByStation(@RequestParam List<Integer> stations) {
        log.debug("The function getHouseholdsByStation in UrlsController is beginning.");
        List<FireInfoDTO> result = urlsService.getHouseholdsByStation(stations);
        log.debug("The function getHouseholdsByStation in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Transactional
    @GetMapping("/personInfo")
    public ResponseEntity<PersonInfo2DTO> getPersonsByName(@RequestParam String firstName, String lastName) {
        log.debug("The function getPersonsByName in UrlsController is beginning.");
        PersonInfo2DTO result = urlsService.getPersonsByName(firstName, lastName);
        log.debug("The function getPersonsByName in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Read - Get a list of city's residents' e-mail.
     *
     * @param city  - a String which is the researched city.
     * @return - A list of String containing all the e-mail addresses of the persons living in the researched city.
     */
    @Transactional
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getMailsByCity(@RequestParam String city) {
        log.debug("The function getMailsByCity in UrlsController is beginning.");
        List<String> result = urlsService.getMailsByCity(city);
        log.debug("The function getMailsByCity in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
