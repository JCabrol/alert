package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.AttachedAddress;
import com.safetynet.alert.model.Firestation;
import com.safetynet.alert.model.MappingFirestationAddress;
import com.safetynet.alert.repository.FirestationRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter

@Service
@Slf4j
public class FirestationService {

    @Autowired
    private FirestationRepository firestationRepository;

    /**
     * Get all the firestations presents in data
     *
     * @return a list containing all the firestations presents in data
     * @throws EmptyFirestationsException - when there is no firestation found
     */
    public List<Firestation> getFirestations() throws EmptyFirestationsException {
        log.debug("The function getFirestations in FirestationService is beginning.");
        List<Firestation> allFirestations = (List<Firestation>) firestationRepository.findAll();
        if (!allFirestations.isEmpty()) {
            log.debug("The function getFirestations in FirestationService is ending. Some firestations were found.");
            return allFirestations;
        } else {
            log.debug("The function getFirestations in FirestationService is ending without founding any firestation.");
            throw new EmptyFirestationsException("There are no firestations registered.\n");
        }
    }

    /**
     * Create a String containing information about firestations in a list
     *
     * @param firestations - a list of firestations
     * @return a String with all information about the firestations presents in the list
     */
    public String firestationsToString(List<Firestation> firestations) {
        log.debug("The function firestationsToString in FirestationService is beginning.");
        StringBuilder result = new StringBuilder();
        for (Firestation firestation : firestations) {
            result.append(firestation.toString());
        }
        log.debug("The function firestationsToString in FirestationService is ending  without any exception.");
        return result.toString();
    }

    /**
     * Get one firestation from his id
     *
     * @param id - an int which is the id of firestation object
     * @return the firestation researched, if it's found
     * @throws FirestationNotFoundException - when the firestation researched is not found
     */
    public Firestation getFirestationById(int id) throws FirestationNotFoundException {
        log.debug("The function getFirestationById in FirestationService is beginning.");
        Optional<Firestation> firestation = firestationRepository.findById(id);
        if (firestation.isPresent()) {
            Firestation firestationFound = firestation.get();
            log.debug("The function getFirestationById in FirestationService is ending, a firestation have been found.");
            return firestationFound;
        } else {
            log.debug("The function getFirestationById in FirestationService is ending, no firestation was found.");
            throw new FirestationNotFoundException("The firestation with Id number " + id + " was not found.\n");
        }
    }

    /**
     * Get firestations from one of theirs addresses
     *
     * @param address - a string which is one of the firestation's addresses
     * @return a list of the firestations having the researched address attached to them
     * @throws FirestationNotFoundException - when the address researched is not found in any firestation
     */
    public List<Firestation> getFirestationByAddress(String address) throws FirestationNotFoundException {
        log.debug("The function getFirestationByAddress in FirestationService is beginning.");
        List<Firestation> firestationsFound = new ArrayList<>();
        List<Firestation> allFirestations = (List<Firestation>) firestationRepository.findAll();
        for (Firestation firestation : allFirestations) {
            if (isAddressAttachedToFireStation(firestation, address)) {
                firestationsFound.add(firestation);
            }
        }
        if (!firestationsFound.isEmpty()) {
            log.debug("The function getFirestationByAddress in FirestationService is ending. Some firestations were found.");
            return firestationsFound;
        } else {
            log.debug("The function getFirestationByAddress in FirestationService is ending, no firestation was found.");
            throw new FirestationNotFoundException("The firestation with address " + address + " was not found.\n");
        }
    }

    /**
     * Get firestations from one of theirs addresses
     *
     * @param mappingFirestationAddress - the id of the firestation in which the address has to be added and the address to add
     * @return a String message indicating the effectuated operations: the number of the firestation created or updated and the address created within this firestation
     * @throws NotRightFormatToPostException   - when the mapping given in parameter doesn't contain required information
     * @throws MappingAlreadyExistingException - when the mapping given in parameter already exists
     */
    // public String addNewMapping(int firestationId, String address) throws NotRightFormatToPostException, MappingAlreadyExistingException {
    public String addNewMapping(MappingFirestationAddress mappingFirestationAddress) throws NotRightFormatToPostException, MappingAlreadyExistingException {
        log.debug("The function addNewMapping in FirestationService is beginning.");

        String message;
        if ((mappingFirestationAddress.getFirestationId() == 0) || (mappingFirestationAddress.getAddress() == null)) {
            throw new NotRightFormatToPostException("There is something missing in the request :\nto post a new mapping there should be a \"firestationId\" and an \"address\" fields.\n");
        } else {
            int id = mappingFirestationAddress.getFirestationId();
            String address = mappingFirestationAddress.getAddress();
            Optional<Firestation> firestation = firestationRepository.findById(id);
            if (firestation.isPresent()) {
                Firestation firestationWithNewAddress = firestation.get();
                if (!isAddressAttachedToFireStation(firestationWithNewAddress, address)) {
                    firestationWithNewAddress.addAttachedAddress(new AttachedAddress(address));
                    firestationRepository.save(firestationWithNewAddress);
                    message = "The firestation number " + id + " was already existing,\n" +
                            "the address \"" + address + "\" have been added to this firestation.";
                    log.info(message);
                    log.debug("The function addNewMapping in FirestationService is ending, an address have been added to an existing firestation.");
                } else {
                    throw new MappingAlreadyExistingException("The address " + address + " was already attached to the firestation number " + id + ".\n");
                }
            } else {
                Firestation newStation = new Firestation();
                newStation.setStationId(id);
                newStation.addAttachedAddress(new AttachedAddress(address));
                firestationRepository.save(newStation);
                message = "The firestation number " + id + " have been created,\n" +
                        " the address \"" + address + "\" have been added to this firestation.";
                log.info(message);
                log.debug("The function addNewMapping in FirestationService is ending, a firestation have been created and an address have been added to the new firestation.");
            }
        }
        return message;
    }

//
//        if (firestationId != 0) {
//            try {
//
//                if (firestation.isPresent()) {
//                    Firestation firestationWithNewAddress = firestation.get();
//                    if (!isAddressAttachedToFireStation(firestationWithNewAddress, address)) {
//                        firestationWithNewAddress.addAttachedAddress(new AttachedAddress(address));
//                        firestationRepository.save(firestationWithNewAddress);
//                        message = "The firestation number " + id + " was already existing,\n" +
//                                "the address \"" + address + "\" have been added to this firestation.";
//                        log.info(message);
//                        log.debug("The function addNewMapping in FirestationService is ending, an address have been added to an existing firestation.");
//                    } else {
//                        throw new MappingAlreadyExistingException("The address " + address + " was already attached to the firestation number " + firestationId + ".\n");
//                    }
//                } else {
//                    Firestation newStation = new Firestation();
//                    newStation.setStationId(firestationId);
//                    newStation.addAttachedAddress(new AttachedAddress(address));
//                    firestationRepository.save(newStation);
//                    message = "The firestation number " + firestationId + " have been created,\n" +
//                            " the address \"" + address + "\" have been added to this firestation.";
//                    log.info(message);
//                    log.debug("The function addNewMapping in FirestationService is ending, a firestation have been created and an address have been added to the new firestation.");
//                }
//                return message;
//            } catch (NullPointerException exception) {
//                throw new NotRightFormatToPostException("There is something missing in the request :\nto post a new mapping there should be a \"firestationId\" and an \"address\" fields.\n");
//            }
//        } else {
//            throw new NotRightFormatToPostException("There is something missing in the request :\nto post a new mapping there should be a \"firestationId\" and an \"address\" fields.\n");
//        }
//    }

    /**
     * Delete one firestation from its id, the firestation has to be empty (with no attached address) to be deleted
     *
     * @param id - an int which is the primary key of the researched firestation
     * @throws NothingToDeleteException     - when the firestation to delete doesn't exist anyway
     * @throws FirestationNonEmptyException - when the firestation to delete is not empty
     */
    public void deleteFirestation(int id) throws FirestationNonEmptyException, NothingToDeleteException {
        log.debug("The function deleteFirestation in FirestationService is beginning.");
        Optional<Firestation> firestation = firestationRepository.findById(id);
        if (firestation.isPresent()) {
            if (firestation.get().getAttachedAddresses().isEmpty()) {
                firestationRepository.deleteById(id);
            } else {
                StringBuilder addressesToReAttribute = new StringBuilder();
                List<AttachedAddress> addresses = firestation.get().getAttachedAddresses();
                for (AttachedAddress attachedAddress : addresses) {
                    addressesToReAttribute.append("\n- ").append(attachedAddress.getAddress());
                }
                throw new FirestationNonEmptyException("The firestation number " + id + " cannot be deleted,\n" +
                        "some addresses are still attached to this firestation:" + addressesToReAttribute
                        + "\nPlease reassign this addresses to another firestation before deleting.");
            }
            log.debug("The function deleteFirestation in FirestationService is ending.");
        } else {
            throw new NothingToDeleteException("The firestation number " + id + "was not found," +
                    "\nso it couldn't have been deleted");
        }
    }


    /**
     * Delete one address from the firestation(s) in which it is attached
     *
     * @param address - a string which is the address to be deleted
     * @return a String which indicates the firestations within the address has been deleted
     * @throws NothingToDeleteException - when the address to delete doesn't exist anyway
     */

    public String deleteAddress(String address) throws NothingToDeleteException {
        log.debug("The function deleteAddress in FirestationService is beginning.");
        boolean addressDeleted = false;
        StringBuilder result = new StringBuilder();
        List<Firestation> allFirestations = (List<Firestation>) firestationRepository.findAll();
        for (Firestation firestation : allFirestations) {
            int firestationId = firestation.getStationId();
            if (isAddressAttachedToFireStation(firestation, address)) {
                firestation.removeAttachedAddress(firestation.getAttachedAddress(address));
                firestationRepository.save(firestation);
                String message = "The address " + address + " have been deleted from firestation number " + firestationId + ".";
                log.info(message);
                result.append(message).append("\n");
                addressDeleted = true;
            }
        }
        if (addressDeleted) {
            log.debug("The function deleteAddressFromFirestation in FirestationService is ending.");
            return result.toString();
        } else {
            throw new NothingToDeleteException("The address " + address + " wasn't found, so it couldn't have been deleted.\n");
        }
    }

//    /**
//     * Save a firestation object in data
//     *
//     * @param firestation - A firestation object which has to be saved in data
//     * @return the firestation object which was saved
//     */
//    public Firestation saveFirestation(Firestation firestation) {
//        log.debug("The function saveFirestation in FirestationService is beginning.");
//        Firestation savedFirestation = firestationRepository.save(firestation);
//        log.debug("The function saveFirestation in FirestationService is ending.");
//        return savedFirestation;
//    }

//    /**
//     * Save a new address in a firestation object
//     *
//     * @param firestation - A firestation in which a new address has to be saved
//     * @param address     - A String which is a new address to add to the firestation
//     * @return An optional firestation object in which address was saved if it was found
//     * @throws MappingAlreadyExistingException - when the given address is already attached to the given firestation
//     */
//    public Firestation saveAddressInFirestation(Firestation firestation, String address) throws MappingAlreadyExistingException {
//        log.debug("The function saveAddressInFirestation in FirestationService is beginning.");
//        int stationNumber = firestation.getStationId();
//        if (!isAddressAttachedToFireStation(stationNumber, address)) {
//            firestation.addAttachedAddress(new AttachedAddress(address));
//            Firestation result = firestationRepository.save(firestation);
//            log.debug("The function saveAddressInFirestation in FirestationService is ending, an address have been saved.");
//            return result;
//        } else {
//            throw new MappingAlreadyExistingException("The address " + address + " was already attached to the firestation number " + stationNumber + ".\n");
//        }
//    }

    /**
     * Indicates if an address is attached to a firestation
     *
     * @param firestation - An int which is the id of the firestation researched
     * @param address     - A String which is the address to research
     * @return true if the address is attached to the firestation, false if it's not
     */
    public boolean isAddressAttachedToFireStation(Firestation firestation, String address) {
        log.debug("The function isAddressAttachedToFireStation in FirestationService is beginning.");
        boolean isAttached = false;
        for (AttachedAddress attachedAddress : firestation.getAttachedAddresses()) {
            if ((attachedAddress.getAddress()).replaceAll("\\s", "").equalsIgnoreCase(address.replaceAll("\\s", ""))) {
                isAttached = true;
                break;
            }
        }
        log.debug("The function isAddressAttachedToFireStation in FirestationService is ending without any exception.");
        return (isAttached);
    }

//    /**
//     * Calculate the total number of firestations registered
//     *
//     * @return the number of firestations
//     */
//    public long numberOfFirestations() {
//        log.debug("The function numberOfFirestations in FirestationService is beginning.");
//        long numberOfFirestations = firestationRepository.count();
//        log.debug("The function numberOfFirestations in FirestationService is ending.\n");
//        return numberOfFirestations;
//    }
//
//    /**
//     * Calculate the total number of firestations registered
//     *
//     * @return the number of firestations
//     */
//    public int getMaximumFirestationId() {
//        log.debug("The function getMaximumFirestationId in FirestationService is beginning.");
//        List<Firestation> allFirestations = (List<Firestation>) firestationRepository.findAll();
//        Firestation maxById = allFirestations
//                .stream()
//                .max(Comparator.comparing(Firestation::getStationId))
//                .orElseThrow(NoSuchElementException::new);
//        int maximumFirestationId = maxById.getStationId();
//        log.debug("The function getMaximumFirestationId in FirestationService is ending.\n");
//        return maximumFirestationId;
//    }
//
//    /**
//     * Calculate the total number of firestations registered
//     *
//     * @return the number of firestations
//     */
//    public int numberOfAddresses(int firestationId) {
//        log.debug("The function numberOfAddresses in FirestationService is beginning.");
//        Optional<Firestation> firestation = firestationRepository.findById(firestationId);
//        if (firestation.isPresent()) {
//            int numberOfAddresses = firestation.get().getAttachedAddresses().size();
//            log.debug("The function numberOfFirestations in FirestationService is ending.\n");
//            return numberOfAddresses;
//        } else {
//            throw new FirestationNotFoundException("The firestation number " + firestationId + " was not found.\n");
//        }
//    }
//
//    public boolean existFirestationById(int firestationId) {
//        log.debug("The function existsFirestationById in FirestationService is beginning.");
//        return firestationRepository.findById(firestationId).isPresent();
//    }

}

