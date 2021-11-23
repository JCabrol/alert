//package com.safetynet.alert.unitTests;
//
//import com.safetynet.alert.exceptions.EmptyPersonsException;
//import com.safetynet.alert.model.Person;
//import com.safetynet.alert.service.PersonService;
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.util.Lists;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
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
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Optional;
//
//import static com.fasterxml.jackson.databind.ObjectWriter.GeneratorSettings.empty;
//import static org.hamcrest.Matchers.*;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@Tag("PersonTests")
//@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@DirtiesContext(classMode = AFTER_CLASS)
//public class PersonControllerTest {
//
//
//    @MockBean
//    private PersonService personService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//
//    @Nested
//    @Tag("PersonControllerTests")
//    @DisplayName("GET requests:")
//    class GetTests {
//
//        @Test
//        @DisplayName("GIVEN a non empty list of persons " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have an \"isOk\" status and the response's body should contain a JSon file with the persons and their informations.")
//        void getAllPersonsTest() throws Exception {
//            // GIVEN
//            Person person1 = new Person(1, "FirstName1", "LastName1", "1 main street", "CITY1", 1111, "111-111-1111", "person1@mail.com");
//            Person person2 = new Person(2, "FirstName2", "LastName2", "2 main street", "CITY2", 2222, "222-222-2222", "person2@mail.com");
//            doReturn(Lists.newArrayList(person1, person2)).when(personService).getPersons();
//            // WHEN
//            mockMvc.perform(get("/person"))
//                    // THEN
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$", hasSize(2)))
//                    .andExpect(jsonPath("$[0].firstName", is("FirstName1")))
//                    .andExpect(jsonPath("$[0].lastName", is("LastName1")))
//                    .andExpect(jsonPath("$[0].address", is("1 main street")))
//                    .andExpect(jsonPath("$[0].city", is("CITY1")))
//                    .andExpect(jsonPath("$[0].zip", is(1111)))
//                    .andExpect(jsonPath("$[0].phoneNumber", is("111-111-1111")))
//                    .andExpect(jsonPath("$[0].mail", is("person1@mail.com")))
//                    .andExpect(jsonPath("$[1].firstName", is("FirstName2")))
//                    .andExpect(jsonPath("$[1].lastName", is("LastName2")))
//                    .andExpect(jsonPath("$[1].address", is("2 main street")))
//                    .andExpect(jsonPath("$[1].city", is("CITY2")))
//                    .andExpect(jsonPath("$[1].zip", is(2222)))
//                    .andExpect(jsonPath("$[1].phoneNumber", is("222-222-2222")))
//                    .andExpect(jsonPath("$[1].mail", is("person2@mail.com")));
//        }
//
//        @Test
//        @DisplayName("GIVEN an empty list of persons " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have a \"notFound\" status and the response's body should be empty.")
//        public void getAllPersonsWhenEmptyTest() throws Exception {
//            //GIVEN
//            //ArrayList<Person> persons = new ArrayList<Person>();
//            doThrow(EmptyPersonsException.class).when(personService).getPersons();
//            //WHEN
//            mockMvc.perform(get("/person"))
//                    //THEN
//                    .andExpect(status().isNotFound())
//                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof EmptyPersonsException));
//        }
//        @Test
//        @DisplayName("GIVEN an empty list of persons " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have a \"notFound\" status and the response's body should be empty.")
//        public void getAllPersonsWhenTableNotExistTest() throws Exception {
//            //GIVEN
//            //ArrayList<Person> persons = new ArrayList<Person>();
//           doReturn(null).when(personService).getPersons();
//            //WHEN
//            mockMvc.perform(get("/person"))
//                    //THEN
//                    .andExpect(status().isNotFound())
//                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof SQLException));
//        }
//
//        @Test
//        @DisplayName("GIVEN an existing person, " +
//                "WHEN we call the uri \"/person/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isOk\" status and the person with all correct attributes in the response.")
//        public void getPersonByNameTest() throws Exception {
//            // GIVEN
//            Person person1 = new Person(1, "FirstName1", "LastName1", "1 main street", "CITY1", 1111, "111-111-1111", "person1@mail.com");
//            doReturn(Optional.of(person1)).when(personService).getPersonByName("FirstName1", "LastName1");
//            // WHEN
//            mockMvc.perform(get("/person/{firstName}/{lastName}", "FirstName1", "LastName1"))
//                    // THEN
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$.id", is(1)))
//                    .andExpect(jsonPath("$.firstName", is("FirstName1")))
//                    .andExpect(jsonPath("$.lastName", is("LastName1")))
//                    .andExpect(jsonPath("$.address", is("1 main street")))
//                    .andExpect(jsonPath("$.city", is("CITY1")))
//                    .andExpect(jsonPath("$.zip", is(1111)))
//                    .andExpect(jsonPath("$.phoneNumber", is("111-111-1111")))
//                    .andExpect(jsonPath("$.mail", is("person1@mail.com")));
//        }
//
//        @DisplayName("GIVEN a non existing person, " +
//                "WHEN we call the uri \"/person/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isNotFound\" status and an empty response.")
//        @Test
//        public void getPersonByNameWithNonExistingPersonTest() throws Exception {
//            doReturn(Optional.empty()).when(personService).getPersonByName("FirstName1", "LastName1");
//            mockMvc.perform(get("/person/{firstName}/{lastName}", "FirstName1", "LastName1"))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//    }
//
//    @Nested
//    @Tag("PersonControllerTests")
//    @DisplayName("POST requests:")
//    class PostTests {
//
//        @Test
//        @DisplayName("GIVEN a person with all informations in the request's body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have an \"isCreated\" status and the header should return the right url to find the person created.")
//        public void addNewPersonTest() throws Exception {
//            // GIVEN
//            String personToPost = "{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"address\":\"1 main street\"," +
//                    "\"city\":\"city1\",\"zip\":1111,\"phoneNumber\":\"111-111-1111\",\"mail\":\"person1@mail.com\"}";
//            Person personToReturn = new Person(1, "FirstName1", "LastName1", "1 main street", "CITY1", 1111, "111-111-1111", "person1@mail.com");
//            doReturn(personToReturn).when(personService).updatePerson(any(Person.class),any(Person.class));
//            // WHEN
//            mockMvc.perform(post("/person")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(personToPost))
//
//                    // THEN
//                    .andExpect(status().isCreated())
//                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/person/FirstName1/LastName1"));
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with only required informations (firstName and LastName) in the request's body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have an \"isCreated\" status and the header should return the right url to find the person created.")
//        public void addNewPersonWithOnlyFirstNameAndLastNameTest() throws Exception {
//            //GIVEN
//            Person person1 = new Person(1, "FirstName1", "LastName1", "1 main street", "CITY1", 1111, "111-111-1111", "person1@mail.com");
//            String personToPost = "{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\"}";
//            doReturn(person1).when(personService).updatePerson(any(Person.class),any(Person.class));
//            //WHEN
//            mockMvc.perform(post("/person")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(personToPost))
//                    // THEN
//                    .andExpect(status().isCreated())
//                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/person/FirstName1/LastName1"));
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with missing required informations (firstName or LastName) in the request's body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have a \"badRequest\" status and the response should be empty.")
//        public void addNewPersonWithMissingRequiredInformationsTest() throws Exception {
//            //GIVEN
//            Person person1 = new Person(1, "FirstName1", "LastName1", "1 main street", "CITY1", 1111, "111-111-1111", "person1@mail.com");
//            String examplePersonJson = "{\"firstName\":\"firstName4\",\"address\":\"4 main street\"," +
//                    "\"city\":\"city4\",\"zip\":4444,\"phoneNumber\":\"444-444-4444\",\"mail\":\"person4@mail.com\"}";
//            doReturn(person1).when(personService).updatePerson(any(Person.class),any(Person.class));
//            //WHEN
//            mockMvc.perform(post("/person")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(examplePersonJson))
//                    //THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//
//        @Test
//        @DisplayName("GIVEN a request without body, " +
//                "WHEN we call the uri \"/person\", " +
//                "THEN we should have a \"badRequest\" status and the response should be empty.")
//        public void addNewPersonWithoutBodyTest() throws Exception {
//            //GIVEN
//            Person person1 = new Person(1, "FirstName1", "LastName1", "1 main street", "CITY1", 1111, "111-111-1111", "person1@mail.com");
//            doReturn(person1).when(personService).updatePerson(any(Person.class),any(Person.class));
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
//        public void addNewPersonTestWhenAlreadyExisting() throws Exception {
//            //GIVEN
//            String personToPost = "{\"firstName\":\"FirstName1\",\"lastName\":\"LastName1\",\"address\":\"1 main street\"," +
//                    "\"city\":\"city1\",\"zip\":1111,\"phoneNumber\":\"111-111-1111\",\"mail\":\"person1@mail.com\"}";
//            Person personExisting = new Person(1, "FirstName1", "LastName1", "1 main street", "CITY1", 1111, "111-111-1111", "person1@mail.com");
//            doReturn(Optional.of(personExisting)).when(personService).getPersonByName("FIRSTNAME1", "LASTNAME1");
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
//    @Nested
//    @Tag("PutPersonTests")
//    @DisplayName("PUT requests:")
//    class PutTests {
//
//        @Test
//        @DisplayName("GIVEN a person with all updatable informations in the request's body, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"isOK\" status " +
//                "and the response should contain the right person with the same first name, last name and id " +
//                "but all other informations updated.")
//        public void putPersonWithAllUpdatesTest() throws Exception {
//            //GIVEN
//            String itemsToUpdate = "{\"address\":\"5 main street\",\"city\":\"city5\",\"zip\":5555,\"phoneNumber\":\"555-555-5555\",\"mail\":\"person5@mail.com\"}";
//            Person personToUpdate = new Person(1, "FirstName1", "LastName1", "1 main street", "city1", 1111, "111-111-1111", "person1@mail.com");
//            doReturn(Optional.of(personToUpdate)).when(personService).getPersonByName("FIRSTNAME1", "LASTNAME1");
//            doReturn(new Person()).when(personService).updatePerson(any(Person.class),any(Person.class));
//            //WHEN
//            mockMvc.perform(put("/person/{firstName}/{lastName}", "FirstName1", "LastName1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(itemsToUpdate))
//                    //THEN
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.id", is(1)))
//                    .andExpect(jsonPath("$.firstName", is("FirstName1")))
//                    .andExpect(jsonPath("$.lastName", is("LastName1")))
//                    .andExpect(jsonPath("$.city", is("city5")))
//                    .andExpect(jsonPath("$.address", is("5 main street")))
//                    .andExpect(jsonPath("$.zip", is(5555)))
//                    .andExpect(jsonPath("$.phoneNumber", is("555-555-5555")))
//                    .andExpect(jsonPath("$.mail", is("person5@mail.com")));
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with some updatable information in the request's body, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"isOK\" status " +
//                "and the response should contain the right person with the same not updated information" +
//                "but all other information updated.")
//        public void putPersonWithOnlyAddressAndMailUpdatesTest() throws Exception {
//            //GIVEN
//            String itemsToUpdate = "{\"address\":\"5 main street\",\"mail\":\"person5@mail.com\"}";
//            Person personToUpdate = new Person(1, "FirstName1", "LastName1", "1 main street", "city1", 1111, "111-111-1111", "person1@mail.com");
//            doReturn(Optional.of(personToUpdate)).when(personService).getPersonByName("FIRSTNAME1", "LASTNAME1");
//            doReturn(new Person()).when(personService).updatePerson(any(Person.class),any(Person.class));
//            //WHEN
//            mockMvc.perform(put("/person/{firstName}/{lastName}", "FirstName1", "LastName1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(itemsToUpdate))
//                    //THEN
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.id", is(1)))
//                    .andExpect(jsonPath("$.firstName", is("FirstName1")))
//                    .andExpect(jsonPath("$.lastName", is("LastName1")))
//                    .andExpect(jsonPath("$.city", is("city1")))
//                    .andExpect(jsonPath("$.address", is("5 main street")))
//                    .andExpect(jsonPath("$.zip", is(1111)))
//                    .andExpect(jsonPath("$.phoneNumber", is("111-111-1111")))
//                    .andExpect(jsonPath("$.mail", is("person5@mail.com")));
//        }
//
//        @Test
//        @DisplayName("GIVEN a person with one updatable information in the request's body, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"isOK\" status " +
//                "and the response should contain the right person with the same not updated information" +
//                "but phoneNumber updated.")
//        public void putPersonWithOnlyPhoneNumberUpdateTest() throws Exception {
//            //GIVEN
//            String itemsToUpdate = "{\"phoneNumber\":\"555-555-5555\"}";
//            Person personToUpdate = new Person(1, "FirstName1", "LastName1", "1 main street", "city1", 1111, "111-111-1111", "person1@mail.com");
//            doReturn(Optional.of(personToUpdate)).when(personService).getPersonByName("FIRSTNAME1", "LASTNAME1");
//            doReturn(new Person()).when(personService).updatePerson(any(Person.class),any(Person.class));
//            //WHEN
//            mockMvc.perform(put("/person/{firstName}/{lastName}", "FirstName1", "LastName1")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(itemsToUpdate))
//                    //THEN
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.id", is(1)))
//                    .andExpect(jsonPath("$.firstName", is("FirstName1")))
//                    .andExpect(jsonPath("$.lastName", is("LastName1")))
//                    .andExpect(jsonPath("$.city", is("city1")))
//                    .andExpect(jsonPath("$.address", is("1 main street")))
//                    .andExpect(jsonPath("$.zip", is(1111)))
//                    .andExpect(jsonPath("$.phoneNumber", is("555-555-5555")))
//                    .andExpect(jsonPath("$.mail", is("person1@mail.com")));
//        }
//
//        @Test
//        @DisplayName("GIVEN a person non-existing in the request's url, " +
//                "WHEN we call the uri  \"/person/{firstName}/{lastName}\", " +
//                "THEN we should have an \"notFound\" status " +
//                "and the response's body should be empty.")
//        public void putPersonNonExistingTest() throws Exception {
//            //GIVEN
//            String itemsToUpdate = "{\"address\":\"5 main street\",\"city\":\"city5\",\"zip\":5555,\"phoneNumber\":\"555-555-5555\",\"mail\":\"person5@mail.com\"}";
//            //WHEN
//            mockMvc.perform(put("/person/{firstName}/{lastName}", "FirstName5", "LastName5")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(itemsToUpdate))
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
//        public void putPersonWithNoBodyInformationsTest() throws Exception {
//            //GIVEN
//            //WHEN
//            mockMvc.perform(put("/person/{firstName}/{lastName}", "FirstName1", "LastName1")
//                            .contentType(MediaType.APPLICATION_JSON).content(""))
//                    //THEN
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//    }
//
//    @Nested
//    @Tag("DeletePersonTests")
//    @DisplayName("DELETE requests:")
//    class DeleteTests {
//
//        @Test
//        @DisplayName("GIVEN an existing person, " +
//                "WHEN we call the uri \"/person/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isEmpty\" status and an empty response.")
//        public void deletePersonByNameTest() throws Exception {
//            // GIVEN
//            Person person1 = new Person(1, "FirstName1", "LastName1", "1 main street", "CITY1", 1111, "111-111-1111", "person1@mail.com");
//            doReturn(Optional.of(person1)).when(personService).getPersonByName("FirstName1", "LastName1");
//            doNothing().when(personService).deletePersonByName(any(String.class),any(String.class));
//            // WHEN
//            mockMvc.perform(delete("/person/{firstName}/{lastName}", "FirstName1", "LastName1"))
//                    // THEN
//                    .andExpect(status().isNoContent())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//
//        @Test
//        @DisplayName("GIVEN a non-existing person, " +
//                "WHEN we call the uri \"/person/{firstName}/{lastName}\", " +
//                "THEN when should have an \"isNotFound\" status and an empty response.")
//        public void deletePersonByNameNonExistingTest() throws Exception {
//            // GIVEN
//            doReturn(Optional.empty()).when(personService).getPersonByName("FirstName1", "LastName1");
//            // WHEN
//            mockMvc.perform(delete("/person/{firstName}/{lastName}", "FirstName1", "LastName1"))
//                    // THEN
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$").doesNotExist());
//        }
//    }
//}
