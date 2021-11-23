package com.safetynet.alert.repository;


import com.safetynet.alert.model.Firestation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirestationRepository extends CrudRepository<Firestation, Integer> {

}
