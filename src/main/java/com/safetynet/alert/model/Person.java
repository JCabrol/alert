package com.safetynet.alert.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Entity
@Table(name = "PERSON")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Person {

    @Id
    @GeneratedValue(generator = "nameId-generator")
    @GenericGenerator(name = "nameId-generator",
            strategy = "com.safetynet.alert.generator.MyGenerator")
    private String id;

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

}
