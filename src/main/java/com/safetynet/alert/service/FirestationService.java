package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.EmptyFirestationsException;
import com.safetynet.alert.exceptions.FirestationNotFoundException;
import com.safetynet.alert.exceptions.MappingAlreadyExistingException;
import com.safetynet.alert.exceptions.NotRightFormatToPostException;
import com.safetynet.alert.model.DTO.FirestationDTO;
import com.safetynet.alert.model.DTO.MappingFirestationAddressDTO;
import com.safetynet.alert.model.Firestation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FirestationService {


    /**
     * Get all the firestations presents in data
     *
     * @return a list containing all the firestations presents in data
     * @throws EmptyFirestationsException - when there is no firestation found
     */
    List<FirestationDTO> getFirestations() throws EmptyFirestationsException;


    /**
     * Get one firestation from its id or one of those addresses
     *
     * @param idOrAddress - a string which is either a firestation's number or one of the firestation's addresses
     * @return a FirestationDTO object which contains information about the firestation researched, if it's found
     * @throws FirestationNotFoundException - when the firestation researched is not found
     */
    FirestationDTO getFirestationDTO(String idOrAddress) throws FirestationNotFoundException;


    /**
     * Add a new mapping address/firestation
     *
     * @param mappingFirestationAddressDTO - the id of the firestation in which the address has to be added and the address to add
     * @return a String message indicating the effectuated operations: the number of the firestation created or updated and the address created within this firestation
     * @throws NotRightFormatToPostException   - when the mapping given in parameter doesn't contain required information
     * @throws MappingAlreadyExistingException - when the mapping given in parameter already exists
     */
    String addNewMapping(MappingFirestationAddressDTO mappingFirestationAddressDTO) throws NotRightFormatToPostException, MappingAlreadyExistingException;


    String updateMapping(MappingFirestationAddressDTO mappingFirestationAddressDTO);

    /**
     * Delete one firestation or one address
     *
     * @param idOrAddress - a string which is either a firestation's number or one of the firestation's addresses
     * @return a String giving information about what have been deleted
     * @throws FirestationNotFoundException - when the firestation researched is not found
     */
    String deleteFirestationOrAddress(String idOrAddress) throws FirestationNotFoundException;

    /**
     * Get one firestation from its id
     *
     * @param id - an int which is the id of firestation object
     * @return the firestation researched, if it's found
     * @throws FirestationNotFoundException - when the firestation researched is not found
     */
    Firestation getFirestationById(int id) throws FirestationNotFoundException;
}

