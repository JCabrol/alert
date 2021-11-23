package com.safetynet.alert.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "ATTACHED_ADDRESS")
public class AttachedAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private long addressId;

    @Column(name = "address")
    private String address;

    @ManyToOne(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JoinColumn(name="station_id")
    private Firestation firestation;

    public AttachedAddress(String address){
        this.address = address;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttachedAddress )) return false;
        return (Objects.equals(addressId, ((AttachedAddress) o).getAddressId()));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

