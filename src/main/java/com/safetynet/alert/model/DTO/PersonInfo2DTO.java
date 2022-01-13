package com.safetynet.alert.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
