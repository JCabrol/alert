package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.DTO.FirestationDTO;
import com.safetynet.alert.model.DTO.MappingFirestationAddressDTO;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
        @DisplayName("GIVEN a non-empty list of firestations " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isOk\" status with a body in json containing the expected list.")
        void getAllFirestationsTest() throws Exception {
            // GIVEN
            //a non-empty list of firestations
            Firestation firestation1 = new Firestation();
            Firestation firestation2 = new Firestation();
            List<Firestation> firestations = new ArrayList<>();
            firestations.add(firestation1);
            firestations.add(firestation2);
            doReturn(firestations).when(firestationService).getFirestations();
            //WHEN
            //we call the uri "/firestation",
            mockMvc.perform(get("/firestations"))
                    // THEN
                    //we should have an "isOk" status with a body in json containing the expected list
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)));
            verify(firestationService, Mockito.times(1)).getFirestations();

        }

        @Test
        @DisplayName("GIVEN an empty list of firestations " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have a \"notFound\" status and the response's body should contain a String with the expected error message.")
        void getAllFirestationsEmptyTest() throws Exception {
            // GIVEN
            //an empty list of firestations
            EmptyObjectException emptyObjectException = new EmptyObjectException("error message");
            doThrow(emptyObjectException).when(firestationService).getFirestations();
            //WHEN
            //we call the uri "/firestation"
            mockMvc.perform(get("/firestations"))
                    // THEN
                    //we should have a "notFound" status and the response's body should contain a String with the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(firestationService, Mockito.times(1)).getFirestations();
        }


        @Test
        @DisplayName("GIVEN an existing firestation, " +
                "WHEN we call the uri \"/firestation/{idOrAddress}\", " +
                "THEN we should have an \"isOk\" status and the firestationDTO with all correct attributes in the response.")
        public void getFirestationDTOExistingTest() throws Exception {
            // GIVEN
            //an existing firestation
            String address1 = "address1";
            String address2 = "address2";
            List<String> addressList = List.of(address1, address2);
            FirestationDTO firestation = new FirestationDTO(1, addressList);
            doReturn(firestation).when(firestationService).getFirestationDTO("1");
            // WHEN
            //we call the uri "/firestation/{idOrAddress}"
            mockMvc.perform(get("/firestation/{idOrAddress}", "1"))
                    // THEN
                    //we should have an "isOk" status and the firestationDTO with all correct attributes in the response
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.firestationNumber", is(1)))
                    .andExpect(jsonPath("$.addressesList", is(addressList)));
            verify(firestationService, Mockito.times(1)).getFirestationDTO("1");
        }

        @Test
        @DisplayName("GIVEN a firestation non existing, " +
                "WHEN we call the uri \"/firestation/{idOrAddress}\", " +
                "THEN we should have an \"isNotFound\" status with the expected error message.")
        public void getFirestationDTONonExistingTest() throws Exception {
            // GIVEN
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(firestationService).getFirestationDTO("1");
            // WHEN
            mockMvc.perform(get("/firestation/{idOrAddress}", "1"))
                    // THEN
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(firestationService, Mockito.times(1)).getFirestationDTO("1");
        }
    }


    @Nested
    @Tag("FirestationControllerTests")
    @DisplayName("POST requests:")
    class PostTests {

        @Test
        @DisplayName("GIVEN a success message returned by the firestationService " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isCreated\" status, the expected message and the header should return the right url to find the firestation created.")
        public void addNewMappingTest() throws Exception {
            // GIVEN
            doReturn("The test is OK!\n").when(firestationService).addNewMapping(any(MappingFirestationAddressDTO.class));
            // WHEN
            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"number\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isCreated())
                    .andExpect(content().string("The test is OK!\nhttp://localhost/firestation/1"))
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/firestation/1"));
            verify(firestationService, Mockito.times(1)).addNewMapping(any(MappingFirestationAddressDTO.class));
        }

        @Test
        @DisplayName("GIVEN a NotRightFormatToPostException returned by the firestationService " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isBasRequest\" status and the response's body should contain the expected error message.")
        public void addNewMappingNotRightFormatToPostExceptionTest() throws Exception {
            // GIVEN
            NotRightFormatToPostException notRightFormatToPostException = new NotRightFormatToPostException("error message");
            doThrow(notRightFormatToPostException).when(firestationService).addNewMapping(any(MappingFirestationAddressDTO.class));
            // WHEN
            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(firestationService, Mockito.times(1)).addNewMapping(any(MappingFirestationAddressDTO.class));
        }

        @Test
        @DisplayName("GIVEN a ObjectAlreadyExistingException returned by the firestationService " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isBasRequest\" status and the response's body should contain the expected error message.")
        public void addNewMappingAlreadyExistingTest() throws Exception {
            // GIVEN
            ObjectAlreadyExistingException objectAlreadyExistingException = new ObjectAlreadyExistingException("error message");
            doThrow(objectAlreadyExistingException).when(firestationService).addNewMapping(any(MappingFirestationAddressDTO.class));
            // WHEN
            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(firestationService, Mockito.times(1)).addNewMapping(any(MappingFirestationAddressDTO.class));
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
                    .andExpect(content().string("The request is not correct: please verify the request's body.\n"));
            verify(firestationService, Mockito.times(0)).addNewMapping(any(MappingFirestationAddressDTO.class));
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
            verify(firestationService, Mockito.times(0)).addNewMapping(any(MappingFirestationAddressDTO.class));
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
            verify(firestationService, Mockito.times(0)).addNewMapping(any(MappingFirestationAddressDTO.class));
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
            doReturn("The test is ok.\n").when(firestationService).updateMapping(any(MappingFirestationAddressDTO.class));
            // WHEN
            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"number\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The test is ok.\nhttp://localhost/firestation/1"))
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/firestation/1"));
            verify(firestationService, Mockito.times(1)).updateMapping(any(MappingFirestationAddressDTO.class));
        }

        @Test
        @DisplayName("GIVEN an ObjectNotFoundException returned by the firestation service, " +
                "WHEN we call the uri \"/firestation\", " +
                "THEN we should have an \"isNotFound\" status with the expected error message.")
        public void updateAddressObjectNotFoundTest() throws Exception {
            // GIVEN
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(firestationService).updateMapping(any());
            // WHEN
            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"number\":\"1\",\"address\":\"address test\"}"))
                    // THEN
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(firestationService, Mockito.times(1)).updateMapping(any());
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
                    .andExpect(content().string("The request is not correct: please verify the request's body.\n"));
            verify(firestationService, Mockito.times(0)).addNewMapping(any(MappingFirestationAddressDTO.class));
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
            verify(firestationService, Mockito.times(0)).addNewMapping(any(MappingFirestationAddressDTO.class));
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
            verify(firestationService, Mockito.times(0)).addNewMapping(any(MappingFirestationAddressDTO.class));
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
            doReturn("The test is Ok!").when(firestationService).deleteFirestationOrAddress("address test");
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", "address test"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The test is Ok!"));
            verify(firestationService, Mockito.times(1)).deleteFirestationOrAddress("address test");
        }

        @Test
        @DisplayName("GIVEN an ObjectNotFoundException returned by the firestation service, " +
                "WHEN we call the uri \"/firestation/{idOrAddress}\", " +
                "THEN we should have an \"isNotFound\" status with the expected error message.")
        public void deleteFirestationObjectNotFoundTest() throws Exception {
            // GIVEN
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(firestationService).deleteFirestationOrAddress("1");
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
                    // THEN
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(firestationService, Mockito.times(1)).deleteFirestationOrAddress("1");
        }

        @Test
        @DisplayName("GIVEN a NothingToDeleteException returned by the firestation service, " +
                "WHEN we call the uri \"/firestation/{idOrAddress}\", " +
                "THEN we should have an \"isBadRequest\" status with the expected error message.")
        public void deleteFirestationNothingToDeleteTest() throws Exception {
            // GIVEN
            NothingToDeleteException nothingToDeleteException = new NothingToDeleteException("error message");
            doThrow(nothingToDeleteException).when(firestationService).deleteFirestationOrAddress("1");
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(firestationService, Mockito.times(1)).deleteFirestationOrAddress("1");
        }

        @Test
        @DisplayName("GIVEN an FirestationNonEmptyException returned by the firestation service, " +
                "WHEN we call the uri \"/firestation/{idOrAddress}\", " +
                "THEN we should have an \"isBadRequest\" status with the expected error message.")
        public void deleteFirestationNonEmptyTest() throws Exception {
            // GIVEN
            FirestationNonEmptyException firestationNonEmptyException = new FirestationNonEmptyException("error message");
            doThrow(firestationNonEmptyException).when(firestationService).deleteFirestationOrAddress("1");
            // WHEN
            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
                    // THEN
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(firestationService, Mockito.times(1)).deleteFirestationOrAddress("1");
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
            verify(firestationService, Mockito.times(0)).deleteFirestationOrAddress(any());
        }

    }

}