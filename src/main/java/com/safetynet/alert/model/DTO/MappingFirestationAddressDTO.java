package com.safetynet.alert.model.DTO;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MappingFirestationAddressDTO {
    private int number;
    private String address;
    private String zip;
    private String city;
}
