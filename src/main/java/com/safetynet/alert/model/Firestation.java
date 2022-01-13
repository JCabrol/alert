package com.safetynet.alert.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FIRESTATION")
public class Firestation {

    @Id
    @Column(name = "STATION_ID")
    private int stationId;

    @OneToMany(mappedBy = "firestation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    List<Address> attachedAddresses = new ArrayList<>();


    public void addAddress(Address newAddress) {
        boolean addressAlreadyAttached = false;
        for (Address address : attachedAddresses) {
            if ((newAddress.getStreet().equals(address.getStreet())) && (newAddress.getCity().equals(address.getCity())) && (newAddress.getZip().equals(address.getZip()))) {
                addressAlreadyAttached = true;
                break;
            }
        }
        if (!addressAlreadyAttached) {
            attachedAddresses.add(newAddress);
            newAddress.setFirestation(this);
        }
    }

    public void removeAttachedAddress(Address addressToDelete) {
        attachedAddresses.remove(addressToDelete);
        addressToDelete.setFirestation(null);
    }
}
