package com.safetynet.alert.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MEDICAL_RECORDS")
public class MedicalRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEDICAL_ID")
    private int medicalId;

    @Column(name = "BIRTHDATE")
    private LocalDate birthdate;

    @OneToOne(mappedBy = "medicalRecords",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Person person;

    @OneToMany(mappedBy = "medicalRecords",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    List<Medication> medications = new ArrayList<>();

    @OneToMany(mappedBy = "medicalRecords",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    List<Allergy> allergies = new ArrayList<>();


    public void addMedication(Medication medication) {
        medications.add(medication);
        medication.setMedicalRecords(this);
    }


    public void addAllergy(Allergy allergy) {
        allergies.add(allergy);
        allergy.setMedicalRecords(this);
    }


    public void addPerson(Person person) {
        this.person = person;
        person.setMedicalRecords(this);
    }

    public void removePerson(Person person) {
        this.setPerson(null);
        person.setMedicalRecords(null);
    }
}
