package com.safetynet.alert.unitTests;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.DTO.PersonDTO;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
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

@Tag("PersonTests")
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
public class PersonControllerTest {


    @MockBean
    private PersonService personService;

    @Autowired
    private MockMvc mockMvc;


    @Nested
    @Tag("PersonControllerTests")
    @DisplayName("GET requests:")
    class GetTests {

        @Test
        @DisplayName("GIVEN a non empty list of persons " +
                "WHEN we call the uri \"/person\", " +
                "THEN we should have an \"isOk\" status and the response's body should contain a JSon file with the persons and their information.")
        void getAllPersonsNonEmptyTest() throws Exception {
            // GIVEN
            //a non-empty list of persons
            PersonDTO person1 = new PersonDTO("FirstName1", "LastName1", "address test", "12345", "city test", "1234567890", "test@mail.com");
            PersonDTO person2 = new PersonDTO("FirstName2", "LastName2", "address test 2", "12345", "city test", "9876543210", "test2@mail.com");
            List<PersonDTO> personList = new ArrayList<>();
            personList.add(person1);
            personList.add(person2);
            doReturn(personList).when(personService).getPersonsDTO();
            // WHEN
            //we call the uri "/person"
            mockMvc.perform(get("/person"))
                    // THEN
                    //we should have an "isOk" status and the response's body should contain a JSon file with the persons and their information.
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].firstName", is("FirstName1")))
                    .andExpect(jsonPath("$[0].lastName", is("LastName1")))
                    .andExpect(jsonPath("$[1].firstName", is("FirstName2")))
                    .andExpect(jsonPath("$[1].lastName", is("LastName2")));
            verify(personService, Mockito.times(1)).getPersonsDTO();
        }

        @Test
        @DisplayName("GIVEN an empty list of persons " +
                "WHEN we call the uri \"/person\", " +
                "THEN we should have a \"notFound\" status and the response's body should contain a String with the expected error message.")
        public void getAllPersonsEmptyTest() throws Exception {
            //GIVEN
            // an empty list of persons
            EmptyObjectException emptyObjectException = new EmptyObjectException("error message");
            doThrow(emptyObjectException).when(personService).getPersonsDTO();
            //WHEN
            //we call the uri "/person"
            mockMvc.perform(get("/person"))
                    //THEN
                    //we should have a "notFound" status and the response's body should contain a String with the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(personService, Mockito.times(1)).getPersonsDTO();
        }


        @Test
        @DisplayName("GIVEN an existing person, " +
                "WHEN we call the uri \"/person/{id}\", " +
                "THEN we should have an \"isOk\" status and the person with all correct attributes in the response.")
        public void getPersonExistingTest() throws Exception {
            // GIVEN
            //an existing person
            PersonDTO person1 = new PersonDTO("FirstName1", "LastName1", "address test", "12345", "city test", "1234567890", "test@mail.com");
            doReturn(person1).when(personService).getPersonDTOById("idTest");
            // WHEN
            //we call the uri "/person/{id}"
            mockMvc.perform(get("/person/{id}", "idTest"))
                    // THEN
                    //we should have an "isOk" status and the person with all correct attributes in the response
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.firstName", is("FirstName1")))
                    .andExpect(jsonPath("$.lastName", is("LastName1")))
                    .andExpect(jsonPath("$.address", is("address test")))
                    .andExpect(jsonPath("$.city", is("city test")))
                    .andExpect(jsonPath("$.zip", is("12345")))
                    .andExpect(jsonPath("$.phoneNumber", is("1234567890")))
                    .andExpect(jsonPath("$.mail", is("test@mail.com")));
            verify(personService, Mockito.times(1)).getPersonDTOById("idTest");
        }

        @DisplayName("GIVEN a non existing person, " +
                "WHEN we call the uri \"/person/{id}\", " +
                "THEN when should have an \"isNotFound\" status and the response's body should contain a String with the expected error message.")
        @Test
        public void getPersonNonExistingTest() throws Exception {
            //GIVEN
            //a non-existing person
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(personService).getPersonDTOById("idTest");
            //WHEN
            //we call the uri "/person/{id}",
            mockMvc.perform(get("/person/{id}", "idTest"))
                    //THEN
                    //the response's body should contain a String with the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(personService, Mockito.times(1)).getPersonDTOById("idTest");
        }
    }

    @Nested
    @Tag("PersonControllerTests")
    @DisplayName("POST requests:")
    class PostTests {

        @Test
        @DisplayName("GIVEN a person with all information in the request's body, " +
                "WHEN we call the uri \"/person\", " +
                "THEN we should have an \"isCreated\" status with the expected information message and the header should return the right url to find the person created.")
        public void addNewPersonAllInformationTest() throws Exception {
            // GIVEN
            //a person with all information in the request's body
            String personToPost = "{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"address\":\"address test\"," +
                    "\"city\":\"city test\",\"zip\":\"12345\",\"phoneNumber\":\"1234567890\",\"mail\":\"test@mail.com\"}";
            Person personToReturn = new Person("FirstName1", "LastName1");
            personToReturn.setId("idTest");
            doReturn(personToReturn).when(personService).createPerson(any(PersonDTO.class));
            // WHEN
            //we call the uri "/person"
            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(personToPost))
                    // THEN
                    //we should have an "isCreated" status with the expected information message and the header should return the right url to find the person created
                    .andExpect(status().isCreated())
                    .andExpect(content().string("The new person FirstName1 LastName1 have been created:\nhttp://localhost/person/idTest"))
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/person/idTest"));
            verify(personService, Mockito.times(1)).createPerson(any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN a person to post with only required information (firstName and LastName), " +
                "WHEN we call the uri \"/person\", " +
                "THEN we should have an \"isCreated\" status with the expected information message and the header should return the right url to find the person created.")
        public void addNewPersonWithOnlyFirstNameAndLastNameTest() throws Exception {
            // GIVEN
            //a person to post with only required information (firstName and LastName)
            String personToPost = "{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\"}";
            Person personToReturn = new Person("FirstName1", "LastName1");
            personToReturn.setId("idTest");
            doReturn(personToReturn).when(personService).createPerson(any(PersonDTO.class));
            // WHEN
            //we call the uri "/person"
            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(personToPost))
                    // THEN
                    //we should have an "isCreated" status with the expected information message and the header should return the right url to find the person created
                    .andExpect(status().isCreated())
                    .andExpect(content().string("The new person FirstName1 LastName1 have been created:\nhttp://localhost/person/idTest"))
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/person/idTest"));
            verify(personService, Mockito.times(1)).createPerson(any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN a NotTheRightFormatToPostException thrown by the personService, " +
                "WHEN we call the uri \"/person\", " +
                "THEN we should have a \"badRequest\" status the response should contain the expected error message.")
        public void addNewPersonNotRightFormatToPostExceptionTest() throws Exception {
            // GIVEN
            //a NotTheRightFormatToPostException thrown by the personService
            String personToPost = "{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\"}";
            NotRightFormatToPostException notRightFormatToPostException = new NotRightFormatToPostException("error message");
            doThrow(notRightFormatToPostException).when(personService).createPerson(any(PersonDTO.class));
            // WHEN
            //we call the uri "/person"
            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(personToPost))
                    // THEN
                    //we should have a "badRequest" status the response should contain the expected error message.
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(personService, Mockito.times(1)).createPerson(any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN an ObjectAlreadyExistingExceptionException thrown by the personService, " +
                "WHEN we call the uri \"/person\", " +
                "THEN we should have a \"badRequest\" status the response should contain the expected error message.")
        public void addNewPersonObjectAlreadyExistingExceptionTest() throws Exception {
            // GIVEN
            //an ObjectAlreadyExistingException thrown by the personService
            String personToPost = "{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\"}";
            ObjectAlreadyExistingException objectAlreadyExistingException = new ObjectAlreadyExistingException("error message");
            doThrow(objectAlreadyExistingException).when(personService).createPerson(any(PersonDTO.class));
            // WHEN
            //we call the uri "/person"
            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(personToPost))
                    // THEN
                    //we should have a "badRequest" status the response should contain the expected error message.
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(personService, Mockito.times(1)).createPerson(any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN a request without body, " +
                "WHEN we call the uri \"/person\", " +
                "THEN we should have a \"badRequest\" status and the response should contain the expected error message.")
        public void addNewPersonWithoutBodyTest() throws Exception {
            //GIVEN
            //a request without body
            Person personToReturn = new Person("FirstName1", "LastName1");
            personToReturn.setId("idTest");
            doReturn(personToReturn).when(personService).createPerson(any(PersonDTO.class));
            //WHEN
            //we call the uri "/person"
            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    //THEN
                    //we should have a "badRequest" status and the response should contain the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct: please verify the request's body.\n"));
            verify(personService, Mockito.times(0)).createPerson(any(PersonDTO.class));
        }
    }

    @Nested
    @Tag("PutPersonTests")
    @DisplayName("PUT requests:")
    class PutTests {

        @Test
        @DisplayName("GIVEN a person with all updatable information in the request's body, " +
                "WHEN we call the uri  \"/person/{id}, " +
                "THEN we should have an \"isOK\" status and the expected information message.")
        public void updatePersonWithAllInformationTest() throws Exception {
            //GIVEN
            //a person with all updatable information in the request's body
            String itemsToUpdate = "{\"address\":\"address test\",\"city\":\"citytest\",\"zip\":\"12345\",\"phoneNumber\":\"1234567890\",\"mail\":\"test@mail.com\"}";
            Person personToReturn = new Person("FirstName1", "LastName1");
            PersonDTO personDTO = new PersonDTO();
            personToReturn.setId("idTest");
            doReturn(personDTO).when(personService).getPersonDTOById("idTest");
            doReturn(personToReturn).when(personService).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
            //WHEN
            //we call the uri  "/person/{id}"
            mockMvc.perform(put("/person/{id}", "idTest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have an "isOK" status and the expected information message
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost:8080/person/idTest"))
                    .andExpect(content().string("The person FirstName1 LastName1 have been updated:\nhttp://localhost:8080/person/idTest"));
            verify(personService, Mockito.times(1)).getPersonDTOById("idTest");
            verify(personService, Mockito.times(1)).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN a person with only some updatable information in the request's body, " +
                "WHEN we call the uri  \"/person/{id}\", " +
                "THEN we should have an \"isOK\" status and the expected information message.")
        public void updatePersonWithSomeInformationTest() throws Exception {
            //GIVEN
            //a person with only some updatable information in the request's body
            String itemsToUpdate = "{\"phoneNumber\":\"1234567890\",\"mail\":\"test@mail.com\"}";
            Person personToReturn = new Person("FirstName1", "LastName1");
            PersonDTO personDTO = new PersonDTO();
            personToReturn.setId("idTest");
            doReturn(personDTO).when(personService).getPersonDTOById("idTest");
            doReturn(personToReturn).when(personService).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
            //WHEN
            //we call the uri  "/person/{id}"
            mockMvc.perform(put("/person/{id}", "idTest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have an "isOK" status and the expected information message
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost:8080/person/idTest"))
                    .andExpect(content().string("The person FirstName1 LastName1 have been updated:\nhttp://localhost:8080/person/idTest"));
            verify(personService, Mockito.times(1)).getPersonDTOById("idTest");
            verify(personService, Mockito.times(1)).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN a person non-existing in the request's url, " +
                "WHEN we call the uri  \"/person/{id}\", " +
                "THEN we should have an \"notFound\" status and the expected error message.")
        public void updatePersonNonExistingTest() throws Exception {
            //GIVEN
            //a person non-existing in the request's url
            String itemsToUpdate = "{\"address\":\"address test\",\"city\":\"citytest\",\"zip\":\"12345\",\"phoneNumber\":\"1234567890\",\"mail\":\"test@mail.com\"}";
            Person personToReturn = new Person("FirstName1", "LastName1");
            personToReturn.setId("idTest");
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(personService).getPersonDTOById("idTest");
            doReturn(personToReturn).when(personService).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
            //WHEN
            //we call the uri  "/person/{id}"
            mockMvc.perform(put("/person/{id}", "idTest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have an "notFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(personService, Mockito.times(1)).getPersonDTOById("idTest");
            verify(personService, Mockito.times(0)).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN a NotTheSamePersonException returned by the personService, " +
                "WHEN we call the uri  \"/person/{id}\", " +
                "THEN we should have an \"isConflict\" status and the expected error message.")
        public void updatePersonNotTheSamePersonTest() throws Exception {
            //GIVEN
            //a NotTheSamePersonException returned by the personService
            String itemsToUpdate = "{\"address\":\"address test\",\"city\":\"citytest\",\"zip\":\"12345\",\"phoneNumber\":\"1234567890\",\"mail\":\"test@mail.com\"}";
            Person personToReturn = new Person("FirstName1", "LastName1");
            personToReturn.setId("idTest");
            NotTheSamePersonException notTheSamePersonException = new NotTheSamePersonException("error message");
            doThrow(notTheSamePersonException).when(personService).getPersonDTOById("idTest");
            doReturn(personToReturn).when(personService).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
            //WHEN
            //we call the uri  "/person/{id}"
            mockMvc.perform(put("/person/{id}", "idTest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have an "isConflict" status and the expected error message
                    .andExpect(status().isConflict())
                    .andExpect(content().string("error message"));
            verify(personService, Mockito.times(1)).getPersonDTOById("idTest");
            verify(personService, Mockito.times(0)).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN a NothingToUpdateException returned by the personService, " +
                "WHEN we call the uri  \"/person/{id}\", " +
                "THEN we should have an \"isBadRequest\" status and the expected error message.")
        public void updatePersonNothingToUpdateTest() throws Exception {
            //GIVEN
            //a NothingToUpdateException returned by the personService
            String itemsToUpdate = "{\"address\":\"address test\",\"city\":\"citytest\",\"zip\":\"12345\",\"phoneNumber\":\"1234567890\",\"mail\":\"test@mail.com\"}";
            Person personToReturn = new Person("FirstName1", "LastName1");
            personToReturn.setId("idTest");
            NothingToUpdateException nothingToUpdateException = new NothingToUpdateException("error message");
            doThrow(nothingToUpdateException).when(personService).getPersonDTOById("idTest");
            doReturn(personToReturn).when(personService).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
            //WHEN
            //we call the uri  "/person/{id}"
            mockMvc.perform(put("/person/{id}", "idTest")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have an "isBadRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("error message"));
            verify(personService, Mockito.times(1)).getPersonDTOById("idTest");
            verify(personService, Mockito.times(0)).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN no id in the request's url, " +
                "WHEN we call the uri  \"/person/{id}\", " +
                "THEN we should have a \"badRequest\" status and the expected error message.")
        public void updatePersonNoUrlTest() throws Exception {
            //GIVEN
            //no id in the request's url
            String itemsToUpdate = "{\"address\":\"address test\",\"city\":\"citytest\",\"zip\":\"12345\",\"phoneNumber\":\"1234567890\",\"mail\":\"test@mail.com\"}";

            //WHEN
            //we call the uri  "/person" without id
            mockMvc.perform(put("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(itemsToUpdate))
                    //THEN
                    //we should have a "badRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
            verify(personService, Mockito.times(0)).getPersonDTOById("idTest");
            verify(personService, Mockito.times(0)).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
        }

        @Test
        @DisplayName("GIVEN a request without body, " +
                "WHEN we call the uri  \"/person/{id}\", " +
                "THEN we should have an \"badRequest\" status and the expected error message.")
        public void updatePersonNoBodyTest() throws Exception {
            //GIVEN
            //a request without body
            //WHEN
            //we call the uri  "/person/{id}"
            mockMvc.perform(put("/person/{id}", "idTest")
                            .contentType(MediaType.APPLICATION_JSON).content(""))
                    //THEN
                    //we should have an "badRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct: please verify the request's body.\n"));
            verify(personService, Mockito.times(0)).getPersonDTOById("idTest");
            verify(personService, Mockito.times(0)).updatePerson(any(PersonDTO.class), any(PersonDTO.class));
        }
    }

    @Nested
    @Tag("DeletePersonTests")
    @DisplayName("DELETE requests:")
    class DeleteTests {

        @Test
        @DisplayName("GIVEN an existing person, " +
                "WHEN we call the uri \"/person/{id}\", " +
                "THEN when should have an \"isOk\" status and the expected information message.")
        public void deletePersonExistingTest() throws Exception {
            // GIVEN
            doNothing().when(personService).deletePersonById("idTest");
            // WHEN
            mockMvc.perform(delete("/person/{id}", "idTest"))
                    // THEN
                    .andExpect(status().isOk())
                    .andExpect(content().string("The person with id idTest has been deleted."));
            verify(personService, Mockito.times(1)).deletePersonById("idTest");
        }

        @Test
        @DisplayName("GIVEN a non-existing person, " +
                "WHEN we call the uri \"/person/{id}\", " +
                "THEN when should have an \"isNotFound\" status and the expected error message.")
        public void deletePersonNonExistingTest() throws Exception {
            // GIVEN
            //a non-existing person
            ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("error message");
            doThrow(objectNotFoundException).when(personService).deletePersonById("idTest");
            // WHEN
            // we call the uri "/person/{id}"
            mockMvc.perform(delete("/person/{id}", "idTest"))
                    // THEN
                    // when should have an "isNotFound" status and the expected error message
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("error message"));
            verify(personService, Mockito.times(1)).deletePersonById("idTest");
        }

        @Test
        @DisplayName("GIVEN no id in the request's url, " +
                "WHEN we call the uri  \"/person/{id}\", " +
                "THEN we should have a \"badRequest\" status and the expected error message.")
        public void updatePersonNoUrlTest() throws Exception {
            //GIVEN
            //no id in the request's url

            //WHEN
            //we call the uri  "/person" without id
            mockMvc.perform(delete("/person"))
                    //THEN
                    //we should have a "badRequest" status and the expected error message
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("The request is not correct : please verify the request's url.\n"));
            verify(personService, Mockito.times(0)).deletePersonById("idTest");
        }
    }
}
