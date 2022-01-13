package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.EmptyPersonsException;
import com.safetynet.alert.exceptions.NotRightFormatToPostException;
import com.safetynet.alert.exceptions.PersonAlreadyExistingException;
import com.safetynet.alert.exceptions.PersonNotFoundException;
import com.safetynet.alert.model.DTO.PersonDTO;
import com.safetynet.alert.model.Person;

import java.util.List;


public interface PersonService {

    /**
     * Get all the persons presents in data in PersonDTO object format
     *
     * @return a list containing all the persons presents in data
     */
    List<PersonDTO> getPersonsDTO();

    /**
     * Get all the persons presents in data
     *
     * @return a list containing all the persons presents in data
     * @throws EmptyPersonsException When the list is empty
     */
    List<Person> getPersons() throws EmptyPersonsException;

    /**
     * Get one person from his first name and last name
     *
     * @param firstName A String which is the first name of the researched person
     * @param lastName  A String which is the last name of the researched person
     * @return a PersonDTO object which corresponds to researched person
     * @throws PersonNotFoundException When the researched person is not found
     */
    PersonDTO getPersonDTOByName(String firstName, String lastName) throws PersonNotFoundException;

    /**
     * Save a person object in data
     *
     * @param person A PersonDTO object which has to be saved
     * @return the person object which was saved
     * @throws PersonAlreadyExistingException When the person to create already exists
     * @throws NotRightFormatToPostException  When the first name or the last name is missing
     */
    Person createPerson(PersonDTO person) throws PersonAlreadyExistingException, NotRightFormatToPostException;


    /**
     * Save a person object in data
     *
     * @param personToUpdate - A person object which has to be saved in data
     */
    Person updatePerson(PersonDTO personToUpdate, PersonDTO personWithNewInformation);

    /**
     * Delete one person from his id
     *
     * @param firstName - an int which is the primary key of the researched person
     */
    void deletePersonByName(String firstName, String lastName);

    /**
     * Get one person from his first name and last name
     *
     * @param firstName A String which is the first name of the researched person
     * @param lastName  A String which is the last name of the researched person
     * @return a Person object which corresponds to researched person
     * @throws PersonNotFoundException When the researched person is not found
     */
    Person getPersonByName(String firstName, String lastName) throws PersonNotFoundException;
}
