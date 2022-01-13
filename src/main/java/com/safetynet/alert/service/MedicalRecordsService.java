package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.DTO.MedicalRecordDTO;
import com.safetynet.alert.model.MedicalRecords;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MedicalRecordsService {

    /**
     * Get all the medical records presents in data
     *
     * @return a list containing all the medical records
     * @throws EmptyMedicalRecordsException - when there are no medical records found
     */
    List<MedicalRecordDTO> getMedicalRecords() throws EmptyMedicalRecordsException;

    /**
     * Create a MedicalRecordDTO object containing information about medicalRecords
     *
     * @param medicalRecords - a medical records
     * @return a String with all information about the medical records presents in the list
     */
    MedicalRecordDTO transformMedicalRecordsToMedicalRecordDTO(MedicalRecords medicalRecords);

    /**
     * Get person's medical records having his first name and his last name
     *
     * @param firstName - a String which is the first name of the person whose medical records is researched
     * @param lastName  - a String which is the last name of the person whose medical records is researched
     * @return MedicalRecords object concerning the researched person
     */
    MedicalRecords getMedicalRecordsByName(String firstName, String lastName) throws MedicalRecordsNotFoundException;

    MedicalRecords addNewMedicalRecords(MedicalRecordDTO medicalRecords) throws NotRightFormatToPostException;

    /**
     * Update person's medical records having his first name and his last name and information to update
     *
     * @param firstName        - a String which is the first name of the person whose medical records is researched
     * @param lastName         - a String which is the last name of the person whose medical records is researched
     * @param medicalRecordDTO - an object containing information to update
     * @return a String containing the information which have been updated
     * @throws NotTheSamePersonException - when the information to update contains a different first name or a different last name
     * @throws NothingToUpdateException  - when there are no information to update in the medicalRecordDTO
     * @throws PersonNotFoundException   - when no person is found with the given firstName and lastName
     */
    String updateMedicalRecord(String firstName, String lastName, MedicalRecordDTO medicalRecordDTO) throws NotTheSamePersonException, NothingToUpdateException, PersonNotFoundException;


    /**
     * Delete medical records
     *
     * @param firstName -a String which is the first name of the person whose medical records have to be deleted
     * @param lastName  -a String which is the last name of the person whose medical records have to be deleted
     * @return a String which indicates the person whose medical records have been deleted
     * @throws NothingToDeleteException - when the medical records to delete don't exist anyway
     * @throws PersonNotFoundException  - when no person is found with the given firstName and lastName
     */
    String deleteMedicalRecords(String firstName, String lastName) throws NothingToDeleteException, PersonNotFoundException;
}

