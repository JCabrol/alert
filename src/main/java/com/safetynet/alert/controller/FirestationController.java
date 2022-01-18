package com.safetynet.alert.controller;

import com.safetynet.alert.model.DTO.FirestationDTO;
import com.safetynet.alert.model.DTO.MappingFirestationAddressDTO;
import com.safetynet.alert.service.FirestationService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@Api("CRUD operations about firestations.")
public class FirestationController {

    @Autowired
    private FirestationService firestationService;

    /**
     * Read - Get all firestations registered.
     *
     * @return - A list of firestations.
     */
    @GetMapping("/firestations")
    @Transactional
    public ResponseEntity<List<FirestationDTO>> getAllFirestations() {
        log.debug("The function getAllFirestations in FirestationController is beginning.");
        List<FirestationDTO> firestations = firestationService.getFirestations();
        log.debug("The function getAllFirestations in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(firestations, HttpStatus.OK);
    }

    /**
     * Read - Get one firestation from its id or one of its attached addresses
     *
     * @param idOrAddress - An int which is the id of the researched firestation or a String which is one of its addresses
     * @return A String giving all information about the firestation object corresponding to the id
     */
    @GetMapping("/firestation/{idOrAddress}")
    @Transactional
    public ResponseEntity<FirestationDTO> getFirestation(@PathVariable("idOrAddress") String idOrAddress) {
        log.debug("The function getFirestation in FirestationController is beginning.");
        FirestationDTO result = firestationService.getFirestationDTO(idOrAddress);
        log.debug("The function getFirestation in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Create - Add a firestation or an address to a firestation
     *
     * @param mappingFirestationAddressDTO - An object which contains a firestation's number (int) and an address (String) which has to be attached to this firestation
     * @return A String indicating the Firestation object saved
     */
    @PostMapping("/firestation")
    @Transactional
    public ResponseEntity<String> addMappingFirestationAddress(@RequestBody MappingFirestationAddressDTO mappingFirestationAddressDTO) {
        log.debug("The function addMappingFirestationAddress in FirestationController is beginning.");
        String result = firestationService.addNewMapping(mappingFirestationAddressDTO);
        int number = mappingFirestationAddressDTO.getNumber();
        //building a new location and putting it in the response's headers to transmit the created firestation's uri to user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(number)
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);

        String okCreated = result + location;
        log.debug("The function addMappingFirestationAddress in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(okCreated, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * Update - Update an address' firestation
     *
     * @param mappingFirestationAddressDTO - A string which is the address for which the firestation has to be changed
     * @return A String indicating the firestation which is updated with the given address
     */
    @PutMapping("/firestation")
    @Transactional
    public ResponseEntity<String> updateAddress(@RequestBody MappingFirestationAddressDTO mappingFirestationAddressDTO) {
        log.debug("The function updateAddressByFirestationId in FirestationController is beginning.");
        String updated = firestationService.updateMapping(mappingFirestationAddressDTO);
        int number = mappingFirestationAddressDTO.getNumber();
        //building a new location and putting it in the response's headers to transmit the updated firestation's uri to user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(number)
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        String okUpdated = updated + location;
        log.debug("The function updateAddressByFirestationId in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(okUpdated, httpHeaders, HttpStatus.OK);
    }

    /**
     * Delete - Delete an address from firestations or a firestation
     *
     * @param idOrAddress - An int which is the firestation's id or a String which is the address to delete
     * @return a String indicating the firestation or the address which have been deleted
     */
    @DeleteMapping("/firestation/{idOrAddress}")
    @Transactional
    public ResponseEntity<String> deleteAddressOrFirestations(@PathVariable("idOrAddress") String idOrAddress) {
        log.debug("The function deleteAddressFromFirestations in FirestationController is beginning.");
        String result = firestationService.deleteFirestationOrAddress(idOrAddress);
        log.debug("The function deleteAddressOrFirestation in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}


