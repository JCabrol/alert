package com.safetynet.alert.model.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonFirestationDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String phonenumber;
    @JsonIgnore
    private boolean isChild;
}

