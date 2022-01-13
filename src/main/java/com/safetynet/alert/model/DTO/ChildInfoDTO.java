package com.safetynet.alert.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChildInfoDTO {

private String firstName;
private String lastName;
private int age;
private String address;
private List<String> householdMembers;
}
