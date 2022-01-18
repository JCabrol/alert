package com.safetynet.alert.service;

import com.safetynet.alert.model.DTO.ChildInfoDTO;
import com.safetynet.alert.model.DTO.FireInfoDTO;
import com.safetynet.alert.model.DTO.FirestationInfoDTO;
import com.safetynet.alert.model.DTO.PersonInfo2DTO;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface UrlsService {

    FirestationInfoDTO getPersonsCoveredByFirestation(int stationNumber);

    List<ChildInfoDTO> getChildrenByAddress(String address);

    List<String> getPhoneNumbersByFirestation(int stationId);

    FireInfoDTO getPersonsByAddress(String address);

    List<FireInfoDTO> getHouseholdsByStation(List<Integer> stationNumbers);

    List<PersonInfo2DTO> getPersonsByName(String firstName, String lastName);

    List<String> getMailsByCity(String city);
}
