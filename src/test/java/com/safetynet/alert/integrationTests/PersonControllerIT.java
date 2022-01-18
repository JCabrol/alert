//package com.safetynet.alert.integrationTests;
//
//import com.safetynet.alert.repository.PersonRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
//import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@Tag("PersonTests")
//@Slf4j
//@ActiveProfiles("test")
//@DirtiesContext(classMode = AFTER_CLASS)
//@SpringBootTest
//@AutoConfigureMockMvc
//
//public class PersonControllerIT {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private PersonRepository personRepository;
//
//    @DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
//    @Nested
//    @Tag("PersonControllerIntegrationTests")
//    @DisplayName("GET integration requests:")
//    class GetIntegrationTests {
//
//        @Test
//        @DisplayName("GIVEN a non empty table \"persons\" " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have an \"isOk\" status and the response's body should contain a JSon file with all the persons.")
//        public void getAllPersonsIntegrationTest() throws Exception {
//            //GIVEN
//            RequestBuilder requestBuilder = MockMvcRequestBuilders
//                    .get("/person");
//            //WHEN
//            mockMvc.perform(requestBuilder)
//                    //THEN
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                    .andExpect(jsonPath("$").isNotEmpty())
//                    .andExpect(jsonPath("$", hasSize(3)))
//                    .andExpect(jsonPath("$[0].firstName", is("FIRSTNAME1")));
//        }
//
//        @Test
//        @DisplayName("GIVEN an empty table \"persons\" " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have a \"noContent\" status and the response's body should be empty.")
//        public void getAllPersonsWithEmptyTableIntegrationTest() throws Exception {
//            //GIVEN
//            personRepository.deleteAll();
//            RequestBuilder requestBuilder = MockMvcRequestBuilders
//                    .get("/person");
//            //WHEN
//            mockMvc.perform(requestBuilder)
//                    //THEN
//                    .andExpect(status().isNoContent())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing person, " +
//                "WHEN we call the uri \"/person/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isOk\" status and the person with all correct attributes in the response.")
//        public void getPersonByNameIntegrationTest() throws Exception {
//            //GIVEN
//            RequestBuilder requestBuilder = MockMvcRequestBuilders
//                    .get("/person/{firstName}/{lastName}", "firstname2", "lastname2");
//            //WHEN
//            mockMvc.perform(requestBuilder)
//                    //THEN
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("firstName").value("FIRSTNAME2"))
//                    .andExpect(jsonPath("lastName").value("LASTNAME2"))
//                    .andExpect(jsonPath("mail").value("person2@mail.com"));
//        }
//
//        @DisplayName("GIVEN a non-existing person, " +
//                "WHEN we call the uri \"/person/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isNotFound\" status and an empty response.")
//        @Test
//        public void getPersonByNameNonExistingIntegrationTest() throws Exception {
//            mockMvc.perform(get("/person/{firstName}/{lastName}", "FIRSTNAME8", "LASTNAME8"))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//
//    }
//
//    @DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
//    @Nested
//    @Tag("PersonControllerIntegrationTests")
//    @DisplayName("POST integration requests:")
//    class PostIntegrationTests {
//
//        @Test
//        @DisplayName("GIVEN a person with all informations in the request's body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have an \"isCreated\" status and the header should return the right url to find the person created.")
//        public void addNewPersonIntegrationTest() throws Exception {
//            // GIVEN
//            String personToPost = "{\"firstName\":\"firstName4\",\"lastName\":\"lastName4\",\"address\":\"4 main street\"," +
//                    "\"city\":\"city4\",\"zip\":4444,\"phoneNumber\":\"444-444-4444\",\"mail\":\"person4@mail.com\"}";
//            // WHEN
//            mockMvc.perform(post("/person")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(personToPost))
//                    // THEN
//                    .andExpect(status().isCreated())
//                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/person/FIRSTNAME4/LASTNAME4"));
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with only required informations (firstName and LastName) in the request's body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have an \"isCreated\" status and the header should return the right url to find the person created.")
//        public void addNewPersonWithOnlyFirstNameAndLastNameIntegrationTest() throws Exception {
//            //GIVEN
//            String personToPost = "{\"firstName\":\"firstName4\",\"lastName\":\"lastName4\"}";
//            //WHEN
//            mockMvc.perform(post("/person")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(personToPost))
//                    // THEN
//                    .andExpect(status().isCreated())
//                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/person/FIRSTNAME4/LASTNAME4"));
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with missing required informations (firstName or LastName) in the request's body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have a \"badRequest\" status and the response should be empty.")
//        public void addNewPersonWithMissingRequiredInformationIntegrationTest() throws Exception {
//            //GIVEN
//            String personToPost = "{\"firstName\":\"firstName4\",\"address\":\"4 main street\"," +
//                    "\"city\":\"city4\",\"zip\":4444,\"phoneNumber\":\"444-444-4444\",\"mail\":\"person4@mail.com\"}";
//            //WHEN
//            mockMvc.perform(post("/person")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(personToPost))
//                    //THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//
//        @Test
//        @DisplayName("GIVEN a request without body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have a \"badRequest\" status and the response should be empty.")
//        public void addNewPersonWithoutBodyIntegrationTest() throws Exception {
//            //GIVEN
//            //WHEN
//            mockMvc.perform(post("/person")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(""))
//                    //THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with a firstName and lastName combination already existing in the request's body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have a \"badRequest\" status and the response should be empty.")
//        public void addNewPersonAlreadyExistingIntegrationTest() throws Exception {
//            //GIVEN
//            String personToPost = "{\"firstName\":\"FirstName1\",\"lastName\":\"LastName1\",\"address\":\"1 main street\"," +
//                    "\"city\":\"city1\",\"zip\":1111,\"phoneNumber\":\"111-111-1111\",\"mail\":\"person1@mail.com\"}";
//            //WHEN
//            mockMvc.perform(post("/person")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(personToPost))
//                    //THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//    }
//
//    @DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
//    @Nested
//    @Tag("PersonControllerIntegrationTests")
//    @DisplayName("PUT integration requests:")
//    class PutIntegrationTests {
//
//        @Test
//        @DisplayName("GIVEN a person with all updatable informations in the request's body, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"isOK\" status " +
//                "and the response should contain the right person with the same first name, last name and id " +
//                "but all other informations updated.")
//        public void putPersonWithAllUpdatesIntegrationTest() throws Exception {
//            //GIVEN
//            if (personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").isPresent()) {
//                int idBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getId();
////                String addressBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getAddress();
////                String cityBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getCity();
////                int zipBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getZip();
////                String phoneNumberBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getPhoneNumber();
//                String mailBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getMail();
//                String updateInformations = "{\"address\":\"4 main street\",\"city\":\"city4\",\"zip\":4444,\"phoneNumber\":\"444-444-4444\",\"mail\":\"person4@mail.com\"}";
//                RequestBuilder requestBuilder = MockMvcRequestBuilders
//                        .put("/person/{firstName}/{lastName}", "firstname2", "lastname2")
//                        .accept(MediaType.APPLICATION_JSON).content(updateInformations)
//                        .contentType(MediaType.APPLICATION_JSON);
//                //WHEN
//                mockMvc.perform(requestBuilder)
//                        //THEN
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.id", is(idBeforeUpdate)))
//                        .andExpect(jsonPath("$.firstName", is("FIRSTNAME2")))
//                        .andExpect(jsonPath("$.lastName", is("LASTNAME2")))
//                        .andExpect(jsonPath("$.city", is("CITY4")))
//                        .andExpect(jsonPath("$.address", is("4 main street")))
//                        .andExpect(jsonPath("$.zip", is(4444)))
//                        .andExpect(jsonPath("$.phoneNumber", is("444-444-4444")))
//                        .andExpect(jsonPath("$.mail", is("person4@mail.com")));
////                assertThat(addressBeforeUpdate).isNotEqualTo("4 main street");
////                assertThat(cityBeforeUpdate).isNotEqualTo("CITY4");
////                assertThat(zipBeforeUpdate).isNotEqualTo(4444);
////                assertThat(phoneNumberBeforeUpdate).isNotEqualTo("444-444-4444");
//                assertThat(mailBeforeUpdate).isNotEqualTo("person4@mail.com");
//            } else {
//                Assertions.fail("Person to update was not found");
//            }
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with some updatable informations in the request's body, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"isOK\" status " +
//                "and the response should contain the right person with the same not updated informations" +
//                "but all other informations updated.")
//        public void putPersonWithOnlyAddressAndMailUpdatesIntegrationTest() throws Exception {
//            //GIVEN
//            if (personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").isPresent()) {
//                int idBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getId();
////                String addressBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getAddress();
////                String cityBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getCity();
////                int zipBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getZip();
////                String phoneNumberBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getPhoneNumber();
//                String mailBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getMail();
//                String updateInformations = "{\"address\":\"4 main street\",\"mail\":\"person4@mail.com\"}";
//                RequestBuilder requestBuilder = MockMvcRequestBuilders
//                        .put("/person/{firstName}/{lastName}", "firstname2", "lastname2")
//                        .accept(MediaType.APPLICATION_JSON).content(updateInformations)
//                        .contentType(MediaType.APPLICATION_JSON);
//                //WHEN
//                mockMvc.perform(requestBuilder)
//                        //THEN
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.id", is(idBeforeUpdate)))
//                        .andExpect(jsonPath("$.firstName", is("FIRSTNAME2")))
//                        .andExpect(jsonPath("$.lastName", is("LASTNAME2")))
////                        .andExpect(jsonPath("$.city", is(cityBeforeUpdate)))
//                        .andExpect(jsonPath("$.address", is("4 main street")))
////                        .andExpect(jsonPath("$.zip", is(zipBeforeUpdate)))
////                        .andExpect(jsonPath("$.phoneNumber", is(phoneNumberBeforeUpdate)))
//                        .andExpect(jsonPath("$.mail", is("person4@mail.com")));
////                assertThat(addressBeforeUpdate).isNotEqualTo("4 main street");
//                assertThat(mailBeforeUpdate).isNotEqualTo("person4@mail.com");
//            } else {
//                Assertions.fail("Person to update was not found");
//            }
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with one updatable information in the request's body, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"isOK\" status " +
//                "and the response should contain the right person with the same not updated informations" +
//                "but phoneNumber updated.")
//        public void putPersonWithOnlyPhoneNumberUpdateIntegrationTest() throws Exception {
//            //GIVEN
//            if (personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").isPresent()) {
//                int idBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getId();
////                String addressBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getAddress();
////                String cityBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getCity();
////                int zipBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getZip();
////                String phoneNumberBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getPhoneNumber();
//                String mailBeforeUpdate = personRepository.findByFirstNameAndLastName("FIRSTNAME2", "LASTNAME2").get().getMail();
//                String updateInformations = "{\"phoneNumber\":\"555-555-5555\"}";
//                RequestBuilder requestBuilder = MockMvcRequestBuilders
//                        .put("/person/{firstName}/{lastName}", "firstname2", "lastname2")
//                        .accept(MediaType.APPLICATION_JSON).content(updateInformations)
//                        .contentType(MediaType.APPLICATION_JSON);
//                //WHEN
//                mockMvc.perform(requestBuilder)
//                        //THEN
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.id", is(idBeforeUpdate)))
//                        .andExpect(jsonPath("$.firstName", is("FIRSTNAME2")))
//                        .andExpect(jsonPath("$.lastName", is("LASTNAME2")))
////                        .andExpect(jsonPath("$.city", is(cityBeforeUpdate)))
////                        .andExpect(jsonPath("$.address", is(addressBeforeUpdate)))
////                        .andExpect(jsonPath("$.zip", is(zipBeforeUpdate)))
//                        .andExpect(jsonPath("$.phoneNumber", is("555-555-5555")))
//                        .andExpect(jsonPath("$.mail", is(mailBeforeUpdate)));
////                assertThat(phoneNumberBeforeUpdate).isNotEqualTo("555-555-5555");
//            } else {
//                Assertions.fail("Person to update was not found");
//            }
//        }
//
//        @Test
//        @DisplayName("GIVEN a person non existing in the request's body, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"notFound\" status " +
//                "and the response's body should be empty.")
//        public void putPersonNonExistingIntegrationTest() throws Exception {
//            //GIVEN
//            String updateInformations = "{\"address\":\"4 main street\",\"city\":\"city4\",\"zip\":4444,\"phoneNumber\":\"444-444-4444\",\"mail\":\"person4@mail.com\"}";
//            RequestBuilder requestBuilder = MockMvcRequestBuilders
//                    .put("/person/{firstName}/{lastName}", "firstname8", "lastname8")
//                    .accept(MediaType.APPLICATION_JSON).content(updateInformations)
//                    .contentType(MediaType.APPLICATION_JSON);
//            //WHEN
//            mockMvc.perform(requestBuilder)
//                    //THEN
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//
//        @Test
//        @DisplayName("GIVEN a request without body, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"badRequest\" status " +
//                "and the response's body should be empty.")
//        public void putPersonWithNoBodyInformationsIntegrationTest() throws Exception {
//            //GIVEN
//            RequestBuilder requestBuilder = MockMvcRequestBuilders
//                    .put("/person/{firstName}/{lastName}", "firstname2", "lastname2")
//                    .accept(MediaType.APPLICATION_JSON)
//                    .contentType(MediaType.APPLICATION_JSON);
//            //WHEN
//            mockMvc.perform(requestBuilder)
//                    //THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//    }
//
//    @DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
//    @Nested
//    @Tag("PersonControllerIntegrationTests")
//    @DisplayName("DELETE integration requests:")
//    class DeleteTests {
//
//        @Test
//        @DisplayName("GIVEN an existing person, " +
//                "WHEN we call the uri \"/person/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isEmpty\" status and an empty response.")
//        public void deletePersonByNameTest() throws Exception {
//            //GIVEN
//            RequestBuilder requestBuilder = MockMvcRequestBuilders
//                    .delete("/person/{firstName}/{lastName}", "firstname2", "lastname2");
//            //WHEN
//            MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//            MockHttpServletResponse response = result.getResponse();
//            //THEN
//            assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
//            assertThat(response.getContentAsString()).isEqualTo("");
//        }
//
//        @Test
//        @DisplayName("GIVEN a non existing person, " +
//                "WHEN we call the uri \"/person/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isNotFound\" status and an empty response.")
//        public void deletePersonByNameNonExistingTest() throws Exception {
//            //GIVEN
//            RequestBuilder requestBuilder = MockMvcRequestBuilders
//                    .delete("/person/{firstName}/{lastName}", "firstname5", "lastname5");
//            //WHEN
//            MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//            MockHttpServletResponse response = result.getResponse();
//            //THEN
//            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
//            assertThat(response.getContentAsString()).isEqualTo("");
//        }
//    }
//}
//
