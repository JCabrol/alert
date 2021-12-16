package com.safetynet.alert.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_ID")
    private int addressId;

    @Column(name = "STREET_NAME_AND_NUMBER")
    private String streetNameAndNumber;

    @Column(name = "ZIP")
    private int zip;

    @Column(name = "CITY")
    private String city;

    @OneToMany(mappedBy = "address",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    List<Person> personList = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name="STATION_ID")
    private Firestation firestation;
}
