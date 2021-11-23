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
@Table(name = "firestation")
public class Firestation {

    @Id
    @Column(name = "station_id")
    private int stationId;

    @OneToMany(mappedBy = "firestation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    List<AttachedAddress> attachedAddresses = new ArrayList<>();

    public void addAttachedAddress(AttachedAddress attachedAddress) {
        boolean addressAlreadyAttached = false;
        for (int i = 0; i < attachedAddresses.size(); i++)
            if (attachedAddresses.get(i).getAddress().equals(attachedAddress.getAddress())) {
                addressAlreadyAttached = true;
            }
        if (!addressAlreadyAttached) {
            attachedAddresses.add(attachedAddress);
            attachedAddress.setFirestation(this);
        }
    }

    public void removeAttachedAddress(AttachedAddress attachedAddress) {
        attachedAddresses.remove(attachedAddress);
        attachedAddress.setFirestation(null);
    }


    public AttachedAddress getAttachedAddress(String address) {
        AttachedAddress result = null;
        for (AttachedAddress attachedAddress : attachedAddresses) {
            if ((attachedAddress.getAddress()).replaceAll("\\s", "").equalsIgnoreCase(address.replaceAll("\\s", ""))) {
                result = attachedAddress;
            }
        }
        return result;
    }


    public String toString() {
        String result = "Firestation n°" + this.stationId + " :\n";
        if (attachedAddresses.isEmpty()) {
            result = result + "There are no addresses attached to this firestation.\n";
        } else {
            for (int i = 0; i < this.attachedAddresses.size(); i++)
                result = result + "- " + this.attachedAddresses.get(i).getAddress() + "\n";
        }
        return result + "\n";
    }
}
