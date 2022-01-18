package com.safetynet.alert.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
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

    @Column(name = "STREET")
    private String street;

    @Column(name = "ZIP")
    @Size(min = 5, max = 5, message = "The zip should contains 5 characters.")
    private String zip;

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

    public Address(String street, String zip, String city){
        this.street = street;
        this.zip=zip;
        this.city=city;
    }

    public void addPerson(Person person) {
        this.personList.add(person);
        person.setAddress(this);
    }

}
