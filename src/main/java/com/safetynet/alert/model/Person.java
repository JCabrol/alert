package com.safetynet.alert.model;

import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "PERSONS")
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

    private String address;

    private String city;

    private int zip;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    private String mail;

}
