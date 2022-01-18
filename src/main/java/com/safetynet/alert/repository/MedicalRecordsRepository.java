package com.safetynet.alert.repository;


import com.safetynet.alert.model.MedicalRecords;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordsRepository extends CrudRepository<MedicalRecords, Integer> {

}
