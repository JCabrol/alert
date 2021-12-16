package com.safetynet.alert.controller;

import com.safetynet.alert.model.DTO.ChildInfoDTO;
import com.safetynet.alert.model.DTO.FireInfoDTO;
import com.safetynet.alert.model.DTO.FirestationInfoDTO;
import com.safetynet.alert.service.UrlsService;
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
@Slf4j
public class UrlsController {

    @Autowired
    private UrlsService urlsService;

    @Transactional
    @GetMapping("/firestations")
        public ResponseEntity<FirestationInfoDTO> getPersonsCoveredByStation(@RequestParam int stationId) {
        log.debug("The function getPersonsCoveredByStation in UrlsController is beginning.");
        FirestationInfoDTO personsCoveredByStation = urlsService.getPersonsCoveredByFirestation(stationId);
        log.debug("The function getPersonsCoveredByStation in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(personsCoveredByStation, HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/childAlert")
    public ResponseEntity<List<ChildInfoDTO>> getChildrenByAddress(@RequestParam String address) {
        log.debug("The function getChildrenByAddress in UrlsController is beginning.");
        List<ChildInfoDTO> childrenByAddress = urlsService.getChildrenByAddress(address);
        log.debug("The function getChildrenByAddress in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(childrenByAddress, HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneNumbersByFirestation(@RequestParam int stationId) {
        log.debug("The function getPhoneNumbersByFirestation in UrlsController is beginning.");
        List<String> phoneNumbersByFirestation = urlsService.getPhoneNumbersByFirestation(stationId);
        log.debug("The function getPhoneNumbersByFirestation in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(phoneNumbersByFirestation, HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/fire")
    public ResponseEntity<FireInfoDTO> getPersonsByAddress(@RequestParam String address) {
        log.debug("The function getPersonsByAddress in UrlsController is beginning.");
        FireInfoDTO personsByAddress = urlsService.getPersonsByAddress(address);
        log.debug("The function getPersonsByAddress in UrlsController is ending without any exception.\n");
        return new ResponseEntity<>(personsByAddress, HttpStatus.OK);
    }

}
