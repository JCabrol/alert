package com.safetynet.alert.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "MEDICATION")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEDICATION_ID")
    private long medicationId;

    @Column(name = "MEDICATION_NAME")
    private String medicationName;

    @ManyToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "MEDICAL_ID")
    private MedicalRecords medicalRecords;

    public Medication(String medicationName) {
        this.medicationName = medicationName;
    }
}

