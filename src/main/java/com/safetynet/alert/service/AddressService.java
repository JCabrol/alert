package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.AddressNotFoundException;
import com.safetynet.alert.model.Address;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {

    /**
     * Get one person from his first name and last name
     *
     * @param street - a String corresponding to the street and number of the researched address
     * @param zip    - a String corresponding to the zip of the researched address
     * @param city   - a String corresponding to the city of the researched address
     * @return a Person object which corresponds to researched person
     * @throws AddressNotFoundException When the researched person is not found
     */
    Address getAddress(String street, String zip, String city) throws AddressNotFoundException;

    /**
     * Get one address from his street
     *
     * @param street - a String corresponding to the street and number of the researched address
     * @return an address object
     * @throws AddressNotFoundException When the researched address is not found
     */
    Address getAddress(String street) throws AddressNotFoundException;
}