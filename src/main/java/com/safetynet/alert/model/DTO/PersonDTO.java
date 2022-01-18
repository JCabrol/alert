package com.safetynet.alert.model.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

    private String firstName;
    private String lastName;
    private String address;
    private String zip;
    private String city;
    private String phoneNumber;
    private String mail;

}
