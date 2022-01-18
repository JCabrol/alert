package com.safetynet.alert.repository;

import com.safetynet.alert.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {

    Optional<Address> findByStreetAndZipAndCity(String street, String zip, String City);

    Optional<Address> findByStreet(String street);
}


