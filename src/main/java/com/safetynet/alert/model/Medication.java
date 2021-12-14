package com.safetynet.alert.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "MEDICATION")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private long medicationId;

    @Column(name = "medication_name")
    private String medicationName;

    @ManyToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name="medical_id")
    private MedicalRecords medicalRecords;

    public Medication(String medicationName){
        this.medicationName = medicationName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medication)) return false;
        return (Objects.equals(medicationId, ((Medication) o).getMedicationId()));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

