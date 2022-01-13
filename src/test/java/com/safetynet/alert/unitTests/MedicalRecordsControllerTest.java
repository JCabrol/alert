//package com.safetynet.alert.unitTests;
//
//import com.safetynet.alert.exceptions.*;
//import com.safetynet.alert.model.*;
//import com.safetynet.alert.model.DTO.MedicalRecordDTO;
//import com.safetynet.alert.service.MedicalRecordsService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@Tag("MedicalRecordsTests")
//@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@DirtiesContext(classMode = AFTER_CLASS)
//public class MedicalRecordsControllerTest {
//
//
//    @MockBean
//    private MedicalRecordsService medicalRecordsService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//
//    @Nested
//    @Tag("MedicalRecordsControllerTests")
//    @DisplayName("GET requests:")
//    class medicalRecordsGetTests {
//
//        @Test
//        @DisplayName("GIVEN a non empty list of medical records " +
//                "WHEN we call the uri \"/medicalRecord\", " +
//                "THEN we should have an \"isOk\" status and the response's body should contain a String with all the medical records.")
//        void getAllMedicalRecordsTest() throws Exception {
//            // GIVEN
//            MedicalRecords medicalRecords = new MedicalRecords();
//            List<MedicalRecords> medicalRecordsList = new ArrayList<>();
//           medicalRecordsList.add(medicalRecords);
//            doReturn(medicalRecordsList).when(medicalRecordsService).getMedicalRecords();
//            doReturn("The test is ok!").when(medicalRecordsService).medicalRecordsToString(medicalRecordsList);
//            //WHEN
//            mockMvc.perform(get("/medicalRecord"))
//                    // THEN
//                    .andExpect(status().isOk())
//                    .andExpect(content().string("The test is ok!"));
//            verify(medicalRecordsService, Mockito.times(1)).getMedicalRecords();
//            verify(medicalRecordsService, Mockito.times(1)).medicalRecordsToString(medicalRecordsList);
//        }
//
//        @Test
//        @DisplayName("GIVEN an empty list of medical records " +
//                "WHEN we call the uri \"/medicalRecord\", " +
//                "THEN we should have an \"isNotFound\" status and the response's body should contain a String indicating that there are no medical records.")
//        void getAllMedicalRecordsEmptyTest() throws Exception {
//            // GIVEN
//            doThrow(EmptyFirestationsException.class).when(medicalRecordsService).getMedicalRecords();
//
//            //WHEN
//            mockMvc.perform(get("/medicalRecord"))
//                    // THEN
//                    .andExpect(status().isNotFound())
//                    .andExpect(content().string(""));
//            verify(medicalRecordsService, Mockito.times(1)).getMedicalRecords();
//            verify(medicalRecordsService, Mockito.times(0)).medicalRecordsToString(any());
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing person with medical records, " +
//                "WHEN we call the uri \"/medicalRecords/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isOk\" status and the response's body is a String with all correct information about the medical records found.")
//        public void getMedicalRecordsExistingTest() throws Exception {
//            // GIVEN
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            Person personTest = new Person(firstName,lastName);
//            MedicalRecords medicalRecords = new MedicalRecords();
//            medicalRecords.addPerson(personTest);
//            medicalRecords.setBirthdate("birthdateTest");
//            medicalRecords.addMedication(new Medication("medicationTest"));
//            doReturn(medicalRecords).when(medicalRecordsService).getMedicalRecordsByName(firstName, lastName);
//            // WHEN
//            mockMvc.perform(get("/medicalRecord/{firstName}/{lastName}", firstName,lastName))
//                    // THEN
//                    .andExpect(status().isOk())
//                    .andExpect(content().string("Medical records about FIRSTNAMETEST LASTNAMETEST :\n" +
//                            "Birthdate : birthdateTest\n" +
//                            "Medications :\n" +
//                           "- medicationTest\n" +
//                          "This person has no allergy.\n\n"));
//            verify(medicalRecordsService, Mockito.times(1)).getMedicalRecordsByName(firstName,lastName);
//        }
//
//        @Test
//        @DisplayName("GIVEN non-existing medical records, " +
//                "WHEN we call the uri \"/medicalRecords/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isNotFound\" status and the response's body is an empty String.")
//        public void getFirestationByIdNonExistingTest() throws Exception {
//            // GIVEN
//            String firstName = "firstNameTest";
//            String lastName = "lastNameTest";
//            doThrow(MedicalRecordsNotFoundException.class).when(medicalRecordsService).getMedicalRecordsByName(any(), any());
//            // WHEN
//            mockMvc.perform(get("/medicalRecord/{firstName}/{lastName}", firstName,lastName))
//                    // THEN
//                    .andExpect(status().isNotFound())
//                    .andExpect(content().string(""));
//            verify(medicalRecordsService, Mockito.times(1)).getMedicalRecordsByName(firstName, lastName);
//        }
//
//    }
//
//    @Nested
//    @Tag("MedicalRecordsControllerTests")
//    @DisplayName("POST requests:")
//    class PostTests {
//
//        @Test
//        @DisplayName("GIVEN medical records returned by the MedicalRecordsService, " +
//                "WHEN we call the uri \"/medicalRecord\", " +
//                "THEN we should have an \"isCreated\" status, the right confirmation message and the header should return the right url to find the medicalRecord created.")
//        public void addMedicalRecordTest() throws Exception {
//            // GIVEN
//            Person personTest = new Person("firstNameTest","lastNameTest");
//            MedicalRecords medicalRecords = new MedicalRecords();
//            medicalRecords.addPerson(personTest);
//            medicalRecords.setBirthdate("birthdateTest");
//            doReturn(medicalRecords).when(medicalRecordsService).addNewMedicalRecords(any(MedicalRecordDTO.class));
//            // WHEN
//            mockMvc.perform(post("/medicalRecord")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content("{\"firstName\":\"firstNameTest\",\"lastName\":\"lastNameTest\",\"birthdate\":\"birthdateTest\"}"))
//                    // THEN
//                    .andExpect(status().isCreated())
//                    .andExpect(content().string("Medical records about firstNameTest lastNameTest have been registered.\n"))
//                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/medicalRecord/firstNameTest/lastNameTest"));
//            verify(medicalRecordsService, Mockito.times(1)).addNewMedicalRecords(any(MedicalRecordDTO.class));
//        }
//
//        @Test
//        @DisplayName("GIVEN a NotRightFormatToPostException returned by the MedicalRecordsService " +
//                "WHEN we call the uri \"/medicalRecord\", " +
//                "THEN we should have an \"isCreated\" status, the right confirmation message and the header should return the right url to find the medicalRecord created.")
//        public void addMedicalRecordWithoutRequiredInformationTest() throws Exception {
//            // GIVEN
//           doThrow(NotRightFormatToPostException.class).when(medicalRecordsService).addNewMedicalRecords(any(MedicalRecordDTO.class));
//            // WHEN
//            mockMvc.perform(post("/medicalRecord")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{\"lastName\":\"lastNameTest\",\"birthdate\":\"birthdateTest\"}"))
//                    // THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().string(""));
//            verify(medicalRecordsService, Mockito.times(1)).addNewMedicalRecords(any(MedicalRecordDTO.class));
//        }
//
//        @Test
//        @DisplayName("GIVEN request without body" +
//                "WHEN we call the uri \"/medicalRecord\", " +
//                "THEN we should have a \"badRequest\" status and the correct error message.")
//        public void addNewMappingMessageNotReadableExceptionTest() throws Exception {
//            // GIVEN
//
//            // WHEN
//            mockMvc.perform(post("/medicalRecord")
//                            .contentType(MediaType.APPLICATION_JSON))
//                    // THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().string("The request is not correct: please verify the request contains a body.\n"));
//            verify(medicalRecordsService, Mockito.times(0)).addNewMedicalRecords(any(MedicalRecordDTO.class));
//        }
//
//        @Test
//        @DisplayName("GIVEN request with body not in Json" +
//                "WHEN we call the uri \"/medicalRecord\", " +
//                "THEN we should have a \"badRequest\" status and the correct error message.")
//        public void addNewMappingHttpMediaTypeNotSupportedExceptionTest() throws Exception {
//            // GIVEN
//
//            // WHEN
//            mockMvc.perform(post("/medicalRecord")
//                            .contentType(MediaType.TEXT_PLAIN))
//                    // THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().string("The request is not correct : the request's body should be in json.\n"));
//            verify(medicalRecordsService, Mockito.times(0)).addNewMedicalRecords(any(MedicalRecordDTO.class));
//        }
//
//        @Test
//        @DisplayName("GIVEN request with not correct url" +
//                "WHEN we call the uri \"/medicalRecord\", " +
//                "THEN we should have a \"badRequest\" status and the correct error message.")
//        public void addNewMappingHttpRequestMethodNotSupportedExceptionTest() throws Exception {
//            // GIVEN
//
//            // WHEN
//            mockMvc.perform(post("/medicalRecord/firstNameTest/lastNameTest")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{\"firstName\":\"firstNameTest\",\"lastName\":\"lastNameTest\",\"birthdate\":\"birthdateTest\"}"))
//                    // THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
//            verify(medicalRecordsService, Mockito.times(0)).addNewMedicalRecords(any(MedicalRecordDTO.class));
//        }
//
//
////
////        @Test
////        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
////        public void addNewMappingNotRightFormatToPostExceptionTest() throws Exception {
////            // GIVEN
////            doThrow(NotRightFormatToPostException.class).when(firestationService).addNewMapping(1, "address test");
////            // WHEN
////            mockMvc.perform(post("/firestation")
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string(""));
////            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
////        public void addNewMappingAlreadyExistingTest() throws Exception {
////            // GIVEN
////            doThrow(MappingAlreadyExistingException.class).when(firestationService).addNewMapping(1, "address test");
////            // WHEN
////            mockMvc.perform(post("/firestation")
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string(""));
////            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN request without body" +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have a \"badRequest\" status and the correct error message.")
////        public void addNewMappingMessageNotReadableExceptionTest() throws Exception {
////            // GIVEN
////
////            // WHEN
////            mockMvc.perform(post("/firestation")
////                            .contentType(MediaType.APPLICATION_JSON))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string("The request is not correct: please verify the request contains a body.\n"));
////            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN request with body not in Json" +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have a \"badRequest\" status and the correct error message.")
////        public void addNewMappingHttpMediaTypeNotSupportedExceptionTest() throws Exception {
////            // GIVEN
////
////            // WHEN
////            mockMvc.perform(post("/firestation")
////                            .contentType(MediaType.TEXT_PLAIN))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string("The request is not correct : the request's body should be in json.\n"));
////            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN request with not correct url" +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have a \"badRequest\" status and the correct error message.")
////        public void addNewMappingHttpRequestMethodNotSupportedExceptionTest() throws Exception {
////            // GIVEN
////
////            // WHEN
////            mockMvc.perform(post("/firestation/1")
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
////            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
////        }
////
////    }
////
////    @Nested
////    @Tag("FirestationControllerTests")
////    @DisplayName("PUT requests:")
////    class PutTests {
////
////        @Test
////        @DisplayName("GIVEN a mapping with a firestation and an address in the request's body, " +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
////        public void updateAddressTest() throws Exception {
////            // GIVEN
////            doReturn("").when(firestationService).deleteAddress("address test");
////            doReturn("").when(firestationService).addNewMapping(1, "address test");
////            // WHEN
////            mockMvc.perform(put("/firestation")
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
////                    // THEN
////                    .andExpect(status().isOk())
////                    .andExpect(content().string("The address address test have been updated to the firestation number 1."))
////                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/firestation/1"));
////            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
////            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
////        public void updateAddressNotRightFormatToPostExceptionTest() throws Exception {
////            // GIVEN
////            doReturn("").when(firestationService).deleteAddress("address test");
////            doThrow(NotRightFormatToPostException.class).when(firestationService).addNewMapping(1, "address test");
////            // WHEN
////            mockMvc.perform(put("/firestation")
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string(""));
////            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
////            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
////        public void updateAddressMappingAlreadyExistingTest() throws Exception {
////            // GIVEN
////            doReturn("").when(firestationService).deleteAddress("address test");
////            doThrow(MappingAlreadyExistingException.class).when(firestationService).addNewMapping(1, "address test");
////            // WHEN
////            mockMvc.perform(put("/firestation")
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string(""));
////            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
////            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN a mapping with an existing firestation and a new address in the request's body, " +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have an \"isCreated\" status and the header should return the right url to find the firestation where the new address have been created.")
////        public void updateAddressNothingToDeleteTest() throws Exception {
////            // GIVEN
////            doThrow(NothingToDeleteException.class).when(firestationService).deleteAddress("address test");
////            doReturn("").when(firestationService).addNewMapping(1, "address test");
////            // WHEN
////            mockMvc.perform(put("/firestation")
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
////                    // THEN
////                    .andExpect(status().isOk())
////                    .andExpect(content().string("The address address test have been updated to the firestation number 1."))
////                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/firestation/1"));
////            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
////            verify(firestationService, Mockito.times(1)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN request without body" +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have a \"badRequest\" status and the correct error message.")
////        public void updateAddressMessageNotReadableExceptionTest() throws Exception {
////            // GIVEN
////
////            // WHEN
////            mockMvc.perform(put("/firestation")
////                            .contentType(MediaType.APPLICATION_JSON))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string("The request is not correct: please verify the request contains a body.\n"));
////            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN request with body not in Json" +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have a \"badRequest\" status and the correct error message.")
////        public void updateAddressHttpMediaTypeNotSupportedExceptionTest() throws Exception {
////            // GIVEN
////
////            // WHEN
////            mockMvc.perform(put("/firestation")
////                            .contentType(MediaType.TEXT_PLAIN))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string("The request is not correct : the request's body should be in json.\n"));
////            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
////        }
////
////        @Test
////        @DisplayName("GIVEN request with not correct url" +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have a \"badRequest\" status and the correct error message.")
////        public void updateAddressHttpRequestMethodNotSupportedExceptionTest() throws Exception {
////            // GIVEN
////
////            // WHEN
////            mockMvc.perform(put("/firestation/1")
////                            .contentType(MediaType.APPLICATION_JSON)
////                            .content("{\"firestationId\":\"1\",\"address\":\"address test\"}"))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
////            verify(firestationService, Mockito.times(0)).addNewMapping(1, "address test");
////        }
////    }
////
////    @Nested
////    @Tag("FirestationControllerTests")
////    @DisplayName("DELETE requests:")
////    class DeleteTests {
////
////        @Test
////        @DisplayName("GIVEN an existing address to delete " +
////                "WHEN we call the uri \"/firestation/{address}\", " +
////                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
////        public void deleteAddressTest() throws Exception {
////            // GIVEN
////            doReturn("The test is Ok!").when(firestationService).deleteAddress("address test");
////            // WHEN
////            mockMvc.perform(delete("/firestation/{idOrAddress}", "address test"))
////                    // THEN
////                    .andExpect(status().isOk())
////                    .andExpect(content().string("The test is Ok!"));
////            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
////            verify(firestationService, Mockito.times(0)).deleteFirestation(anyInt());
////        }
////
////        @Test
////        @DisplayName("GIVEN an existing address to delete " +
////                "WHEN we call the uri \"/firestation/{address}\", " +
////                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
////        public void deleteAddressNonExistingTest() throws Exception {
////            // GIVEN
////            doThrow(NothingToDeleteException.class).when(firestationService).deleteAddress("address test");
////            // WHEN
////            mockMvc.perform(delete("/firestation/{idOrAddress}", "address test"))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string(""));
////            verify(firestationService, Mockito.times(1)).deleteAddress("address test");
////            verify(firestationService, Mockito.times(0)).deleteFirestation(anyInt());
////        }
////        @Test
////        @DisplayName("GIVEN an existing address to delete " +
////                "WHEN we call the uri \"/firestation/{address}\", " +
////                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
////        public void deleteFirestationTest() throws Exception {
////            // GIVEN
////            doNothing().when(firestationService).deleteFirestation(1);
////            // WHEN
////            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
////                    // THEN
////                    .andExpect(status().isOk())
////                    .andExpect(content().string("The firestation number 1 have been deleted."));
////            verify(firestationService, Mockito.times(0)).deleteAddress(any());
////            verify(firestationService, Mockito.times(1)).deleteFirestation(1);
////        }
////
////        @Test
////        @DisplayName("GIVEN an existing address to delete " +
////                "WHEN we call the uri \"/firestation/{address}\", " +
////                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
////        public void deleteFirestationNonExistingTest() throws Exception {
////            // GIVEN
////            doThrow(NothingToDeleteException.class).when(firestationService).deleteFirestation(1);
////            // WHEN
////            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string(""));
////            verify(firestationService, Mockito.times(0)).deleteAddress(any());
////            verify(firestationService, Mockito.times(1)).deleteFirestation(1);
////        }
////
////        @Test
////        @DisplayName("GIVEN an existing address to delete " +
////                "WHEN we call the uri \"/firestation/{address}\", " +
////                "THEN we should have an \"isOk\" status and the header should return the right url to find the firestation created.")
////        public void deleteFirestationNonEmptyTest() throws Exception {
////            // GIVEN
////            doThrow(FirestationNonEmptyException.class).when(firestationService).deleteFirestation(1);
////            // WHEN
////            mockMvc.perform(delete("/firestation/{idOrAddress}", 1))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string(""));
////            verify(firestationService, Mockito.times(0)).deleteAddress(any());
////            verify(firestationService, Mockito.times(1)).deleteFirestation(1);
////        }
////
////        @Test
////        @DisplayName("GIVEN request with not correct url" +
////                "WHEN we call the uri \"/firestation\", " +
////                "THEN we should have a \"badRequest\" status and the correct error message.")
////        public void deleteFirestationOrAddressHttpRequestMethodNotSupportedExceptionTest() throws Exception {
////            // GIVEN
////
////            // WHEN
////            mockMvc.perform(delete("/firestation"))
////                    // THEN
////                    .andExpect(status().isBadRequest())
////                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
////            verify(firestationService, Mockito.times(0)).deleteAddress(any());
////            verify(firestationService, Mockito.times(0)).deleteFirestation(anyInt());
////        }
//
//    }
//
//}