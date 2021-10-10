package com.safetynet.alert.controller;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;


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
    public ResponseEntity<Iterable<Person>> getAllPersons() {
        log.debug("The function getAllPersons in PersonController is beginning.");
        Iterable<Person> persons = personService.getPersons();

        if (persons.iterator().hasNext()) {
            log.debug("The function getAllPersons in PersonController is ending.\n");
            return ResponseEntity.ok(persons);
        } else {
            log.debug("There are no persons registered in data base.\n");
            log.debug("The function getAllPersons in PersonController is ending.\n");
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Read - Get one person from his first name and last name
     *
     * @param pathVariables - A map object of two Strings which are first name and last name of the researched person
     * @return the person object corresponding to the first name and last name or NULL if the person wasn't found
     */
    @GetMapping("/person/{firstName}/{lastName}")
    public ResponseEntity<Person> getPersonByName(@PathVariable Map<String, String> pathVariables) {
        log.debug("The function getPersonByName in PersonController is beginning.");
        String firstName = pathVariables.get("firstName");
        String lastName = pathVariables.get("lastName");
        log.debug("Getting firstName and lastName attributes from url.");
        Optional<Person> personResearched = personService.getPersonByName(firstName, lastName);
        if (personResearched.isPresent()) {
            log.info("The person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " has been found.");
            log.debug("The function getPersonByName in PersonController is ending.\n");
            return ResponseEntity.ok(personResearched.get());
        } else {
            log.error("The person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " was not found.");
            log.debug("The function getPersonByName in PersonController is ending.\n");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create - Add a new person
     *
     * @param person: An object Person
     * @return The person object saved
     */
    @PostMapping("/person")
    public ResponseEntity<Void> addNewPerson(@RequestBody Person person) {
        log.debug("The function addNewPerson in PersonController is beginning.");
        try {
            if (personService.getPersonByName(person.getFirstName().toUpperCase(), person.getLastName().toUpperCase()).isPresent()) {
                log.error("A person named " + person.getFirstName().toUpperCase() + " " + person.getLastName().toUpperCase() + " already exists, so it cannot be created.");
                log.debug("The function addNewPerson in PersonController is ending.\n");
                return ResponseEntity.badRequest().build();
            } else {
                Person newPerson = personService.savePerson(person);

                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{firstName}/{lastName}")
                        .buildAndExpand(newPerson.getFirstName(), newPerson.getLastName())
                        .toUri();

                log.info("The new person " + newPerson.getFirstName().toUpperCase() + " " + newPerson.getLastName().toUpperCase() + " have been added in database.");
                log.debug("The function addNewPerson in PersonController is ending.\n");
                return ResponseEntity.created(location).build();
            }
        } catch (NullPointerException e) {
            log.error("Something went wrong while trying to add a new person, no new person was added: " + e.getMessage());
            log.debug("The function addNewPerson in PersonController is ending.\n");
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete - Delete a person
     *
     * @param pathVariables - A map object of two Strings which are the first name and the last name of the person to delete
     */
    @DeleteMapping("/person/{firstName}/{lastName}")
    public ResponseEntity<Void> deletePersonByName(@PathVariable Map<String, String> pathVariables) {
        log.debug("The function deletePersonByName in PersonController is beginning.");
        String firstName = pathVariables.get("firstName");
        String lastName = pathVariables.get("lastName");
        log.debug("Getting firstName and lastName attributes from url.");
        Optional<Person> person = personService.getPersonByName(firstName, lastName);
        if (person.isPresent()) {
            log.debug("The person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " has been found.");
            int id = person.get().getId();
            personService.deletePerson(id);
            log.info("The person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " has been deleted.");
            log.debug("The function deletePersonByName in PersonController is ending.\n");
            return ResponseEntity.noContent().build();
        } else {
            log.error("The person " + firstName.toUpperCase() + " " + lastName.toUpperCase() + " was not found, so it couldn't have been deleted.\n");
            log.debug("The function deletePersonByName in PersonController is ending.\n");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update - Update an existing person
     *
     * @param pathVariables - A map object of two Strings which are the first name and the last name of the person to update
     * @param person        - The person object updated
     * @return the person which is updated or NULL if the person wasn't found
     */
    @PutMapping("/person/{firstName}/{lastName}")
    public ResponseEntity<Person> updatePersonByName(@PathVariable Map<String, String> pathVariables, @RequestBody Person person) {
        log.debug("The function updatePersonByName in PersonController is beginning.");
        String firstName = pathVariables.get("firstName").toUpperCase();
        String lastName = pathVariables.get("lastName").toUpperCase();
        log.debug("Getting firstName and lastName attributes from url.");
        Optional<Person> personToUpdate = personService.getPersonByName(firstName, lastName);
        if (personToUpdate.isPresent()) {
            log.debug("The person " + firstName + " " + lastName + " have been found.");
            String address = person.getAddress();
            if (address != null) {
                personToUpdate.get().setAddress(address);
                log.info(firstName + " " + lastName + "'s address has been updated.");
            }
            String city = person.getCity();
            if (city != null) {
                personToUpdate.get().setCity(city);
                log.info(firstName + " " + lastName + "'s city has been updated.");
            }
            String mail = person.getMail();
            if (mail != null) {
                personToUpdate.get().setMail(mail);
                log.info(firstName + " " + lastName + "'s mail has been updated.");
            }
            String phoneNumber = person.getPhoneNumber();
            if (phoneNumber != null) {
                personToUpdate.get().setPhoneNumber(phoneNumber);
                log.info(firstName + " " + lastName + "'s phone number has been updated.");
            }
            int zip = person.getZip();
            if (zip != 0) {
                personToUpdate.get().setZip(zip);
                log.info(firstName + " " + lastName + "'s zip has been updated.");
            }
            personService.savePerson(personToUpdate.get());
            log.info("All updated informations have been saved.");
            log.debug("The function updatePersonByName in PersonController is ending.\n");
            return ResponseEntity.ok(personToUpdate.get());
        } else {
            log.error("The person " + firstName + " " + lastName + " wasn't found, so no updates have been done.");
            log.debug("The function updatePersonByName in PersonController is ending.\n");
            return ResponseEntity.notFound().build();
        }
    }
}
