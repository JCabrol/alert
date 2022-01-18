package com.safetynet.alert.controller;

import com.safetynet.alert.model.DTO.MedicalRecordDTO;
import com.safetynet.alert.model.MedicalRecords;
import com.safetynet.alert.service.MedicalRecordsService;
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
import java.util.Map;

@RestController
@Slf4j
@Api("CRUD operations about medical records.")
public class MedicalRecordsController {

    @Autowired
    private MedicalRecordsService medicalRecordsService;

    /**
     * Read - Get all medical records registered.
     *
     * @return - A String containing all medical records information.
     */
    @GetMapping("/medicalRecord")
    @Transactional
    public ResponseEntity<List<MedicalRecordDTO>> getAllMedicalRecords() {
        log.debug("The function getAllMedicalRecords in MedicalRecordsController is beginning.");
        List<MedicalRecordDTO> medicalRecords = medicalRecordsService.getMedicalRecords();
        log.debug("The function getAllMedicalRecords in MedicalRecordsController is ending without any exception.\n");
        return new ResponseEntity<>(medicalRecords, HttpStatus.OK);
    }

    /**
     * Read - Get one medical records from the first name and the last name of the person
     *
     * @param pathVariables - A map object of two Strings which are first name and last name of the person whose medical records is researched
     * @return A String giving all information about the medical records concerning the person
     */
    @GetMapping("/medicalRecord/{firstName}/{lastName}")
    @Transactional
    public ResponseEntity<MedicalRecordDTO> getMedicalRecords(@PathVariable Map<String, String> pathVariables) {
        log.debug("The function getMedicalRecords in MedicalRecordsController is beginning.");
        //getting first name and last name from url
        String firstName = pathVariables.get("firstName");
        String lastName = pathVariables.get("lastName");
        //getting medical records corresponding to this person
        MedicalRecords medicalRecords = medicalRecordsService.getMedicalRecordsByName(firstName, lastName);
        MedicalRecordDTO medicalRecordDTO = medicalRecordsService.transformMedicalRecordsToMedicalRecordDTO(medicalRecords);
        log.debug("The function getMedicalRecords in MedicalRecordsController is ending without any exception.\n");
        return new ResponseEntity<>(medicalRecordDTO, HttpStatus.OK);
    }

    /**
     * Create - Add new medical records
     *
     * @param medicalRecords - the new medical records to add
     * @return A String indicating the medical records created
     */
    @PostMapping("/medicalRecord")
    @Transactional
    public ResponseEntity<String> addMedicalRecord(@RequestBody MedicalRecordDTO medicalRecords) {
        log.debug("The function addMedicalRecord in MedicalRecordsController is beginning.");
        //adding medical records
        MedicalRecords medicalRecordSaved = medicalRecordsService.addNewMedicalRecords(medicalRecords);
        String firstName = medicalRecordSaved.getPerson().getFirstName().toUpperCase();
        String lastName = medicalRecordSaved.getPerson().getLastName().toUpperCase();
        String result = "Medical records about " + firstName + " " + lastName + " have been registered.\n";
        //building a new location and putting it in the response's headers to transmit the created medical records' uri to user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(firstName, lastName)
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        result = result + location;
        log.debug("The function addMedicalRecord in MedicalRecordsController is ending without any exception.\n");
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * Update - Update medical records
     *
     * @param medicalRecordDTO - An object containing medical information to update
     * @param pathVariables - A map object containing two Strings which are first name and last name of the person whose medical records has to be updated
     * @return A String indicating the firestation which is updated with the given address
     */
    @PutMapping("/medicalRecord/{firstName}/{lastName}")
    @Transactional
    public ResponseEntity<String> updateMedicalRecords(@PathVariable Map<String, String> pathVariables, @RequestBody MedicalRecordDTO medicalRecordDTO) {
        log.debug("The function updateMedicalRecords in MedicalRecordsController is beginning.");
        //Getting firstName and lastName attributes from url
        String firstName = pathVariables.get("firstName");
        String lastName = pathVariables.get("lastName");
       //updating medical records
        String result = medicalRecordsService.updateMedicalRecord(firstName, lastName, medicalRecordDTO);
        //building a new location and putting it in the response's headers to transmit the updated medical records' uri to user
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(firstName.toUpperCase(), lastName.toUpperCase())
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        log.debug("The function updateMedicalRecords in MedicalRecordsController is ending without any exception.\n");
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     * Delete - Delete medical records about a person
     *
     * @param pathVariables - An int which is the firestation's id or a String which is the address to delete
     * @return a String indicating the firestation or the address which have been deleted
     */
    @DeleteMapping("/medicalRecord/{firstName}/{lastName}")
    @Transactional
    public ResponseEntity<String> deleteMedicalRecords(@PathVariable Map<String, String> pathVariables) {
        log.debug("The function deleteMedicalRecords in MedicalRecordsController is beginning.");
        String firstName = pathVariables.get("firstName");
        String lastName = pathVariables.get("lastName");
        String result = medicalRecordsService.deleteMedicalRecords(firstName, lastName);
        log.debug("The function deleteMedicalRecords in MedicalRecordsController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}


