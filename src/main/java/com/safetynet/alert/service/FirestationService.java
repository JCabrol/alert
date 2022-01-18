package com.safetynet.alert.service;


import com.safetynet.alert.exceptions.EmptyObjectException;
import com.safetynet.alert.exceptions.ObjectAlreadyExistingException;
import com.safetynet.alert.exceptions.NotRightFormatToPostException;
import com.safetynet.alert.exceptions.ObjectNotFoundException;
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
     * @throws EmptyObjectException - when there is no firestation found
     */
    List<FirestationDTO> getFirestations() throws EmptyObjectException;


    /**
     * Get one firestation from its id or one of those addresses
     *
     * @param idOrAddress - a string which is either a firestation's number or one of the firestation's addresses
     * @return a FirestationDTO object which contains information about the firestation researched, if it's found
     * @throws ObjectNotFoundException - when the firestation researched is not found
     */
    FirestationDTO getFirestationDTO(String idOrAddress) throws ObjectNotFoundException;


    /**
     * Add a new mapping address/firestation
     *
     * @param mappingFirestationAddressDTO - the id of the firestation in which the address has to be added and the address to add
     * @return a String message indicating the effectuated operations: the number of the firestation created or updated and the address created within this firestation
     * @throws NotRightFormatToPostException   - when the mapping given in parameter doesn't contain required information
     * @throws ObjectAlreadyExistingException - when the mapping given in parameter already exists
     */
    String addNewMapping(MappingFirestationAddressDTO mappingFirestationAddressDTO) throws NotRightFormatToPostException, ObjectAlreadyExistingException;


    String updateMapping(MappingFirestationAddressDTO mappingFirestationAddressDTO);

    /**
     * Delete one firestation or one address
     *
     * @param idOrAddress - a string which is either a firestation's number or one of the firestation's addresses
     * @return a String giving information about what have been deleted
     * @throws ObjectNotFoundException - when the firestation researched is not found
     */
    String deleteFirestationOrAddress(String idOrAddress) throws ObjectNotFoundException;

    /**
     * Get one firestation from its id
     *
     * @param id - an int which is the id of firestation object
     * @return the firestation researched, if it's found
     * @throws ObjectNotFoundException - when the firestation researched is not found
     */
    Firestation getFirestationById(int id) throws ObjectNotFoundException;
}

