package com.safetynet.alert.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirestationInfoDTO {
    private int stationId;
    private int numberOfChildren;
    private int numberOfAdults;
    private List<PersonFirestationDTO> personsCoveredByStation;
}
