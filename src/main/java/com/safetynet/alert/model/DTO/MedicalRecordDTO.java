package com.safetynet.alert.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDTO {

    String firstName;
    String lastName;
    String birthdate;
    List<String> medications = new ArrayList<>();
    List<String> allergies = new ArrayList<>();
}
