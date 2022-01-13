package com.safetynet.alert.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "PERSON")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "FIRST_NAME", updatable = false)
    @NonNull
    private String firstName;

    @Column(name = "LAST_NAME", updatable = false)
    @NonNull
    private String lastName;

    @ManyToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @Column(name = "PHONE_NUMBER")
    @Size(min = 10, max = 10, message = "The phone number should contains 10 characters.")
    private String phoneNumber;

    @Column(name = "MAIL")
    @Email(message = "Email should be valid.")
    private String mail;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "MEDICAL_ID")
    private MedicalRecords medicalRecords;


    public MedicalRecords getMedicalRecords() {
        return medicalRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        return (Objects.equals(id, ((Person) o).getId()));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

//    public String toString() {
//        return "Name : " + this.firstName + " " + this.lastName + "\n" +
//                "Address : " + this.address + " - " +
//                this.zip + " " + this.city + "\n" +
//                "Phone number : " + this.phoneNumber + "\n" +
//                "Mail : " + this.mail + "\n" + "\n";
//    }
}
