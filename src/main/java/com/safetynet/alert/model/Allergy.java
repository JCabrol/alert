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
    @JoinColumn(name="MEDICAL_ID")
    private MedicalRecords medicalRecords;

    public Allergy(String allergy){
        this.allergyName = allergy;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Allergy)) return false;
        return (Objects.equals(allergyId, ((Allergy) o).getAllergyId()));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

