package com.safetynet.alert.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonInfoDTO {

    String firstName;
    String lastName;
    String phoneNumber;
    int age;
    List<String> medications;
    List<String> allergies;

}
