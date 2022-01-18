package com.safetynet.alert.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
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
