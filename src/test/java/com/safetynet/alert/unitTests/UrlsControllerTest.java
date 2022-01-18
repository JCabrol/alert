package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.ObjectNotFoundException;
import com.safetynet.alert.model.DTO.*;
import com.safetynet.alert.service.UrlsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("UrlsTests")
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
public class UrlsControllerTest {

    @MockBean
    private UrlsService urlsService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @Tag("UrlsControllerTests")
    @DisplayName("getPersonsCoveredByStation tests:")
    class GetPersonsCoveredByStationTests {
        @Test
        @DisplayName("GIVEN persons covered by a station, " +
                "WHEN we call the uri \"/firestation?stationNumber=<station_number>\", " +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getPersonsCoveredByStationTest() throws Exception {
            // GIVEN
            //an existing person with medical records
            PersonFirestationDTO personFirestationDTO1 = new PersonFirestationDTO("firstName1", "lastName1", "address1", "phonenumber1", true);
            PersonFirestationDTO personFirestationDTO2 = new PersonFirestationDTO("firstName2", "lastName2", "address2", "phonenumber2", false);
            List<PersonFirestationDTO> personFirestationDTOList = new ArrayList<>();
            personFirestationDTOList.add(personFirestationDTO1);
            personFirestationDTOList.add(personFirestationDTO2);
            FirestationInfoDTO firestationInfoDTO = new FirestationInfoDTO(1, 1, 1, personFirestationDTOList);
            doReturn(firestationInfoDTO).when(urlsService).getPersonsCoveredByFirestation(1);
            // WHEN
            //we call the uri "/medicalRecords/{firstName}/{lastName}"
            mockMvc.perform(get("/firestation")
                            .param("stationId", "1"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information about the medical records found
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.stationId", is(1)))
                    .andExpect(jsonPath("$.numberOfChildren", is(1)))
                    .andExpect(jsonPath("$.numberOfAdults", is(1)))
                    .andExpect(jsonPath("$.personsCoveredByStation", hasSize(2)));
            verify(urlsService, Mockito.times(1)).getPersonsCoveredByFirestation(1);
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation, " +
                "WHEN we call the uri \"/firestation?stationNumber=<station_number>\", " +
                "THEN when should have an \"isNotFound\" status and the expected error message.")
        public void getPersonsCoveredByStationNonExistingTest() throws Exception {
            // GIVEN
            //a non-existing firestation
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(urlsService).getPersonsCoveredByFirestation(1);
            // WHEN
            //we call the uri "/firestation?stationNumber=<station_number>"
            mockMvc.perform(get("/firestation")
                            .param("stationId", "1"))
                    // THEN
                    //when should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(urlsService, Mockito.times(1)).getPersonsCoveredByFirestation(1);
        }

        @Test
        @DisplayName("GIVEN a request parameter missing, " +
                "WHEN we call the uri \"/firestation?stationNumber=<station_number>\", " +
                "THEN when should have an \"isBadRequest\" status and the expected error message.")
        public void getPersonsCoveredByStationMissingParamTest() throws Exception {
            // GIVEN
            //a request parameter missing
            // WHEN
            //we call the uri "/firestation?stationNumber=<station_number>"
            mockMvc.perform(get("/firestation"))
                    // THEN
                    //when should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("A request parameter is missing.\n"));
            verify(urlsService, Mockito.times(0)).getPersonsCoveredByFirestation(anyInt());
        }
    }

    @Nested
    @Tag("UrlsControllerTests")
    @DisplayName("getChildrenByAddress tests:")
    class GetChildrenByAddressTests {
        @Test
        @DisplayName("GIVEN children living at an address, " +
                "WHEN we call the uri /childAlert?address=<address>\", " +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getChildrenByAddressTest() throws Exception {
            // GIVEN
            //children living at an address
            ChildInfoDTO childInfoDTO1 = new ChildInfoDTO("firstName1", "lastName1", 7, "phonenumber1", new ArrayList<>());
            ChildInfoDTO childInfoDTO2 = new ChildInfoDTO("firstName2", "lastName2", 9, "phonenumber1", new ArrayList<>());
            List<ChildInfoDTO> childInfoDTOList = new ArrayList<>();
            childInfoDTOList.add(childInfoDTO1);
            childInfoDTOList.add(childInfoDTO2);
            doReturn(childInfoDTOList).when(urlsService).getChildrenByAddress("address test");
            // WHEN
            //we call the uri /childAlert?address=<address>
            mockMvc.perform(get("/childAlert")
                            .param("address", "address test"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$.[0].firstName", is("firstName1")))
                    .andExpect(jsonPath("$.[1].firstName", is("firstName2")))
                    .andExpect(jsonPath("$.[0].householdMembers", hasSize(0)))
                    .andExpect(jsonPath("$.[1].householdMembers", hasSize(0)));
            verify(urlsService, Mockito.times(1)).getChildrenByAddress("address test");
        }

        @Test
        @DisplayName("GIVEN not any child living at an address, " +
                "WHEN we call the uri /childAlert?address=<address>\", " +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with empty list.")
        public void getChildrenByAddressNoChildTest() throws Exception {
            // GIVEN
            // not any child living at an address
            List<ChildInfoDTO> childInfoDTOList = new ArrayList<>();
            doReturn(childInfoDTOList).when(urlsService).getChildrenByAddress("address test");
            // WHEN
            //we call the uri /childAlert?address=<address>
            mockMvc.perform(get("/childAlert")
                            .param("address", "address test"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with empty list
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
            verify(urlsService, Mockito.times(1)).getChildrenByAddress("address test");
        }

        @Test
        @DisplayName("GIVEN a non-existing address, " +
                "WHEN we call the uri /childAlert?address=<address>\", " +
                "THEN when should have an \"isNotFound\" status and the expected error message.")
        public void getChildrenByAddressNonExistingTest() throws Exception {
            // GIVEN
            //a non-existing address
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(urlsService).getChildrenByAddress("address test");
            // WHEN
            //we call the uri /childAlert?address=<address>
            mockMvc.perform(get("/childAlert")
                            .param("address", "address test"))
                    // THEN
                    //when should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(urlsService, Mockito.times(1)).getChildrenByAddress("address test");
        }

        @Test
        @DisplayName("GIVEN a request parameter missing, " +
                "WHEN we call the uri /childAlert?address=<address>\", " +
                "THEN when should have an \"isBadRequest\" status and the expected error message.")
        public void getChildrenByAddressMissingParamTest() throws Exception {
            // GIVEN
            //a request parameter missing
            // WHEN
            //we call the uri /childAlert?address=<address>
            mockMvc.perform(get("/childAlert"))
                    // THEN
                    //when should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("A request parameter is missing.\n"));
            verify(urlsService, Mockito.times(0)).getChildrenByAddress(anyString());
        }
    }

    @Nested
    @Tag("UrlsControllerTests")
    @DisplayName("getPhoneNumbersByFirestation tests:")
    class GetPhoneNumbersByFirestationTests {
        @Test
        @DisplayName("GIVEN children living at an address, " +
                "WHEN we call the uri \"/phoneAlert?firestation=<firestation_number>\",\n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getPhoneNumbersByFirestationTest() throws Exception {
            // GIVEN
            //children living at an address
            List<String> phoneNumberList = new ArrayList<>();
            phoneNumberList.add("phone number 1");
            phoneNumberList.add("phone number 2");
            doReturn(phoneNumberList).when(urlsService).getPhoneNumbersByFirestation(1);
            // WHEN
            //we call the uri "/phoneAlert?firestation=<firestation_number>"
            mockMvc.perform(get("/phoneAlert")
                            .param("stationId", "1"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$.[0]", is("phone number 1")))
                    .andExpect(jsonPath("$.[1]", is("phone number 2")));
            verify(urlsService, Mockito.times(1)).getPhoneNumbersByFirestation(1);
        }

        @Test
        @DisplayName("GIVEN a firestation with no attached address, " +
                "WHEN we call the uri \"/phoneAlert?firestation=<firestation_number>\",\n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with empty list.")
        public void getPhoneNumbersByFirestationNoAddressTest() throws Exception {
            // GIVEN
            // a firestation with no attached address
            List<String> phoneNumberList = new ArrayList<>();
            doReturn(phoneNumberList).when(urlsService).getPhoneNumbersByFirestation(1);
            // WHEN
            //we call the uri "/phoneAlert?firestation=<firestation_number>"
            mockMvc.perform(get("/phoneAlert")
                            .param("stationId", "1"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with empty list
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
            verify(urlsService, Mockito.times(1)).getPhoneNumbersByFirestation(1);
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation, " +
                "WHEN we call the uri \"/phoneAlert?firestation=<firestation_number>\", " +
                "THEN when should have an \"isNotFound\" status and the expected error message.")
        public void getPhoneNumbersByFirestationNonExistingTest() throws Exception {
            // GIVEN
            //a non-existing firestation
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(urlsService).getPhoneNumbersByFirestation(1);
            // WHEN
            //we call the uri "/phoneAlert?firestation=<firestation_number>"
            mockMvc.perform(get("/phoneAlert")
                            .param("stationId", "1"))
                    // THEN
                    //when should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(urlsService, Mockito.times(1)).getPhoneNumbersByFirestation(1);
        }

        @Test
        @DisplayName("GIVEN a request parameter missing, " +
                "WHEN we call the uri \"/phoneAlert?firestation=<firestation_number>\", " +
                "THEN when should have an \"isBadRequest\" status and the expected error message.")
        public void getPhoneNumbersByFirestationMissingParamTest() throws Exception {
            // GIVEN
            //a request parameter missing
            // WHEN
            //we call the uri "/phoneAlert?firestation=<firestation_number>"
            mockMvc.perform(get("/phoneAlert"))
                    // THEN
                    //when should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("A request parameter is missing.\n"));
            verify(urlsService, Mockito.times(0)).getPhoneNumbersByFirestation(anyInt());
        }
    }

    @Nested
    @Tag("UrlsControllerTests")
    @DisplayName("getPersonsByAddress tests:")
    class GetPersonsByAddressTests {
        @Test
        @DisplayName("GIVEN person living at an address, " +
                "WHEN we call the uri \"/fire?address=<address>\" \n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getPersonsByAddressTest() throws Exception {
            // GIVEN
            //person living at an address
            String address = "address test";
            PersonInfoDTO personInfoDTO1 = new PersonInfoDTO("firstName1", "lastName1", "phonenumber1", 34, new ArrayList<>(), new ArrayList<>());
            PersonInfoDTO personInfoDTO2 = new PersonInfoDTO("firstName2", "lastName2", "phonenumber2", 3, new ArrayList<>(), new ArrayList<>());
            List<PersonInfoDTO> personInfoDTOList = new ArrayList<>();
            personInfoDTOList.add(personInfoDTO1);
            personInfoDTOList.add(personInfoDTO2);
            FireInfoDTO fireInfoDTO = new FireInfoDTO(address, 1, personInfoDTOList);
            doReturn(fireInfoDTO).when(urlsService).getPersonsByAddress(address);
            // WHEN
            //we call the uri "/fire?address=<address>"
            mockMvc.perform(get("/fire")
                            .param("address", address))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.address", is(address)))
                    .andExpect(jsonPath("$.station", is(1)))
                    .andExpect(jsonPath("$.personList", hasSize(2)))
                    .andExpect(jsonPath("$.personList.[0].firstName", is("firstName1")))
                    .andExpect(jsonPath("$.personList.[1].lastName", is("lastName2")));
            verify(urlsService, Mockito.times(1)).getPersonsByAddress("address test");
        }

        @Test
        @DisplayName("GIVEN not any person living at an address, " +
                "WHEN we call the uri \"/fire?address=<address>\" \n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with empty list of persons.")
        public void getPersonsByAddressNobodyTest() throws Exception {
            // GIVEN
            // not any person living at an address
            FireInfoDTO fireInfoDTO = new FireInfoDTO("address test", 1, new ArrayList<>());
            doReturn(fireInfoDTO).when(urlsService).getPersonsByAddress("address test");
            // WHEN
            //we call the uri "/fire?address=<address>"
            mockMvc.perform(get("/fire")
                            .param("address", "address test"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with empty list
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.address", is("address test")))
                    .andExpect(jsonPath("$.station", is(1)))
                    .andExpect(jsonPath("$.personList", hasSize(0)));
            verify(urlsService, Mockito.times(1)).getPersonsByAddress("address test");
        }

        @Test
        @DisplayName("GIVEN a non-existing address, " +
                "WHEN we call the uri \"/fire?address=<address>\" \n" +
                "THEN when should have an \"isNotFound\" status and the expected error message.")
        public void getPersonsByAddressNonExistingTest() throws Exception {
            // GIVEN
            //a non-existing address
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(urlsService).getPersonsByAddress("address test");
            // WHEN
            //we call the uri "/fire?address=<address>"
            mockMvc.perform(get("/fire")
                            .param("address", "address test"))
                    // THEN
                    //when should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(urlsService, Mockito.times(1)).getPersonsByAddress("address test");
        }

        @Test
        @DisplayName("GIVEN a request parameter missing, " +
                "WHEN we call the uri \"/fire?address=<address>\" \n" +
                "THEN when should have an \"isBadRequest\" status and the expected error message.")
        public void getPersonsByAddressMissingParamTest() throws Exception {
            // GIVEN
            //a request parameter missing
            // WHEN
            //we call the uri "/fire?address=<address>"
            mockMvc.perform(get("/fire"))
                    //when should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("A request parameter is missing.\n"));
            verify(urlsService, Mockito.times(0)).getChildrenByAddress(anyString());
        }
    }

    @Nested
    @Tag("UrlsControllerTests")
    @DisplayName("getHouseholdsByStation tests:")
    class GetHouseholdsByStationTests {
        @Test
        @DisplayName("GIVEN households covered by one firestation, " +
                "WHEN we call the uri \"/flood/stations?stations=<a list of station_numbers>\" \n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getHouseholdsByStationOnlyOneTest() throws Exception {
            // GIVEN
            //households covered by one firestation
            PersonInfoDTO personInfoDTO1 = new PersonInfoDTO("firstName1", "lastName1", "phonenumber1", 34, new ArrayList<>(), new ArrayList<>());
            PersonInfoDTO personInfoDTO2 = new PersonInfoDTO("firstName2", "lastName2", "phonenumber2", 3, new ArrayList<>(), new ArrayList<>());
            List<PersonInfoDTO> personInfoDTOList = List.of(personInfoDTO1, personInfoDTO2);
            List<PersonInfoDTO> personInfoDTOList2 = List.of(personInfoDTO1);
            FireInfoDTO fireInfoDTO = new FireInfoDTO("address test 1", 1, personInfoDTOList);
            FireInfoDTO fireInfoDTO2 = new FireInfoDTO("address test 2", 1, personInfoDTOList2);
            List<FireInfoDTO> fireInfoDTOList = List.of(fireInfoDTO, fireInfoDTO2);
            List<Integer> listStation = List.of(1);
            doReturn(fireInfoDTOList).when(urlsService).getHouseholdsByStation(listStation);
            // WHEN
            //we call the uri "/flood/stations?stations=<a list of station_numbers>"
            mockMvc.perform(get("/flood/stations")
                            .param("stations", "1"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$.[0].station", is(1)))
                    .andExpect(jsonPath("$.[1].station", is(1)))
                    .andExpect(jsonPath("$.[0].address", is("address test 1")))
                    .andExpect(jsonPath("$.[1].address", is("address test 2")))
                    .andExpect(jsonPath("$.[0].personList", hasSize(2)))
                    .andExpect(jsonPath("$.[1].personList", hasSize(1)));
            verify(urlsService, Mockito.times(1)).getHouseholdsByStation(List.of(1));
        }

        @Test
        @DisplayName("GIVEN households covered by several firestations, " +
                "WHEN we call the uri \"/flood/stations?stations=<a list of station_numbers>\" \n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getHouseholdsByStationSeveralTest() throws Exception {
            // GIVEN
            //households covered by several firestations
            PersonInfoDTO personInfoDTO1 = new PersonInfoDTO("firstName1", "lastName1", "phonenumber1", 34, new ArrayList<>(), new ArrayList<>());
            PersonInfoDTO personInfoDTO2 = new PersonInfoDTO("firstName2", "lastName2", "phonenumber2", 3, new ArrayList<>(), new ArrayList<>());
            List<PersonInfoDTO> personInfoDTOList = List.of(personInfoDTO1, personInfoDTO2);
            List<PersonInfoDTO> personInfoDTOList2 = List.of(personInfoDTO1);
            FireInfoDTO fireInfoDTO = new FireInfoDTO("address test 1", 1, personInfoDTOList);
            FireInfoDTO fireInfoDTO2 = new FireInfoDTO("address test 2", 1, personInfoDTOList2);
            FireInfoDTO fireInfoDTO3 = new FireInfoDTO("address test 3", 2, personInfoDTOList);
            List<FireInfoDTO> fireInfoDTOList = List.of(fireInfoDTO, fireInfoDTO2, fireInfoDTO3);
            List<Integer> listStation = List.of(1, 2);
            doReturn(fireInfoDTOList).when(urlsService).getHouseholdsByStation(listStation);
            // WHEN
            //we call the uri "/flood/stations?stations=<a list of station_numbers>"
            mockMvc.perform(get("/flood/stations")
                            .param("stations", "1", "2"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$.[0].station", is(1)))
                    .andExpect(jsonPath("$.[1].station", is(1)))
                    .andExpect(jsonPath("$.[2].station", is(2)));
            verify(urlsService, Mockito.times(1)).getHouseholdsByStation(List.of(1, 2));
        }


        @Test
        @DisplayName("GIVEN not any household covered by firestation, " +
                "WHEN we call the uri \"/flood/stations?stations=<a list of station_numbers>\" \n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with empty list.")
        public void getHouseholdsByStationNobodyTest() throws Exception {
            // GIVEN
            // not any person living at an address
            List<FireInfoDTO> fireInfoDTOList = new ArrayList<>();
            doReturn(fireInfoDTOList).when(urlsService).getHouseholdsByStation(List.of(1));
            // WHEN
            //we call the uri "/flood/stations?stations=<a list of station_numbers>"
            mockMvc.perform(get("/flood/stations")
                            .param("stations", "1"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with empty list
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
            verify(urlsService, Mockito.times(1)).getHouseholdsByStation(List.of(1));
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation, " +
                "WHEN we call the uri \"/flood/stations?stations=<a list of station_numbers>\" \n" +
                "THEN when should have an \"isNotFound\" status and the expected error message.")
        public void getHouseholdsByStationNonExistingTest() throws Exception {
            // GIVEN
            //a non-existing address
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(urlsService).getHouseholdsByStation(List.of(1));
            // WHEN
            //we call the uri "/flood/stations?stations=<a list of station_numbers>"
            mockMvc.perform(get("/flood/stations")
                            .param("stations", "1"))
                    // THEN
                    //when should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(urlsService, Mockito.times(1)).getHouseholdsByStation(List.of(1));
        }

        @Test
        @DisplayName("GIVEN a request parameter missing, " +
                "WHEN we call the uri \"/flood/stations?stations=<a list of station_numbers>\" \n" +
                "THEN when should have an \"isBadRequest\" status and the expected error message.")
        public void getHouseholdsByStationMissingParamTest() throws Exception {
            // GIVEN
            //a request parameter missing
            // WHEN
            //we call the uri "/flood/stations?stations=<a list of station_numbers>"
            mockMvc.perform(get("/flood/stations"))
                    //when should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("A request parameter is missing.\n"));
            verify(urlsService, Mockito.times(0)).getHouseholdsByStation(anyList());
        }
    }

    @Nested
    @Tag("UrlsControllerTests")
    @DisplayName("getPersonsByName tests:")
    class GetPersonsByNameTests {
        @Test
        @DisplayName("GIVEN one existing person with the researched name, " +
                "WHEN we call the uri \"/personInfo?firstName=<firstName>&lastName=<lastName>\" \n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getPersonsByNameTest() throws Exception {
            // GIVEN
            //one existing person with the researched name
            PersonInfo2DTO person = new PersonInfo2DTO("firstNameTest", "lastNameTest", "address test", "mailTest", "phonenumber1", new ArrayList<>(), new ArrayList<>());

            List<PersonInfo2DTO> personList = new ArrayList<>();
            personList.add(person);
            doReturn(personList).when(urlsService).getPersonsByName("firstNameTest", "lastNameTest");
            // WHEN
            //we call the uri "/personInfo?firstName=<firstName>&lastName=<lastName>"
            mockMvc.perform(get("/personInfo")
                            .param("firstName", "firstNameTest")
                            .param("lastName", "lastNameTest"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$.[0].firstName", is("firstNameTest")))
                    .andExpect(jsonPath("$.[0].lastName", is("lastNameTest")));
            verify(urlsService, Mockito.times(1)).getPersonsByName("firstNameTest", "lastNameTest");
        }

        @Test
        @DisplayName("GIVEN two existing persons with the researched name, " +
                "WHEN we call the uri \"/personInfo?firstName=<firstName>&lastName=<lastName>\" \n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getPersonsByNameTwoPersonsTest() throws Exception {
            // GIVEN
            //two existing persons with the researched name
            PersonInfo2DTO person = new PersonInfo2DTO("firstNameTest", "lastNameTest", "address test", "mailTest", "phonenumber1", new ArrayList<>(), new ArrayList<>());
            PersonInfo2DTO person2 = new PersonInfo2DTO("firstNameTest", "lastNameTest", "address test2", "mailTest2", "phonenumber2", new ArrayList<>(), new ArrayList<>());
            List<PersonInfo2DTO> personList = new ArrayList<>();
            personList.add(person);
            personList.add(person2);
            doReturn(personList).when(urlsService).getPersonsByName("firstNameTest", "lastNameTest");
            // WHEN
            //we call the uri "/personInfo?firstName=<firstName>&lastName=<lastName>"
            mockMvc.perform(get("/personInfo")
                            .param("firstName", "firstNameTest")
                            .param("lastName", "lastNameTest"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$.[0].address", is("address test")))
                    .andExpect(jsonPath("$.[1].address", is("address test2")));
            verify(urlsService, Mockito.times(1)).getPersonsByName("firstNameTest", "lastNameTest");
        }

        @Test
        @DisplayName("GIVEN not existing person with the researched name, " +
                "WHEN we call the uri \"/fire?address=<address>\" \n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with empty list of persons.")
        public void getPersonsByAddressNobodyTest() throws Exception {
            // GIVEN
            // not any person living at an address
            List<PersonInfo2DTO> personList = new ArrayList<>();
            doReturn(personList).when(urlsService).getPersonsByName("firstNameTest", "lastNameTest");
            // WHEN
            //"WHEN we call the uri "/personInfo?firstName=<firstName>&lastName=<lastName>"
            mockMvc.perform(get("/personInfo")
                            .param("firstName", "firstNameTest")
                            .param("lastName", "lastNameTest"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with empty list
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
            verify(urlsService, Mockito.times(1)).getPersonsByName("firstNameTest", "lastNameTest");
        }

        @Test
        @DisplayName("GIVEN a request parameter missing, " +
                "WHEN we call the uri \"/personInfo?firstName=<firstName>&lastName=<lastName>\" \n" +
                "THEN when should have an \"isBadRequest\" status and the expected error message.")
        public void getPersonsByAddressMissingParamTest() throws Exception {
            // GIVEN
            //a request parameter missing
            // WHEN
            //"WHEN we call the uri "/personInfo?firstName=<firstName>&lastName=<lastName>"
            mockMvc.perform(get("/personInfo")
                            .param("lastName", "lastNameTest"))
                    //when should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("A request parameter is missing.\n"));
            verify(urlsService, Mockito.times(0)).getPersonsByName(anyString(), anyString());
        }
    }

    @Nested
    @Tag("UrlsControllerTests")
    @DisplayName("getMailsByCity tests:")
    class GetMailsByCityTests {
        @Test
        @DisplayName("GIVEN non empty list of mail, " +
                "WHEN we call the uri \"/communityEmail?city=<city>\",\n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information.")
        public void getMailsByCityTest() throws Exception {
            // GIVEN
            //children living at an address
            List<String> mailList = new ArrayList<>();
            mailList.add("mail 1");
            mailList.add("mail 2");
            doReturn(mailList).when(urlsService).getMailsByCity("cityTest");
            // WHEN
            //we call the uri "/communityEmail?city=<city>"
            mockMvc.perform(get("/communityEmail")
                            .param("city", "cityTest"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$.[0]", is("mail 1")))
                    .andExpect(jsonPath("$.[1]", is("mail 2")));
            verify(urlsService, Mockito.times(1)).getMailsByCity("cityTest");
        }

        @Test
        @DisplayName("GIVEN an empty list of mail, " +
                "WHEN we call the uri \"/communityEmail?city=<city>\",\n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with an empty list.")
        public void getMailsByCityEmptyTest() throws Exception {
            // GIVEN
            //children living at an address
            List<String> mailList = new ArrayList<>();
            doReturn(mailList).when(urlsService).getMailsByCity("cityTest");
            // WHEN
            //we call the uri "/communityEmail?city=<city>"
            mockMvc.perform(get("/communityEmail")
                            .param("city", "cityTest"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with an empty list
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
            verify(urlsService, Mockito.times(1)).getMailsByCity("cityTest");
        }

        @Test
        @DisplayName("GIVEN non-existing city, " +
                "WHEN we call the uri \"/communityEmail?city=<city>\",\n" +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with an empty list.")
        public void getMailsByCityNoExistingTest() throws Exception {
            // GIVEN
            //non-existing city
            List<String> mailList = new ArrayList<>();
            mailList.add("mail 1");
            mailList.add("mail 2");
            doReturn(mailList).when(urlsService).getMailsByCity("cityTest");
            // WHEN
            //we call the uri "/communityEmail?city=<city>"
            mockMvc.perform(get("/communityEmail")
                            .param("city", "cityTest2"))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with an empty list
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
            verify(urlsService, Mockito.times(1)).getMailsByCity("cityTest2");
        }

        @Test
        @DisplayName("GIVEN a request parameter missing, " +
                "WHEN we call the uri \"/communityEmail?city=<city>\", " +
                "THEN when should have an \"isBadRequest\" status and the expected error message.")
        public void getPhoneNumbersByFirestationMissingParamTest() throws Exception {
            // GIVEN
            //a request parameter missing
            // WHEN
            //we call the uri "/communityEmail?city=<city>"
            mockMvc.perform(get("/communityEmail"))
                    // THEN
                    //when should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("A request parameter is missing.\n"));
            verify(urlsService, Mockito.times(0)).getMailsByCity(anyString());
        }
    }

}
