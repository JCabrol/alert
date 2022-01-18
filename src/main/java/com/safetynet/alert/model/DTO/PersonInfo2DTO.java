package com.safetynet.alert.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonInfo2DTO {

    String firstName;
    String lastName;
    String address;
    String mail;
    String phoneNumber;
    List<String> medications;
    List<String> allergies;

}
