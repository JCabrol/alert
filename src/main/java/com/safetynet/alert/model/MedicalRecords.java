package com.safetynet.alert.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MEDICAL_RECORDS")
public class MedicalRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEDICAL_ID")
    private int medicalId;

    @Column(name = "BIRTHDATE")
    private String birthdate;

    @Column(name = "BIRTHDATE2")
    private LocalDate birthdate2;

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
        boolean medicationAlreadyAttached = false;
        for (Medication value : medications)
            if (value.getMedicationName().equals(medication.getMedicationName())) {
                medicationAlreadyAttached = true;
                break;
            }
        if (!medicationAlreadyAttached) {
            medications.add(medication);
            medication.setMedicalRecords(this);
        }
    }

    public void addAllergy(Allergy allergy) {
        boolean allergyAlreadyAttached = false;
        for (Allergy value : allergies)
            if (value.getAllergyName().equals(allergy.getAllergyName())) {
                allergyAlreadyAttached = true;
                break;
            }
        if (!allergyAlreadyAttached) {
            allergies.add(allergy);
            allergy.setMedicalRecords(this);
        }
    }

    public void addPerson(Person person) {
        this.person = person;
        person.setMedicalRecords(this);
    }


    public void removePerson(Person person) {
        this.setPerson(null);
        person.setMedicalRecords(null);
    }

    public void removeMedication(Medication medication) {
        medications.remove(medication);
        medication.setMedicalRecords(null);
    }

    public void removeAllergy(Allergy allergy) {
        allergies.remove(allergy);
        allergy.setMedicalRecords(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalRecords)) return false;
        return (Objects.equals(medicalId, ((MedicalRecords) o).getMedicalId()));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        if ((this.person == null)) {
            result.append("Medical records number ").append(this.getMedicalId()).append(" :\n");
        } else {
            result.append("Medical records about ").append(this.person.getFirstName().toUpperCase()).append(" ").append(this.person.getLastName().toUpperCase()).append(" :\n");
        }
        result.append("Birthdate : ").append(this.birthdate).append("\n");
        if (medications.isEmpty()) {
            result.append("This person has no medication.\n");
        } else {
            result.append("Medications :\n");
            for (Medication medication : this.medications) {
                result.append("- ").append(medication.getMedicationName()).append("\n");
            }
        }
        if (allergies.isEmpty()) {
            result.append("This person has no allergy.\n");
        } else {
            result.append("Allergies :\n");
            for (Allergy allergy : this.allergies) {
                result.append("- ").append(allergy.getAllergyName()).append("\n");
            }
        }
        result.append("\n");
        return result.toString();
    }
}
