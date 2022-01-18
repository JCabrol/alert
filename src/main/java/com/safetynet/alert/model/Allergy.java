package com.safetynet.alert.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "ALLERGY")
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALLERGY_ID")
    private long allergyId;

    @Column(name = "ALLERGY_NAME")
    private String allergyName;

    @ManyToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "MEDICAL_ID")
    private MedicalRecords medicalRecords;

    public Allergy(String allergy) {
        this.allergyName = allergy;
    }
}

