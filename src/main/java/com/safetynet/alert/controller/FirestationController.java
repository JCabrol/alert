package com.safetynet.alert.controller;

import com.safetynet.alert.exceptions.NothingToDeleteException;
import com.safetynet.alert.model.Firestation;
import com.safetynet.alert.model.DTO.MappingFirestationAddressDTO;
import com.safetynet.alert.service.FirestationService;
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
public class FirestationController {

    @Autowired
    private FirestationService firestationService;

    /**
     * Read - Get all firestations registered.
     *
     * @return - A list of firestations.
     */
    @GetMapping("/firestation")
    @Transactional
    public ResponseEntity<String> getAllFirestations() {
        log.debug("The function getAllFirestations in FirestationController is beginning.");
        //getting all firestations
        List<Firestation> firestations = firestationService.getFirestations();
        //putting the result list to String to be readable by user
        String result = firestationService.firestationsToString(firestations);
        log.debug("The function getAllFirestations in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Read - Get one firestation from its id or one of its attached addresses
     *
     * @param idOrAddress - An int which is the id of the researched firestation or a String which is one of its addresses
     * @return A String giving all information about the firestation object corresponding to the id
     */
    @GetMapping("/firestation/{idOrAddress}")
    @Transactional
    public ResponseEntity<String> getFirestation(@PathVariable("idOrAddress") String idOrAddress) {
        log.debug("The function getFirestation in FirestationController is beginning.");
        String result;
        try {
            //if the variable idOrAddress is an int, so it's a firestation's id, the function getFirestationById is called
            int id = Integer.parseInt(idOrAddress);
            Firestation firestationResearched = firestationService.getFirestationById(id);
            result = firestationResearched.toString();
        } catch (NumberFormatException e) {
            //if the variable idOrAddress is not an int, so it's supposed to be an address, the function getFirestationByAddress is called
            List<Firestation> firestationsResearched = firestationService.getFirestationByAddress(idOrAddress);
            result = firestationService.firestationsToString(firestationsResearched);
        }
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
        //getting parameters id and address from request's body and calling the function addNewMapping on this
//
//        String address = mappingFirestationAddress.getAddress();
//        String result = firestationService.addNewMapping(id, address);
        String result = firestationService.addNewMapping(mappingFirestationAddressDTO);
        int id = mappingFirestationAddressDTO.getFirestationId();
        //building a new location and putting it in the response's headers to transmit the created firestation's uri to user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        log.debug("The function addMappingFirestationAddress in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.CREATED);
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
        //getting parameters id and address from request's body and calling the function addNewMapping on this
        int id = mappingFirestationAddressDTO.getFirestationId();
        String address = mappingFirestationAddressDTO.getAddress();
        //deleting old mappings concerning the address to update
        try{
        firestationService.deleteAddress(address);}catch(NothingToDeleteException ignored){}
        //creating a new mapping with given firestation and address
        firestationService.addNewMapping(mappingFirestationAddressDTO);
        //building a new location and putting it in the response's headers to transmit the updated firestation's uri to user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        String okSaved = "The address " + address + " have been updated to the firestation number " + id + ".";
        log.debug("The function updateAddressByFirestationId in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(okSaved, httpHeaders, HttpStatus.OK);
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
        String result;
        try {
            //if the variable idOrAddress is an int, so it's a firestation's id, the function deleteFirestation is called
            int id = Integer.parseInt(idOrAddress);
            firestationService.deleteFirestation(id);
            result = "The firestation number " + id + " have been deleted.";
        } catch (NumberFormatException e) {
            //if the variable idOrAddress is not an int, so it's supposed to be an address, the function deleteAddress is called
            result = firestationService.deleteAddress(idOrAddress);
        }
        log.debug("The function deleteAddressOrFirestation in FirestationController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}


