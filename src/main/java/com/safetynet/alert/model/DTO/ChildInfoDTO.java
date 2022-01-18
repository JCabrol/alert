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
public class ChildInfoDTO {

private String firstName;
private String lastName;
private int age;
private String address;
private List<String> householdMembers;
}
