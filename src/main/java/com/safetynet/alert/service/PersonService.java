package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.EmptyObjectException;
import com.safetynet.alert.exceptions.NotRightFormatToPostException;
import com.safetynet.alert.exceptions.ObjectAlreadyExistingException;
import com.safetynet.alert.exceptions.ObjectNotFoundException;
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
     * @throws EmptyObjectException When the list is empty
     */
    List<Person> getPersons() throws EmptyObjectException;

    /**
     * Get one person from his id
     *
     * @param id A String which is composed by the person's first name and last name
     * @return a Person object which corresponds to researched person
     * @throws ObjectNotFoundException When the researched person is not found
     */
    Person getPersonById(String id) throws ObjectNotFoundException;

    /**
     * Get one person from his first name and last name
     *
     * @param id A String which is composed by person's first name and last name
     * @return a PersonDTO object which corresponds to researched person
     * @throws ObjectNotFoundException When the researched person is not found
     */
    PersonDTO getPersonDTOById(String id) throws ObjectNotFoundException;

    /**
     * Save a person object in data
     *
     * @param person A PersonDTO object which has to be saved
     * @return the person object which was saved
     * @throws ObjectAlreadyExistingException When the person to create already exists
     * @throws NotRightFormatToPostException  When the first name or the last name is missing
     */
    Person createPerson(PersonDTO person) throws ObjectAlreadyExistingException, NotRightFormatToPostException;


    /**
     * Save a person object in data
     *
     * @param personToUpdate - A person object which has to be saved in data
     */
    Person updatePerson(PersonDTO personToUpdate, PersonDTO personWithNewInformation);

    /**
     * Delete one person from his id
     *
     * @param id - a String which is composed by the person's first name and last name
     */
    void deletePersonById(String id);

    /**
     * Get one person from his first name and last name
     *
     * @param firstName A String which is the first name of the researched person
     * @param lastName  A String which is the last name of the researched person
     * @return a Person object which corresponds to researched person
     * @throws ObjectNotFoundException When the researched person is not found
     */
    List<Person> getPersonsByName(String firstName, String lastName) throws ObjectNotFoundException;


}
