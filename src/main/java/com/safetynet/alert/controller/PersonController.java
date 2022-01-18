package com.safetynet.alert.controller;

import com.safetynet.alert.model.DTO.PersonDTO;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;


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
     * @param id - A string composed by the person's first name and last name
     * @return a PersonDTO object corresponding to the person researched
     */
    @ApiOperation(value = "Get a person by its id.")
    @GetMapping("/person/{id}")
    @Transactional
    public ResponseEntity<PersonDTO> getPerson(@PathVariable String id) {
        log.debug("The function getPerson in PersonController is beginning.");
        PersonDTO personResearched = personService.getPersonDTOById(id);
        log.info("The person " + personResearched.getFirstName().toUpperCase() + " " + personResearched.getLastName().toUpperCase() + " has been found. ");
        log.debug("The function getPerson in PersonController is ending without any exception.\n");
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
                .path("/{id}")
                .buildAndExpand(newPerson.getId())
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
     * @param id     - A String composed by the person's first name and last name
     * @param person - The person object updated
     * @return the person which is updated or NULL if the person wasn't found
     */
    @ApiOperation(value = "Update a person by its id.")
    @PutMapping("/person/{id}")
    @Transactional
    public ResponseEntity<String> updatePerson(@PathVariable String id, @RequestBody PersonDTO person) {
        log.debug("The function updatePerson in PersonController is beginning.");
        Person result = personService.updatePerson(personService.getPersonDTOById(id), person);
        URI location = ServletUriComponentsBuilder
                .fromUri(URI.create("http://localhost:8080/person"))
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        String okUpdated = "The person " + result.getFirstName() + " " + result.getLastName() + " have been updated:\n" + location;
        log.debug("The function updatePersonByName in PersonController is ending without any exception.\n");
        return new ResponseEntity<>(okUpdated, httpHeaders, HttpStatus.OK);
    }

    /**
     * Delete - Delete a person
     *
     * @param id - A String which is composed by person's first name and last name
     */
    @ApiOperation(value = "Delete a person by its id.")
    @DeleteMapping("/person/{id}")
    @Transactional
    public ResponseEntity<String> deletePerson(@PathVariable String id) {
        log.debug("The function deletePerson in PersonController is beginning.");
        personService.deletePersonById(id);
        String message = "The person with id " + id + " has been deleted.";
        log.debug("The function deletePerson in PersonController is ending without any exception.\n");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}

