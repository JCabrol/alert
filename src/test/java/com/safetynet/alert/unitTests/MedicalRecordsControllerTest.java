package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.DTO.MedicalRecordDTO;
import com.safetynet.alert.model.MedicalRecords;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.MedicalRecordsService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("MedicalRecordsTests")
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
public class MedicalRecordsControllerTest {


    @MockBean
    private MedicalRecordsService medicalRecordsService;

    @Autowired
    private MockMvc mockMvc;


    @Nested
    @Tag("MedicalRecordsControllerTests")
    @DisplayName("GET requests:")
    class medicalRecordsGetTests {

        @Test
        @DisplayName("GIVEN a non empty list of medical records " +
                "WHEN we call the uri \"/medicalRecord\", " +
                "THEN we should have an \"isOk\" status and the response's body should contain a String with all the medical records.")
        void getAllMedicalRecordsTest() throws Exception {
            // GIVEN
            MedicalRecords medicalRecords1 = new MedicalRecords();
            MedicalRecords medicalRecords2 = new MedicalRecords();
            List<MedicalRecords> medicalRecordsList = new ArrayList<>();
            medicalRecordsList.add(medicalRecords1);
            medicalRecordsList.add(medicalRecords2);
            doReturn(medicalRecordsList).when(medicalRecordsService).getMedicalRecords();
            //WHEN
            mockMvc.perform(get("/medicalRecord"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)));
            verify(medicalRecordsService, Mockito.times(1)).getMedicalRecords();
        }

        @Test
        @DisplayName("GIVEN an empty list of medical records " +
                "WHEN we call the uri \"/medicalRecord\", " +
                "THEN we should have an \"isNotFound\" status and the response's body should contain a String indicating that there are no medical records.")
        void getAllMedicalRecordsEmptyTest() throws Exception {
            // GIVEN
            EmptyObjectException emptyObjectException = new EmptyObjectException("error message");
            doThrow(emptyObjectException).when(medicalRecordsService).getMedicalRecords();
            //WHEN
            mockMvc.perform(get("/medicalRecord"))
                    // THEN
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(medicalRecordsService, Mockito.times(1)).getMedicalRecords();
        }

        @Test
        @DisplayName("GIVEN an existing person with medical records, " +
                "WHEN we call the uri \"/medicalRecords/{firstName}/{lastName}\", " +
                "THEN when should have an \"isOk\" status and the response's body is a JSON with all correct information about the medical records found.")
        public void getMedicalRecordsExistingTest() throws Exception {
            // GIVEN
            //an existing person with medical records
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            MedicalRecords medicalRecords = new MedicalRecords();
            List<String> medications = new ArrayList<>();
            List<String> allergies = new ArrayList<>();
            medications.add("medicationTest");
            MedicalRecordDTO medicalRecordsDTO = new MedicalRecordDTO("firstNameTest", "lastNameTest", "14-11-1982", medications, allergies);
            doReturn(medicalRecords).when(medicalRecordsService).getMedicalRecordsByName(firstName, lastName);
            doReturn(medicalRecordsDTO).when(medicalRecordsService).transformMedicalRecordsToMedicalRecordDTO(medicalRecords);
            // WHEN
            //we call the uri "/medicalRecords/{firstName}/{lastName}"
            mockMvc.perform(get("/medicalRecord/{firstName}/{lastName}", firstName, lastName))
                    // THEN
                    //when should have an "isOk" status and the response's body is a JSON with all correct information about the medical records found
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.firstName", is("firstNameTest")))
                    .andExpect(jsonPath("$.lastName", is("lastNameTest")))
                    .andExpect(jsonPath("$.birthdate", is("14-11-1982")))
                    .andExpect(jsonPath("$.allergies", hasSize(0)))
                    .andExpect(jsonPath("$.medications", hasSize(1)))
                    .andExpect(jsonPath("$.medications.[0]", is("medicationTest")));
            verify(medicalRecordsService, Mockito.times(1)).getMedicalRecordsByName(firstName, lastName);
            verify(medicalRecordsService, Mockito.times(1)).transformMedicalRecordsToMedicalRecordDTO(medicalRecords);
        }

        @Test
        @DisplayName("GIVEN non-existing medical records, " +
                "WHEN we call the uri \"/medicalRecords/{firstName}/{lastName}\", " +
                "THEN when should have an \"isNotFound\" status and the expected error message.")
        public void getMedicalRecordsNonExistingTest() throws Exception {
            // GIVEN
            //non-existing medical records
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(medicalRecordsService).getMedicalRecordsByName(any(), any());
            // WHEN
            //we call the uri "/medicalRecords/{firstName}/{lastName}"
            mockMvc.perform(get("/medicalRecord/{firstName}/{lastName}", firstName, lastName))
                    // THEN
                    //when should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(medicalRecordsService, Mockito.times(1)).getMedicalRecordsByName(firstName, lastName);
            verify(medicalRecordsService, Mockito.times(0)).transformMedicalRecordsToMedicalRecordDTO(any());
        }
    }

    @Nested
    @Tag("MedicalRecordsControllerTests")
    @DisplayName("POST requests:")
    class PostTests {

        @Test
        @DisplayName("GIVEN medical records returned by the MedicalRecordsService, " +
                "WHEN we call the uri \"/medicalRecord\", " +
                "THEN we should have an \"isCreated\" status, the right confirmation message and the header should return the right url to find the medicalRecord created.")
        public void addMedicalRecordTest() throws Exception {
            // GIVEN
            MedicalRecords medicalRecords = new MedicalRecords();
            Person person = new Person("firstNameTest", "lastNameTest");
            medicalRecords.addPerson(person);
            doReturn(medicalRecords).when(medicalRecordsService).addNewMedicalRecords(any(MedicalRecordDTO.class));
            // WHEN
            mockMvc.perform(post("/medicalRecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firstName\":\"firstNameTest\",\"lastName\":\"lastNameTest\",\"birthdate\":\"birthdateTest\"}"))
                    // THEN
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Medical records about FIRSTNAMETEST LASTNAMETEST have been registered.\nhttp://localhost/medicalRecord/FIRSTNAMETEST/LASTNAMETEST"))
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/medicalRecord/FIRSTNAMETEST/LASTNAMETEST"));
            verify(medicalRecordsService, Mockito.times(1)).addNewMedicalRecords(any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN a NotRightFormatToPostException returned by the MedicalRecordsService " +
                "WHEN we call the uri \"/medicalRecord\", " +
                "THEN we should have an \"isBadRequest\" status, with the expected error message.")
        public void addMedicalRecordWithoutRequiredInformationTest() throws Exception {
            // GIVEN
            doThrow(NotRightFormatToPostException.class).when(medicalRecordsService).addNewMedicalRecords(any(MedicalRecordDTO.class));
            // WHEN
            mockMvc.perform(post("/medicalRecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"lastName\":\"lastNameTest\",\"birthdate\":\"birthdateTest\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
            verify(medicalRecordsService, Mockito.times(1)).addNewMedicalRecords(any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN request without body" +
                "WHEN we call the uri \"/medicalRecord\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void addNewMappingMessageNotReadableExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(post("/medicalRecord")
                            .contentType(MediaType.APPLICATION_JSON))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct: please verify the request's body.\n"));
            verify(medicalRecordsService, Mockito.times(0)).addNewMedicalRecords(any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN request with body not in Json" +
                "WHEN we call the uri \"/medicalRecord\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void addNewMappingHttpMediaTypeNotSupportedExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(post("/medicalRecord")
                            .contentType(MediaType.TEXT_PLAIN))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : the request's body should be in json.\n"));
            verify(medicalRecordsService, Mockito.times(0)).addNewMedicalRecords(any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN request with not correct url" +
                "WHEN we call the uri \"/medicalRecord\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void addNewMappingHttpRequestMethodNotSupportedExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(post("/medicalRecord/firstNameTest/lastNameTest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firstName\":\"firstNameTest\",\"lastName\":\"lastNameTest\",\"birthdate\":\"birthdateTest\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
            verify(medicalRecordsService, Mockito.times(0)).addNewMedicalRecords(any(MedicalRecordDTO.class));
        }
    }

    @Nested
    @Tag("MedicalRecordsControllerTests")
    @DisplayName("PUT requests:")
    class PutTests {
        @Test
        @DisplayName("GIVEN medical records with all updatable information in the request's body, " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN we should have an \"isOK\" status and the expected information message.")
        public void updateMedicalRecordsWithAllInformationTest() throws Exception {
            //GIVEN
            //medical records with all updatable information in the request's body
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String itemsToUpdate = "{\"firstName\":\"firstNameTest\",\"lastName\":\"lastNameTest\",\"birthdate\":\"14-11-1982\",\"medications\":[\"medicationTest1\",\"medicationTest2\"],\"allergies\":[]}";
            doReturn("information message").when(medicalRecordsService).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
            //WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", firstName, lastName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have an "isOK" status and the expected information message
                    .andExpect(status().isOk())
                    .andExpect(content().string("information message"));
            verify(medicalRecordsService, Mockito.times(1)).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN medical records with only some updatable information in the request's body, " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN we should have an \"isOK\" status and the expected information message.")
        public void updateMedicalRecordsWithSomeInformationTest() throws Exception {
            //GIVEN
            //medical records with only some updatable information in the request's body
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String itemsToUpdate = "{\"medications\":[\"medicationTest1\",\"medicationTest2\"]}";
            doReturn("information message").when(medicalRecordsService).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
            //WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", firstName, lastName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have an "isOK" status and the expected information message
                    .andExpect(status().isOk())
                    .andExpect(content().string("information message"));
            verify(medicalRecordsService, Mockito.times(1)).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN medical records with not found person in the url, " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN we should have an \"isNotFound\" status and the expected error message.")
        public void updateMedicalRecordObjectNotFoundExceptionTest() throws Exception {
            //GIVEN
            //medical records with not found person in the url
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String itemsToUpdate = "{\"medications\":[\"medicationTest1\",\"medicationTest2\"]}";
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(medicalRecordsService).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
            //WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", firstName, lastName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(medicalRecordsService, Mockito.times(1)).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN medical records with not same person name in the url and in the body, " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN we should have an \"isConflict\" status and the expected error message.")
        public void updateMedicalRecordNotTheSamePersonExceptionTest() throws Exception {
            //GIVEN
            //medical records with not same person name in the url and in the body
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String itemsToUpdate = "{\"firstName\":\"firstNameTest\",\"lastName\":\"lastNameTest\",\"birthdate\":\"14-11-1982\"}";
            NotTheSamePersonException notTheSamePersonException = new NotTheSamePersonException("error message");
            doThrow(notTheSamePersonException).when(medicalRecordsService).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
            //WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", firstName, lastName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    // we should have an "isConflict" status and the expected error message
                    .andExpect(status().isConflict())
                    .andExpect(content().string("error message"));
            verify(medicalRecordsService, Mockito.times(1)).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN medical records with NothingToUpdateException, " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN we should have an \"isBadRequest\" status and the expected error message.")
        public void updateMedicalNothingToUpdateExceptionTest() throws Exception {
            //GIVEN
            //medical records with NothingToUpdateException
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            String itemsToUpdate = "{\"firstName\":\"firstNameTest\",\"lastName\":\"lastNameTest\",\"birthdate\":\"14-11-1982\"}";
            NothingToUpdateException nothingToUpdateException = new NothingToUpdateException("error message");
            doThrow(nothingToUpdateException).when(medicalRecordsService).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
            //WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", firstName, lastName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    // we should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(medicalRecordsService, Mockito.times(1)).updateMedicalRecord(eq(firstName), eq(lastName), any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN no first name and last name in the url , " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN we should have an \"isBadRequest\" status and the expected error message.")
        public void updateMedicalRecordsNoNameUrlTest() throws Exception {
            //GIVEN
            //no first name and last name in the url
            String itemsToUpdate = "{\"firstName\":\"firstNameTest\",\"lastName\":\"lastNameTest\",\"birthdate\":\"14-11-1982\"}";
            //WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(put("/medicalRecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    // we should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
            verify(medicalRecordsService, Mockito.times(0)).updateMedicalRecord(any(), any(), any(MedicalRecordDTO.class));
        }

        @Test
        @DisplayName("GIVEN nothing in the body's request , " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN we should have an \"isBadRequest\" status and the expected error message.")
        public void updateMedicalRecordsNoBodyTest() throws Exception {
            //GIVEN
            //nothing in the body's request
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            //WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(put("/medicalRecord/{firstName}/{lastName}", firstName, lastName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    //THEN
                    // we should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct: please verify the request's body.\n"));
            verify(medicalRecordsService, Mockito.times(0)).updateMedicalRecord(any(), any(), any(MedicalRecordDTO.class));
        }
    }

    @Nested
    @Tag("DeletePersonTests")
    @DisplayName("DELETE requests:")
    class DeleteTests {

        @Test
        @DisplayName("GIVEN existing person and medical records, " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN when should have an \"isOk\" status and the expected information message.")
        public void deleteMedicalRecordsExistingTest() throws Exception {
            // GIVEN
            //existing person and medical records
            String firstName = "firstNameTest";
            String lastName = "lastNameTest";
            doReturn("information message").when(medicalRecordsService).deleteMedicalRecords(firstName, lastName);
            // WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(delete("/medicalRecord/{firstName}/{lastName}", firstName, lastName))
                    // THEN
                    //when should have an "isOk" status and the expected information message
                    .andExpect(status().isOk())
                    .andExpect(content().string("information message"));
            verify(medicalRecordsService, Mockito.times(1)).deleteMedicalRecords(firstName, lastName);
        }

        @Test
        @DisplayName("GIVEN a non-existing person, " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN when should have an \"isNotFound\" status and the expected error message.")
        public void deleteMedicalRecordsNonExistingTest() throws Exception {
            // GIVEN
            //a non-existing person
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(medicalRecordsService).deleteMedicalRecords("firstName", "lastName");
            // WHEN
            // we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(delete("/medicalRecord/{firstName}/{lastName}", "firstName", "lastName"))
                    // THEN
                    // when should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(medicalRecordsService, Mockito.times(1)).deleteMedicalRecords("firstName", "lastName");
        }

        @Test
        @DisplayName("GIVEN a NothingToDeleteException returned by medicalRecordsService, " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN when should have an \"isBadRequest\" status and the expected error message.")
        public void deleteMedicalRecordsNothingToDeleteTest() throws Exception {
            // GIVEN
            //a NothingToDeleteException returned by medicalRecordsService
            NothingToDeleteException nothingToDeleteException = new NothingToDeleteException("error message");
            doThrow(nothingToDeleteException).when(medicalRecordsService).deleteMedicalRecords("firstName", "lastName");
            // WHEN
            // we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(delete("/medicalRecord/{firstName}/{lastName}", "firstName", "lastName"))
                    // THEN
                    // when should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(medicalRecordsService, Mockito.times(1)).deleteMedicalRecords("firstName", "lastName");
        }

        @Test
        @DisplayName("GIVEN no first name and last name in the url , " +
                "WHEN we call the uri \"/medicalRecord/{firstName}/{lastName}\", " +
                "THEN we should have an \"isBadRequest\" status and the expected error message.")
        public void updateMedicalRecordsNoUrlTest() throws Exception {
            //GIVEN
            //no first name and last name in the url

            //WHEN
            //we call the uri "/medicalRecord/{firstName}/{lastName}"
            mockMvc.perform(delete("/medicalRecord"))
                    //THEN
                    // we should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
            verify(medicalRecordsService, Mockito.times(0)).deleteMedicalRecords(any(), any());
        }
    }
}