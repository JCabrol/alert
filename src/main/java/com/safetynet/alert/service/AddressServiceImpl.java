package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.ObjectNotFoundException;
import com.safetynet.alert.model.Address;
import com.safetynet.alert.repository.AddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Get one address from his street, zip and city
     *
     * @param street - a String corresponding to the street and number of the researched address
     * @param zip    - a String corresponding to the zip of the researched address
     * @param city   - a String corresponding to the city of the researched address
     * @return an address object
     * @throws ObjectNotFoundException When the researched address is not found
     */
    @Override
    public Address getAddress(String street, String zip, String city) throws ObjectNotFoundException {
        log.debug("The function getAddress in AddressService is beginning.");
        List<Address> addressesResearched = (List<Address>) addressRepository.findAll();
        List<Address> addressesFound = new ArrayList<>();
        if(street!=null){
        addressesFound = addressesResearched
                        .stream()
                        .filter(a -> (a.getStreet()).replaceAll("\\s", "").equalsIgnoreCase(street.replaceAll("\\s", "")))
                        .filter(a -> a.getZip().equals(zip))
                        .filter(a -> a.getCity().equalsIgnoreCase(city))
                        .collect(Collectors.toList());}

        if (addressesFound.isEmpty()) {
            log.debug("The function getAddress in AddressService is ending, no address was found.");
            throw new ObjectNotFoundException("The address " + street + " - " + zip + " " + city + " was not found.\n");
        } else {
            Address addressFound = addressesFound.get(0);
            log.debug("The function getAddress in AddressService is ending, an address was found");
            return addressFound;
        }
    }

    /**
     * Get one address from his street
     *
     * @param street - a String corresponding to the street and number of the researched address
     * @return an address object
     * @throws ObjectNotFoundException When the researched address is not found
     */
    @Override
    public Address getAddress(String street) throws ObjectNotFoundException {
        log.debug("The function getAddress in AddressService is beginning.");
        List<Address> addressesResearched = (List<Address>) addressRepository.findAll();
        List<Address> addressesFound =
                addressesResearched
                        .stream()
                        .filter(a -> (a.getStreet()).replaceAll("\\s", "").equalsIgnoreCase(street.replaceAll("\\s", "")))
                        .collect(Collectors.toList());

        if (addressesFound.isEmpty()) {
            log.debug("The function getAddress in AddressService is ending, no address was found.");
            throw new ObjectNotFoundException("The address " + street + " was not found.\n");
        } else {
            Address addressFound = addressesFound.get(0);
            log.debug("The function getAddress in AddressService is ending, an address was found");
            return addressFound;
        }
    }

}

