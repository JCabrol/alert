package com.safetynet.alert.controller;

import com.safetynet.alert.model.DTO.PersonDTO;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@Api("CRUD operations about persons.")
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * Read - Get all persons registered in database
     *
     * @return - A list of all the persons
     */
    @ApiOperation(value = "Get all the persons.")
    @GetMapping("/person")
    @Transactional
    public ResponseEntity<List<PersonDTO>> getAllPersons() {
        log.debug("The function getAllPersons in PersonController is beginning.");
        List<PersonDTO> persons = personService.getPersonsDTO();
        log.debug("The function getAllPersons in PersonController is ending without any exception.");
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    /**
     * Read - Get one person from his first name and last name
     *
     * @param pathVariables - A map object of two Strings which are first name and last name of the researched person
     * @return a PersonDTO object corresponding to the person researched
     */
    @ApiOperation(value = "Get a person by its first name and last name.")
    @GetMapping("/person/{firstName}/{lastName}")
    @Transactional
    public ResponseEntity<PersonDTO> getPersonByName(@PathVariable Map<String, String> pathVariables) {
        log.debug("The function getPersonByName in PersonController is beginning.");
        String firstName = pathVariables.get("firstName");
        String lastName = pathVariables.get("lastName");
        log.debug("firstName and lastName attributes have been got from url.");
        PersonDTO personResearched = personService.getPersonDTOByName(firstName, lastName);
        log.info("The person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " has been found. ");
        log.debug("The function getPersonByName in PersonController is ending without any exception.\n");
        return new ResponseEntity<>(personResearched, HttpStatus.OK);
    }

    /**
     * Create - Add a new person
     *
     * @param person: A PersonDTO object containing information to create person
     * @return a String indicating the person created
     */
    @ApiOperation(value = "Add a new person.")
    @PostMapping("/person")
    @Transactional
    public ResponseEntity<String> addNewPerson(@RequestBody PersonDTO person) {
        log.debug("The function addNewPerson in PersonController is beginning.");
        Person newPerson = personService.createPerson(person);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(newPerson.getFirstName(), newPerson.getLastName())
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        String okSaved = "The new person " + newPerson.getFirstName() + " " + newPerson.getLastName() + " have been created:\n" + location;
        log.debug("The function addNewPerson in PersonController is ending without any exception.\n");
        return new ResponseEntity<>(okSaved, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * Update - Update an existing person
     *
     * @param pathVariables - A map object of two Strings which are the first name and the last name of the person to update
     * @param person        - The person object updated
     * @return the person which is updated or NULL if the person wasn't found
     */
    @ApiOperation(value = "Update a person.")
    @PutMapping("/person/{firstName}/{lastName}")
    @Transactional
    public ResponseEntity<String> updatePersonByName(@PathVariable Map<String, String> pathVariables, @RequestBody PersonDTO person) {
        log.debug("The function updatePersonByName in PersonController is beginning.");
        String firstName = pathVariables.get("firstName").toUpperCase();
        String lastName = pathVariables.get("lastName").toUpperCase();
        log.debug("Getting firstName and lastName attributes from url.");
        personService.updatePerson(personService.getPersonDTOByName(firstName, lastName), person);
        URI location = ServletUriComponentsBuilder
                .fromUri(URI.create("http://localhost:8080/person"))
                .path("/{firstName}/{lastName}")
                .buildAndExpand(firstName, lastName)
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        String okUpdated = "The person " + firstName + " " + lastName + " have been updated:\n" + location;
        log.debug("The function updatePersonByName in PersonController is ending without any exception.\n");
        try{
        return new ResponseEntity<>(okUpdated, httpHeaders, HttpStatus.OK);}catch(ConstraintViolationException e){return new ResponseEntity<>("In the exception", HttpStatus.OK);}
    }

    /**
     * Delete - Delete a person
     *
     * @param pathVariables - A map object of two Strings which are the first name and the last name of the person to delete
     */
    @ApiOperation(value = "Delete a person by its first name and last name.")
    @DeleteMapping("/person/{firstName}/{lastName}")
    public ResponseEntity<String> deletePersonByName(@PathVariable Map<String, String> pathVariables) {
        log.debug("The function deletePersonByName in PersonController is beginning.");
        String firstName = pathVariables.get("firstName");
        String lastName = pathVariables.get("lastName");
        log.debug("Getting firstName and lastName attributes from url.");
        personService.deletePersonByName(firstName, lastName);
        String message = "The person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " has been deleted.";
        log.debug("The function deletePersonByName in PersonController is ending without any exception.\n");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}

