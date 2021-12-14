package com.safetynet.alert.controller;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.util.Map;


@RestController
@Slf4j
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * Read - Get all persons registered in database
     *
     * @return - An iterable object of persons fulfilled
     */
    @GetMapping("/person")
    public ResponseEntity<String> getAllPersons() {
        log.debug("The function getAllPersons in PersonController is beginning.");
        Iterable<Person> persons = personService.getPersons();
        String result = persons.toString();
        log.debug("The function getAllPersons in PersonController is ending without any exception.");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Read - Get one person from his first name and last name
     *
     * @param pathVariables - A map object of two Strings which are first name and last name of the researched person
     * @return the person object corresponding to the first name and last name or NULL if the person wasn't found
     */
    @GetMapping("/person/{firstName}/{lastName}")
    public ResponseEntity<String> getPersonByName(@PathVariable Map<String, String> pathVariables) {
        log.debug("The function getPersonByName in PersonController is beginning.");
        String firstName = pathVariables.get("firstName");
        String lastName = pathVariables.get("lastName");
        log.debug("firstName and lastName attributes have been got from url.");
        Person personResearched = personService.getPersonByName(firstName, lastName);
        String result = personResearched.toString();
        log.info("The person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " has been found. ");
        log.debug("The function getPersonByName in PersonController is ending without any exception.\n");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Create - Add a new person
     *
     * @param person: An object Person
     * @return The person object saved
     */
    @PostMapping("/person")
    public ResponseEntity<String> addNewPerson(@RequestBody Person person) {
        log.debug("The function addNewPerson in PersonController is beginning.");
        Person newPerson = personService.createPerson(person);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(newPerson.getFirstName(), newPerson.getLastName())
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        String okSaved = "The new person " + newPerson.getFirstName().toUpperCase() + " " + newPerson.getLastName().toUpperCase() + " have been created.";
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
    @PutMapping("/person/{firstName}/{lastName}")
    public ResponseEntity<String> updatePersonByName(@PathVariable Map<String, String> pathVariables, @RequestBody Person person) {
        log.debug("The function updatePersonByName in PersonController is beginning.");
        String firstName = pathVariables.get("firstName").toUpperCase();
        String lastName = pathVariables.get("lastName").toUpperCase();
        log.debug("Getting firstName and lastName attributes from url.");
        Person personToUpdate = personService.getPersonByName(firstName, lastName);
        String updatedMessage = personService.updatePerson(personToUpdate, person);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(firstName, lastName)
                .toUri();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(location);
        log.debug("The function addNewPerson in PersonController is ending without any exception.\n");
        return new ResponseEntity<>(updatedMessage, httpHeaders, HttpStatus.OK);
    }

    /**
     * Delete - Delete a person
     *
     * @param pathVariables - A map object of two Strings which are the first name and the last name of the person to delete
     */
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

