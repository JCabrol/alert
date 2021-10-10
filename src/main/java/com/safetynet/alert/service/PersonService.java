package com.safetynet.alert.service;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Getter
@Setter

@Service
@Slf4j
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    /**
     * Read - Get one person from his id
     *
     * @param  id - an int which is the primary key of person object
     * @return an optional person object corresponding to the id researched if it's found
     */
    public Optional<Person> getPersonById(final int id) {
        log.debug("The function getPersonById in PersonService is beginning.");
        Optional<Person> personFound = personRepository.findById(id);
        log.debug("The function getPersonById in PersonService is ending.");
        return personFound;
    }

    /**
     * Read - Get all the persons presents in data
     *
     *
     * @return an iterable containing all the persons presents in data
     */
    public Iterable<Person> getPersons() {
        log.debug("The function getPersons in PersonService is beginning.");
        Iterable<Person> allPersons = personRepository.findAll();
        log.debug("The function getPersons in PersonService is ending.");
        return allPersons;
    }

    /**
     * Read - delete one person from his id
     *
     * @param id - an id which is the primary key of the researched person
     *
     */
    public void deletePerson(final int id) {
        log.debug("The function deletePerson in PersonService is beginning.");
        personRepository.deleteById(id);
        log.debug("The function deletePerson in PersonService is ending.");
    }

    /**
     * Read - Save a person object in data
     *
     * @param person - A person object which has to be saved in data
     * @return the person object which was saved
     */
    public Person savePerson(Person person) {
        log.debug("The function savePerson in PersonService is beginning.");
        person.setFirstName(person.getFirstName().toUpperCase());
        person.setLastName(person.getLastName().toUpperCase());
        if(person.getCity()!=null){
        person.setCity(person.getCity().toUpperCase());}
        Person savedPerson = personRepository.save(person);
        log.debug("The function savePerson in PersonService is ending.");
        return savedPerson;
    }

    /**
     * Read - Get one person from his first name and last name
     *
     * @param firstName - A String which is the first name of the researched person
     * @param lastName - A String which is the last name of the researched person
     * @return an optional person object which is the person researched if it's found
     */
    public Optional<Person> getPersonByName(String firstName, String lastName) {
        log.debug("The function getPersonByName in PersonService is beginning.");
        String upperCaseFirstName = firstName.toUpperCase();
        String upperCaseLastName = lastName.toUpperCase();
        Optional<Person> personFound = personRepository.findByFirstNameAndLastName(upperCaseFirstName, upperCaseLastName);
        log.debug("The function getPersonByName in PersonService is ending.");
        return personFound;
    }
}
