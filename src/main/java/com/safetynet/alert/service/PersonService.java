package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import lombok.Getter;
import lombok.Setter;
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
     * @param id - an int which is the primary key of person object
     * @return an optional person object corresponding to the id researched if it's found
     */
    public Person getPersonById(int id) throws PersonNotFoundException {
        log.debug("The function getPersonById in PersonService is beginning.");
        if (personRepository.findById(id).isPresent()) {
            Person personFound = personRepository.findById(id).get();
            log.debug("The function getPersonById in PersonService is ending, a person have been found.");
            return personFound;
        } else {
            log.debug("The function getPersonById in PersonService is ending, no person was found.");
            throw new PersonNotFoundException("The person with Id number " + id + " was not found.\n");
        }
    }

    /**
     * Read - Get all the persons presents in data
     *
     * @return an iterable containing all the persons presents in data
     */
    public Iterable<Person> getPersons() throws EmptyPersonsException {
        log.debug("The function getPersons in PersonService is beginning.");
        Iterable<Person> allPersons = personRepository.findAll();
        if (allPersons.iterator().hasNext()) {
            log.debug("The function getPersons in PersonService is ending. Some persons were found.");
            return allPersons;
        } else {
            log.debug("The function getPersons in PersonService is ending without founding any persons.");
            throw new EmptyPersonsException("There are no persons registered.\n");
        }
    }


    /**
     * Delete one person from his id
     *
     * @param firstName - an int which is the primary key of the researched person
     */
    public void deletePersonByName(String firstName, String lastName) throws PersonNotFoundException {
        log.debug("The function deletePersonByName in PersonService is beginning.");
        String upperCaseFirstName = firstName.toUpperCase();
        String upperCaseLastName = lastName.toUpperCase();
        Optional<Person> person = personRepository.findByFirstNameAndLastName(upperCaseFirstName, upperCaseLastName);
        if (person.isPresent()) {
            int id = person.get().getId();
            personRepository.deleteById(id);
            log.info("The person " + upperCaseFirstName + " " + upperCaseLastName + "have been deleted. \n");
            log.debug("The function deletePersonByName in PersonService is ending, a person have been deleted.");
        } else {
            throw new PersonNotFoundException("The person " + upperCaseFirstName + " " + upperCaseLastName + "was not found, so it cannot have been deleted.\n");
        }
    }

    /**
     * Save a person object in data
     *
     * @param person - A person object which has to be saved in data
     * @return the person object which was saved
     */
    public Person createPerson(Person person) throws PersonAlreadyExistingException, NotRightFormatToPostException {
        log.debug("The function createPerson in PersonService is beginning.");
        String upperCaseFirstName;
        String upperCaseLastName;
        try {
            upperCaseFirstName = person.getFirstName().toUpperCase();
            upperCaseLastName = person.getLastName().toUpperCase();
        } catch (NullPointerException e) {
            log.debug("The function createPerson in PersonService is ending without creating anybody.");
            throw new NotRightFormatToPostException("There is something missing in the request :\nto post a new person there should be at least a \"firstName\" and a \"lastName\" fields.\n");
        }
        if (!existPersonByName(upperCaseFirstName, upperCaseLastName)) {
            person.setFirstName(upperCaseFirstName);
            person.setLastName(upperCaseLastName);
            if (person.getCity() != null) {
                person.setCity(person.getCity().toUpperCase());
            }
            Person createdPerson = personRepository.save(person);
            log.info("The person " + upperCaseFirstName + " " + upperCaseLastName + " have been created.");
            log.debug("The function updatePerson in PersonService is ending with creating a person.");
            return createdPerson;
        } else {
            log.debug("The function createPerson in PersonService is ending without creating anybody.");
            throw new PersonAlreadyExistingException("The person " + upperCaseFirstName + " " + upperCaseLastName + " was already existing, so it couldn't have been created.\n");
        }
    }

    /**
     * Save a person object in data
     *
     * @param personToUpdate - A person object which has to be saved in data
     * @return the person object which was saved
     */
    public String updatePerson(Person personToUpdate, Person personWithNewInformation) throws NothingToUpdateException, NotTheSamePersonException {
        log.debug("The function updatePerson in PersonService is beginning.");

        String upperCaseFirstName = personToUpdate.getFirstName().toUpperCase();
        String upperCaseLastName = personToUpdate.getLastName().toUpperCase();

        String firstName;
        String lastName;
        try {
            firstName = personWithNewInformation.getFirstName().toUpperCase();
        } catch (NullPointerException e) {
            firstName = "";
        }
        try {
            lastName = personWithNewInformation.getLastName().toUpperCase();
        } catch (NullPointerException e) {
            lastName = "";
        }
        if (((firstName.isBlank()) || (upperCaseFirstName.equals(firstName)))
                &&
                ((lastName.isBlank()) || (upperCaseLastName.equals(lastName)))) {

            boolean updated = false;
            String itemsChanged = "";

            String address = personWithNewInformation.getAddress();
            if (address != null) {
                personToUpdate.setAddress(address);
                updated = true;
                itemsChanged = itemsChanged + "- the address : " + address + "\n";
            }
            String city = personWithNewInformation.getCity();
            if (city != null) {
                personToUpdate.setCity(city.toUpperCase());
                updated = true;
                itemsChanged = itemsChanged + "- the city : " + city + "\n";
            }
            String mail = personWithNewInformation.getMail();
            if (mail != null) {
                personToUpdate.setMail(mail);
                updated = true;
                itemsChanged = itemsChanged + "- the mail : " + mail + "\n";
            }
            String phoneNumber = personWithNewInformation.getPhoneNumber();
            if (phoneNumber != null) {
                personToUpdate.setPhoneNumber(phoneNumber);
                updated = true;
                itemsChanged = itemsChanged + "- the phone number : " + phoneNumber + "\n";
            }
            int zip = personWithNewInformation.getZip();
            if (zip != 0) {
                personToUpdate.setZip(zip);
                updated = true;
                itemsChanged = itemsChanged + "- the zip : " + zip + "\n";
            }
            if (updated) {
                personRepository.save(personToUpdate);
                String updatingMessage = "The person " + upperCaseFirstName + " " + upperCaseLastName + " have been updated with following items :\n" + itemsChanged;
                log.info(updatingMessage);
                log.debug("The function updatePerson in PersonService is ending with updating a person.");
                return updatingMessage;
            } else {
                log.debug("The function updatePerson in PersonService is ending without updating anything.");
                throw new NothingToUpdateException("The person " + upperCaseFirstName + " " + upperCaseLastName + " wasn't updated, there was no element to update.\n");
            }
        } else {
            log.debug("The function updatePerson in PersonService is ending without updating anything.");
            throw new NotTheSamePersonException("It's not possible to update person first name or person last name,\n" +
                    "so the person called " + upperCaseFirstName + " " + upperCaseLastName + " cannot be updated to a person called " + personWithNewInformation.getFirstName().toUpperCase() + " " + personWithNewInformation.getLastName().toUpperCase() + ".\n");
        }
    }

    /**
     * Get one person from his first name and last name
     *
     * @param firstName - A String which is the first name of the researched person
     * @param lastName  - A String which is the last name of the researched person
     * @return an optional person object which is the person researched if it's found
     */
    public Person getPersonByName(String firstName, String lastName) throws PersonNotFoundException {
        log.debug("The function getPersonByName in PersonService is beginning.");
        String upperCaseFirstName = firstName.toUpperCase();
        String upperCaseLastName = lastName.toUpperCase();
        Optional<Person> personResearched = personRepository.findByFirstNameAndLastName(upperCaseFirstName, upperCaseLastName);
        if (personResearched.isPresent()) {
            Person personFound = personResearched.get();
            log.debug("The function getPersonByName in PersonService is ending, a person was found");
            return personFound;
        } else {
            log.debug("The function getPersonByName in PersonService is ending, no person was found.");
            throw new PersonNotFoundException("The person " + upperCaseFirstName + " " + upperCaseLastName + " was not found.\n");
        }
    }

    public boolean existPersonByName(String firstName, String lastName) {
        return personRepository.findByFirstNameAndLastName(firstName, lastName).isPresent();
    }
}
