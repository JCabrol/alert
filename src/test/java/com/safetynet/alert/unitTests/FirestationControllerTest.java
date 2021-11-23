package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.AttachedAddress;
import com.safetynet.alert.model.Firestation;
import com.safetynet.alert.service.FirestationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("FirestationTests")
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
public class FirestationControllerTest {


    @MockBean
    private FirestationService firestationService;

    @Autowired
    private MockMvc mockMvc;


    @Nested
    @Tag("FirestationControllerTests")
    @DisplayName("GET requests:")
    class FirestationGetTests {

        @Test
        @DisplayName("GIVEN a non empty list of firestations " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isOk\" status and the response's body should contain a JSon file with the firestations and their informations.")
        void getAllFirestationsTest() throws Exception {
            // GIVEN
            Firestation firestation1 = new Firestation();
            firestation1.setStationId(1);
            firestation1.addAttachedAddress(new AttachedAddress("address test"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation1);
            doReturn(firestations).when(firestationService).getFirestations();
            doReturn("The test is ok!").when(firestationService).firestationsToString(firestations);
            //WHEN
            mockMvc.perform(get("/firestation"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The test is ok!"));
            verify(firestationService, Mockito.times(1)).getFirestations();
            verify(firestationService, Mockito.times(1)).firestationsToString(firestations);
        }

        @Test
        @DisplayName("GIVEN a non empty list of firestations " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isOk\" status and the response's body should contain a JSon file with the firestations and their informations.")
        void getAllFirestationsEmptyTest() throws Exception {
            // GIVEN
            doThrow(EmptyFirestationsException.class).when(firestationService).getFirestations();

            //WHEN
            mockMvc.perform(get("/firestation"))
                    // THEN
                    .andExpect(status().isNotFound());
            verify(firestationService, Mockito.times(1)).getFirestations();
            verify(firestationService, Mockito.times(0)).firestationsToString(any());
        }


        @Test
        @DisplayName("GIVEN an existing firestation, " +
                "WHEN we call the uri \"/firestation/{id}\", " +
                "THEN when should have an \"isOk\" status and the response's body is a String with all correct information about the firestation found.")
        public void getFirestationByIdExistingTest() throws Exception {
            // GIVEN
            Firestation firestation1 = new Firestation();
            firestation1.setStationId(1);
            firestation1.addAttachedAddress(new AttachedAddress("address test"));
            doReturn(firestation1).when(firestationService).getFirestationById(1);
            // WHEN
            mockMvc.perform(get("/firestation/{idOrAddress}", 1))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("Firestation n°1 :\n- address test\n\n"));
            verify(firestationService, Mockito.times(1)).getFirestationById(1);
            verify(firestationService, Mockito.times(0)).getFirestationByAddress(any());
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation, " +
                "WHEN we call the uri \"/firestation/{id}\", " +
                "THEN when should have an \"isNotFound\" status and the response's body is an empty String.")
        public void getFirestationByIdNonExistingTest() throws Exception {
            // GIVEN
            doThrow(FirestationNotFoundException.class).when(firestationService).getFirestationById(1);
            // WHEN
            mockMvc.perform(get("/firestation/{idOrAddress}", 1))
                    // THEN
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(1)).getFirestationById(1);
            verify(firestationService, Mockito.times(0)).getFirestationByAddress(any());
        }

        @Test
        @DisplayName("GIVEN an existing address, " +
                "WHEN we call the uri \"/firestation/{address}\", " +
                "THEN when should have an \"isOk\" status and the response's body is a String with all correct information about the firestation in which the address is.")
        public void getFirestationByAddressExistingTest() throws Exception {
            // GIVEN
            Firestation firestation1 = new Firestation();
            firestation1.setStationId(1);
            firestation1.addAttachedAddress(new AttachedAddress("address test"));
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation1);
            doReturn(firestations).when(firestationService).getFirestationByAddress("address test");
            doReturn("The test is ok!").when(firestationService).firestationsToString(firestations);
            // WHEN
            mockMvc.perform(get("/firestation/{idOrAddress}", "address test"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The test is ok!"));
            verify(firestationService, Mockito.times(1)).getFirestationByAddress("address test");
            verify(firestationService, Mockito.times(1)).firestationsToString(firestations);
            verify(firestationService, Mockito.times(0)).getFirestationById(anyInt());
        }

        @Test
        @DisplayName("GIVEN a non-existing firestation, " +
                "WHEN we call the uri \"/firestation/{id}\", " +
                "THEN when should have an \"isOk\" status and the firestation with all correct attributes in the response.")
        public void getFirestationByAddressNonExistingTest() throws Exception {
            // GIVEN
            doThrow(FirestationNotFoundException.class).when(firestationService).getFirestationByAddress("address test");
            // WHEN
            mockMvc.perform(get("/firestation/{idOrAddress}", "address test"))
                    // THEN
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(1)).getFirestationByAddress("address test");
            verify(firestationService, Mockito.times(0)).firestationsToString(any());
            verify(firestationService, Mockito.times(0)).getFirestationById(anyInt());
        }
    }

    @Nested
    @Tag("FirestationControllerTests")
    @DisplayName("POST requests:")
    class PostTests {

        @Test
        @DisplayName("GIVEN a mapping with a new firestation and a new address in the request's body, " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation created.")
        public void addNewMappingTest() throws Exception {
            // GIVEN
            doReturn("The test is OK!").when(firestationService).addNewMapping(1, "address test");
            // WHEN
            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isCreated())
                    .andExpect(content().string("The test is OK!"))
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/firestation/1"));
            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
        public void addNewMappingNotRightFormatToPostExceptionTest() throws Exception {
            // GIVEN
            doThrow(NotRightFormatToPostException.class).when(firestationService).addNewMapping(1, "address test");
            // WHEN
            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
        public void addNewMappingAlreadyExistingTest() throws Exception {
            // GIVEN
            doThrow(MappingAlreadyExistingException.class).when(firestationService).addNewMapping(1, "address test");
            // WHEN
            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN request without body" +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void addNewMappingMessageNotReadableExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct: please verify the request contains a body.\n"));
            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN request with body not in Json" +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void addNewMappingHttpMediaTypeNotSupportedExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.TEXT_PLAIN))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : the request's body should be in json.\n"));
            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN request with not correct url" +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void addNewMappingHttpRequestMethodNotSupportedExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(post("/firestation/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
        }

    }

    @Nested
    @Tag("FirestationControllerTests")
    @DisplayName("PUT requests:")
    class PutTests {

        @Test
        @DisplayName("GIVEN a mapping with a firestation and an address in the request's body, " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
        public void updateAddressTest() throws Exception {
            // GIVEN
            doReturn("").when(firestationService).deleteAddress("address test");
            doReturn("").when(firestationService).addNewMapping(1, "address test");
            // WHEN
            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The address address test have been updated to the firestation number 1."))
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/firestation/1"));
            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
        public void updateAddressNotRightFormatToPostExceptionTest() throws Exception {
            // GIVEN
            doReturn("").when(firestationService).deleteAddress("address test");
            doThrow(NotRightFormatToPostException.class).when(firestationService).addNewMapping(1, "address test");
            // WHEN
            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
        public void updateAddressMappingAlreadyExistingTest() throws Exception {
            // GIVEN
            doReturn("").when(firestationService).deleteAddress("address test");
            doThrow(MappingAlreadyExistingException.class).when(firestationService).addNewMapping(1, "address test");
            // WHEN
            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
        public void updateAddressNothingToDeleteTest() throws Exception {
            // GIVEN
            doThrow(NothingToDeleteException.class).when(firestationService).deleteAddress("address test");
            doReturn("").when(firestationService).addNewMapping(1, "address test");
            // WHEN
            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The address address test have been updated to the firestation number 1."))
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/firestation/1"));
            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN request without body" +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void updateAddressMessageNotReadableExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct: please verify the request contains a body.\n"));
            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN request with body not in Json" +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void updateAddressHttpMediaTypeNotSupportedExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.TEXT_PLAIN))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : the request's body should be in json.\n"));
            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
        }

        @Test
        @DisplayName("GIVEN request with not correct url" +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void updateAddressHttpRequestMethodNotSupportedExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(put("/firestation/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
        }
    }

    @Nested
    @Tag("FirestationControllerTests")
    @DisplayName("DELETE requests:")
    class DeleteTests {

        @Test
        @DisplayName("GIVEN an existing address to delete " +
                "WHEN we call the uri \"/firestation/{address}\", " +
                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
        public void deleteAddressTest() throws Exception {
            // GIVEN
            doReturn("The test is Ok!").when(firestationService).deleteAddress("address test");
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", "address test"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The test is Ok!"));
            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
            verify(firestationService, Mockito.times(0)).deleteFirestation(anyInt());
        }

        @Test
        @DisplayName("GIVEN an existing address to delete " +
                "WHEN we call the uri \"/firestation/{address}\", " +
                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
        public void deleteAddressNonExistingTest() throws Exception {
            // GIVEN
            doThrow(NothingToDeleteException.class).when(firestationService).deleteAddress("address test");
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", "address test"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
            verify(firestationService, Mockito.times(0)).deleteFirestation(anyInt());
        }
        @Test
        @DisplayName("GIVEN an existing address to delete " +
                "WHEN we call the uri \"/firestation/{address}\", " +
                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
        public void deleteFirestationTest() throws Exception {
            // GIVEN
            doNothing().when(firestationService).deleteFirestation(1);
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The firestation number 1 have been deleted."));
            verify(firestationService, Mockito.times(0)).deleteAddress(any());
            verify(firestationService, Mockito.times(1)).deleteFirestation(1);
        }

        @Test
        @DisplayName("GIVEN an existing address to delete " +
                "WHEN we call the uri \"/firestation/{address}\", " +
                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
        public void deleteFirestationNonExistingTest() throws Exception {
            // GIVEN
            doThrow(NothingToDeleteException.class).when(firestationService).deleteFirestation(1);
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(0)).deleteAddress(any());
            verify(firestationService, Mockito.times(1)).deleteFirestation(1);
        }

        @Test
        @DisplayName("GIVEN an existing address to delete " +
                "WHEN we call the uri \"/firestation/{address}\", " +
                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
        public void deleteFirestationNonEmptyTest() throws Exception {
            // GIVEN
            doThrow(FirestationNonEmptyException.class).when(firestationService).deleteFirestation(1);
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
            verify(firestationService, Mockito.times(0)).deleteAddress(any());
            verify(firestationService, Mockito.times(1)).deleteFirestation(1);
        }

        @Test
        @DisplayName("GIVEN request with not correct url" +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have a \"badRequest\" status and the correct error message.")
        public void deleteFirestationOrAddressHttpRequestMethodNotSupportedExceptionTest() throws Exception {
            // GIVEN

            // WHEN
            mockMvc.perform(delete("/firestation"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
            verify(firestationService, Mockito.times(0)).deleteAddress(any());
            verify(firestationService, Mockito.times(0)).deleteFirestation(anyInt());
        }

    }

}