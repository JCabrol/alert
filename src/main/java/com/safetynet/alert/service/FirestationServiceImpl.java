package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Address;
import com.safetynet.alert.model.DTO.FirestationDTO;
import com.safetynet.alert.model.DTO.MappingFirestationAddressDTO;
import com.safetynet.alert.model.Firestation;
import com.safetynet.alert.repository.FirestationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Service
@Slf4j
public class FirestationServiceImpl implements FirestationService {

    @Autowired
    private FirestationRepository firestationRepository;

    @Autowired
    private AddressService addressService;

    /**
     * Get all the firestations presents in data
     *
     * @return a list containing all the firestations presents in data
     * @throws EmptyFirestationsException - when there is no firestation found
     */
    @Override
    public List<FirestationDTO> getFirestations() throws EmptyFirestationsException {
        log.debug("The function getFirestations in FirestationService is beginning.");
        List<Firestation> allFirestations = (List<Firestation>) firestationRepository.findAll();
        if (!allFirestations.isEmpty()) {
            List<FirestationDTO> allFirestationsDTO = allFirestations.stream().map(this::transformFirestationToFirestationDTO).collect(Collectors.toList());
            log.debug("The function getFirestations in FirestationService is ending. Some firestations were found.");
            return allFirestationsDTO;
        } else {
            log.debug("The function getFirestations in FirestationService is ending without founding any firestation.");
            throw new EmptyFirestationsException("There are no firestations registered.\n");
        }
    }

    /**
     * Get one firestation from its id
     *
     * @param id - an int which is the id of firestation object
     * @return the firestation researched, if it's found
     * @throws FirestationNotFoundException - when the firestation researched is not found
     */
    @Override
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
     * Get firestation from one of its addresses
     *
     * @param address - a string which is one of the firestation's addresses
     * @return the firestations having the researched address attached to its
     * @throws FirestationNotFoundException - when the address researched is not found in any firestation
     */
    public Firestation getFirestationByAddress(String address) throws FirestationNotFoundException {
        log.debug("The function getFirestationByAddress in FirestationService is beginning.");
        List<Firestation> allFirestations = (List<Firestation>) firestationRepository.findAll();
        List<Firestation> firestationFoundList = allFirestations
                .stream()
                .filter(f -> isAddressAttachedToFireStation(f, address))
                .collect(Collectors.toList());
        if (firestationFoundList.isEmpty()) {
            log.debug("The function getFirestationByAddress in FirestationService is ending, no firestation was found.");
            throw new FirestationNotFoundException("No firestation was found with the address : " + address);
        } else {
            Firestation firestationFound = firestationFoundList.get(0);
            log.debug("The function getFirestationByAddress in FirestationService is ending, a firestation have been found.");
            return firestationFound;
        }
    }

    /**
     * Transform a Firestation object to a FirestationDTO object which can be returned to user
     *
     * @param firestation - the firestation to transform
     * @return a FirestationDTO object which contains information about the given firestation
     */
    public FirestationDTO transformFirestationToFirestationDTO(Firestation firestation) {
        FirestationDTO firestationDTO = new FirestationDTO();
        firestationDTO.setFirestationNumber(firestation.getStationId());
        List<Address> addresses = firestation.getAttachedAddresses();
        List<String> addressesList = addresses
                .stream()
                .map(a -> a.getStreet() + " - " + a.getZip() + " " + a.getCity())
                .collect(Collectors.toList());
        firestationDTO.setAddressesList(addressesList);
        return firestationDTO;
    }

    /**
     * Get one firestation from its id or one of those addresses
     *
     * @param idOrAddress - a string which is either a firestation's number or one of the firestation's addresses
     * @return a FirestationDTO object which contains information about the firestation researched, if it's found
     * @throws FirestationNotFoundException - when the firestation researched is not found
     */
    @Override
    public FirestationDTO getFirestationDTO(String idOrAddress) throws FirestationNotFoundException {
        log.debug("The function getFirestationDTO in FirestationService is beginning.");
        Firestation firestation;
        try {
            //if the variable idOrAddress is an int, so it's a firestation's id, the function getFirestationById is called
            int id = Integer.parseInt(idOrAddress);
            firestation = getFirestationById(id);
        } catch (NumberFormatException e) {
            //if the variable idOrAddress is not an int, so it's supposed to be an address, the function getFirestationByAddress is called
            firestation = getFirestationByAddress(idOrAddress);
        }
        FirestationDTO firestationDTO = transformFirestationToFirestationDTO(firestation);
        log.debug("The function getFirestationDTO in FirestationService is ending.");
        return firestationDTO;
    }

    /**
     * Indicates if an address is attached to a firestation or not
     *
     * @param firestation - the firestation to which the address might be attached
     * @param address     - a String which represents the address which might be attached to he firestation
     * @return true if the address is attached to the firestation, false if it's not
     */
    public boolean isAddressAttachedToFireStation(Firestation firestation, String address) {
        log.debug("The function isAddressAttachedToFireStation in FirestationService is beginning.");
        boolean isAttached = false;
        for (Address attachedAddress : firestation.getAttachedAddresses()) {
            if ((attachedAddress.getStreet()).replaceAll("\\s", "").equalsIgnoreCase(address.replaceAll("\\s", ""))) {
                isAttached = true;
                break;
            }
        }
        log.debug("The function isAddressAttachedToFireStation in FirestationService is ending without any exception.");
        return (isAttached);
    }

    /**
     * Add a new mapping address/firestation
     *
     * @param mappingFirestationAddressDTO - the id of the firestation in which the address has to be added and the address to add
     * @return a String message indicating the effectuated operations: the number of the firestation created or updated and the address created within this firestation
     * @throws NotRightFormatToPostException   - when the mapping given in parameter doesn't contain required information
     * @throws MappingAlreadyExistingException - when the mapping given in parameter already exists
     */
    @Override
    public String addNewMapping(MappingFirestationAddressDTO mappingFirestationAddressDTO) throws NotRightFormatToPostException, MappingAlreadyExistingException {
        log.debug("The function addNewMapping in FirestationService is beginning.");
        String message;
        if (mappingFirestationAddressDTO.getAddress() == null) {
            int firestationNumber = mappingFirestationAddressDTO.getNumber();
            if ((firestationNumber == 0) || (firestationRepository.existsById(firestationNumber))) {
                //This exception is thrown when there is no firestation number or no address in the request's body.
                throw new NotRightFormatToPostException("There is something missing in the request :\nto post a new mapping there should be at least a \"number\" and an \"address\" fields.\n");
            } else {
                //if there is only a non-existing firestation number in the request's body, a new empty firestation is created.
                Firestation newEmptyFirestation = new Firestation();
                newEmptyFirestation.setStationId(firestationNumber);
                firestationRepository.save(newEmptyFirestation);
                message = "The Firestation number " + firestationNumber + " have been created.\n" +
                        "This Firestation isn't attached to any address yet.\n";
                log.debug("The function addNewMapping in FirestationService is ending, a new empty firestation have been created");
            }
        } else {
            //if the request's body is correct the new mapping could be created
            log.debug("The given mapping is correct");
            int id = mappingFirestationAddressDTO.getNumber();
            String street = mappingFirestationAddressDTO.getAddress();
            String zip;
            if (mappingFirestationAddressDTO.getZip() == null) {
                //default zip if there is not
                zip = "97451";
            } else {
                zip = mappingFirestationAddressDTO.getZip();
            }
            String city;
            if (mappingFirestationAddressDTO.getCity() == null) {
                //default city if there is not
                city = "CULVER";
            } else {
                city = mappingFirestationAddressDTO.getCity().toUpperCase();
            }
            Address address;
            try {
                log.debug("looking for the address");
                System.out.println(street+" - "+zip+" "+city);
                address = addressService.getAddress(street, zip, city);
                System.out.println("address id :"+address.getAddressId());
                System.out.println("street :"+address.getStreet());
                System.out.println("station id :"+address.getFirestation().getStationId());
                log.debug(("An address have been found"));
                //if the address already exists, that means
                if (address.getFirestation().getStationId() == id) {
                    throw new MappingAlreadyExistingException("The address \"" + street + " - " + zip + " " + city + "\" was already attached to the firestation number " + id + ".\n");
                } else {
                    int numberStation = address.getFirestation().getStationId();
                    throw new MappingAlreadyExistingException("The address \"" + street + " - " + zip + " " + city + "\" is already attached to the firestation number " + numberStation
                            + ",\nso it can't be attached to the firestation number " + id + ".\n"
                            + "If you want to change an address' mapping, please update it.\n");
                }
            } catch (AddressNotFoundException e) {
                log.debug("a new address is created");
                address = new Address(street, zip, city);

                Optional<Firestation> firestation = firestationRepository.findById(id);
                if (firestation.isPresent()) {
                    Firestation firestationWithNewAddress = firestation.get();
                    firestationWithNewAddress.addAddress(address);
                    firestationRepository.save(firestationWithNewAddress);
                    message = "The address \"" + street + " - " + zip + " " + city + "\" have been added to the firestation number " + id + ".\n";
                    log.info(message);
                    log.debug("The function addNewMapping in FirestationService is ending, an address have been added to an existing firestation.");
                } else {
                    Firestation newStation = new Firestation();
                    newStation.setStationId(id);
                    newStation.addAddress(address);
                    firestationRepository.save(newStation);
                    message = "The firestation number " + id + " have been created,\n" +
                            " the address \"" + street + " - " + zip + " " + city + "\" have been added to the firestation number " + id + ".\n";
                    log.info(message);
                    log.debug("The function addNewMapping in FirestationService is ending, a firestation have been created and an address have been added to the new firestation.");
                }
            }
        }
        return message;
    }

    /**
     * Delete one firestation from its id, the firestation has to be empty (with no attached address) to be deleted
     *
     * @param id - an int which is the primary key of the researched firestation
     * @throws NothingToDeleteException     - when the firestation to delete doesn't exist anyway
     * @throws FirestationNonEmptyException - when the firestation to delete is not empty
     */
    public String deleteFirestation(int id) throws FirestationNonEmptyException, NothingToDeleteException {
        log.debug("The function deleteFirestation in FirestationService is beginning.");
        Optional<Firestation> firestation = firestationRepository.findById(id);
        if (firestation.isPresent()) {
            if (firestation.get().getAttachedAddresses().isEmpty()) {
                firestationRepository.deleteById(id);
                log.debug("The function deleteFirestation in FirestationService is ending without any exception.");
                return "The Firestation number " + id + " have been deleted";
            } else {
                List<Address> addresses = firestation.get().getAttachedAddresses();
                List<String> addressesToReAttribute = addresses
                        .stream()
                        .map(a -> a.getStreet() + " - " + a.getZip() + " " + a.getCity())
                        .collect(Collectors.toList());
                throw new FirestationNonEmptyException("The firestation number " + id + " cannot be deleted,\n" +
                        "some addresses are still attached to this firestation:\n" + addressesToReAttribute.stream().map(s -> s + "\n").collect(Collectors.joining())
                        + "Please reassign this addresses to another firestation before deleting.");
            }

        } else {
            throw new NothingToDeleteException("The firestation number " + id + " was not found," +
                    "\nso it couldn't have been deleted");
        }
    }


    /**
     * Delete one address from the firestation in which it is attached
     *
     * @param address - a string which is the address to be deleted
     * @return a String which indicates the firestations within the address has been deleted
     * @throws NothingToDeleteException - when the address to delete doesn't exist anyway
     */
    public String deleteAddressMapping(Address address) throws NothingToDeleteException {
        log.debug("The function deleteAddress in FirestationService is beginning.");
        Firestation firestation = address.getFirestation();
        if (firestation != null) {
            firestation.removeAttachedAddress(address);
            firestationRepository.save(firestation);
            String message = "The address " + address.getStreet() + " - " + address.getZip() + " " + address.getCity() + " have been removed from firestation number " + firestation.getStationId() + ".\n";
            log.info(message);
            log.debug("The function deleteAddressFromFirestation in FirestationService is ending without any exception.");
            return message;
        } else {
            throw new NothingToDeleteException("The address " + address.getStreet() + " - " + address.getZip() + " " + address.getCity() + " wasn't attached to any firestation, so no mapping couldn't have been deleted.\n");
        }
    }


    @Override
    public String updateMapping(MappingFirestationAddressDTO mappingFirestationAddressDTO) {
        log.debug("The function updateMapping in FirestationService is beginning.");
        if (firestationRepository.existsById(mappingFirestationAddressDTO.getNumber())) {
            String street = mappingFirestationAddressDTO.getAddress();
            String zip = mappingFirestationAddressDTO.getZip();
            if (zip == null) {
                zip = "97451";
            }
            String city = mappingFirestationAddressDTO.getCity();
            if (city == null) {
                city = "CULVER";
            }
            String result = "";
            Address address = addressService.getAddress(street, zip, city);
            try {
                result = deleteAddressMapping(address);
            } catch (NothingToDeleteException ignored) {
            }
            result = result + addNewMapping(mappingFirestationAddressDTO);
            log.debug("The function deleteAddress in FirestationService is ending without any exception.");
            return result;
        } else {
            throw new FirestationNotFoundException("The Firestation number " + mappingFirestationAddressDTO.getNumber() + " was not found," +
                    "\nso the new mapping couldn't have been done.\n");
        }
    }

    /**
     * Delete one firestation or one address
     *
     * @param idOrAddress - a string which is either a firestation's number or one of the firestation's addresses
     * @return a String giving information about what have been deleted
     * @throws FirestationNotFoundException - when the firestation researched is not found
     */
    @Override
    public String deleteFirestationOrAddress(String idOrAddress) throws FirestationNotFoundException {
        log.debug("The function deleteFirestationOrAddress in FirestationService is beginning.");
        String result;
        try {
            //if the variable idOrAddress is an int, so it's a firestation's id, the function getFirestationById is called
            int id = Integer.parseInt(idOrAddress);
            result = deleteFirestation(id);
        } catch (NumberFormatException e) {
            //if the variable idOrAddress is not an int, so it's supposed to be an address, the function getFirestationByAddress is called
            Address address = addressService.getAddress(idOrAddress);
            result = deleteAddressMapping(address);
        }
        log.debug("The function getFirestationDTO in FirestationService is ending.");
        return result;
    }

}

