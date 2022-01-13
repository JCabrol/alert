package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Address;
import com.safetynet.alert.model.DTO.PersonDTO;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.AddressRepository;
import com.safetynet.alert.repository.PersonRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter

@Service
@Slf4j
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressService addressService;

    /**
     * Get all the persons presents in data in PersonDTO object format
     *
     * @return a list containing all the persons presents in data
     */
    @Override
    public List<PersonDTO> getPersonsDTO() {
        log.debug("The function getPersonsDTO in PersonService is beginning.");

        List<PersonDTO> result = getPersons()
                .stream()
                .map(this::transformPersonToPersonDTO)
                .collect(Collectors.toList());

        log.debug("The function getPersons in PersonService is ending. Some persons were found.");
        return result;
    }

    /**
     * Get all the persons presents in data
     *
     * @return a list containing all the persons presents in data
     * @throws EmptyPersonsException When the list is empty
     */
    @Override
    public List<Person> getPersons() throws EmptyPersonsException {
        log.debug("The function getPersons in PersonService is beginning.");
        List<Person> allPersons = (List<Person>) personRepository.findAll();
        if (!allPersons.isEmpty()) {
            log.debug("The function getPersons in PersonService is ending. Some persons were found.");
            return allPersons;
        } else {
            throw new EmptyPersonsException("There are no persons registered.\n");
        }
    }

    /**
     * Get one person from his first name and last name
     *
     * @param firstName A String which is the first name of the researched person
     * @param lastName  A String which is the last name of the researched person
     * @return a PersonDTO object which corresponds to researched person
     * @throws PersonNotFoundException When the researched person is not found
     */
    @Override
    public PersonDTO getPersonDTOByName(String firstName, String lastName) throws PersonNotFoundException {
        log.debug("The function getPersonDTOByName in PersonService is beginning.");
        String upperCaseFirstName = firstName.toUpperCase();
        String upperCaseLastName = lastName.toUpperCase();
        Person personFound = getPersonByName(upperCaseFirstName, upperCaseLastName);
        PersonDTO personDTOResult = transformPersonToPersonDTO(personFound);
        log.debug("The function getPersonDTOByName in PersonService is ending, a person was found");
        return personDTOResult;
    }


    /**
     * Get one person from his first name and last name
     *
     * @param firstName A String which is the first name of the researched person
     * @param lastName  A String which is the last name of the researched person
     * @return a Person object which corresponds to researched person
     * @throws PersonNotFoundException When the researched person is not found
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

    /**
     * Save a person object in data
     *
     * @param person A PersonDTO object which has to be saved
     * @return the person object which was saved
     * @throws PersonAlreadyExistingException When the person to create already exists
     * @throws NotRightFormatToPostException  When the first name or the last name is missing
     */
    @Override
    public Person createPerson(PersonDTO person) throws PersonAlreadyExistingException, NotRightFormatToPostException {
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
            Person createdPerson = transformPersonDTOToPerson(person);
            personRepository.save(createdPerson);
            log.info("The person " + upperCaseFirstName + " " + upperCaseLastName + " have been created.");
            log.debug("The function createPerson in PersonService is ending with creating a person.");
            return createdPerson;
        } else {
            log.debug("The function createPerson in PersonService is ending without creating anybody.");
            throw new PersonAlreadyExistingException("The person " + upperCaseFirstName + " " + upperCaseLastName + " was already existing, so it couldn't have been created.\n");
        }
    }

    /**
     * Indicates if a person already exists or not
     *
     * @param firstName A String which is the first name of the researched person
     * @param lastName  A String which is the last name of the researched person
     * @return a boolean indicating if a person with the given first name and last name exists
     */
    public boolean existPersonByName(String firstName, String lastName) {
        log.debug("The function existPersonByName in PersonService is beginning.");
        boolean exists = personRepository.findByFirstNameAndLastName(firstName, lastName).isPresent();
        log.debug("The function existPersonByName in PersonService is ending.");
        return exists;
    }

    /**
     * Transforms a PersonDTO object to a Person object containing the same information
     *
     * @param personDTO a PersonDTO object
     * @return a Person object with the same information
     */
    public Person transformPersonDTOToPerson(PersonDTO personDTO) {
        log.debug("The function transformPersonDTOToPerson in PersonService is beginning.");
        Person person = new Person(personDTO.getFirstName(), personDTO.getLastName());
        String street = personDTO.getAddress();
        String zip = personDTO.getZip();
        String city = personDTO.getCity();
        Address address;
        try {
            address = addressService.getAddress(street, zip, city);
        } catch (AddressNotFoundException e) {
            address = new Address(street, zip, city);
        }
        address.addPerson(person);
        person.setPhoneNumber(personDTO.getPhoneNumber());
        person.setMail(personDTO.getMail());
        log.debug("The function transformPersonDTOToPerson in PersonService is ending.");
        return person;
    }

    /**
     * Transforms a Person object to a PersonDTO object containing the same information
     *
     * @param person a Person object
     * @return a PersonDTO object with the same information
     */
    public PersonDTO transformPersonToPersonDTO(Person person) {
        log.debug("The function transformPersonToPersonDTO in PersonService is beginning.");
        PersonDTO personDTO = new PersonDTO(
                person.getFirstName(),
                person.getLastName(),
                person.getAddress().getStreet(),
                person.getAddress().getZip(),
                person.getAddress().getCity(),
                person.getPhoneNumber(),
                person.getMail());
        log.debug("The function transformPersonToPersonDTO in PersonService is ending.");
        return personDTO;
    }

    /**
     * Save a person object in data
     *
     * @param personToUpdate - A person object which has to be saved in data
     */
    @Override
    public Person updatePerson(PersonDTO personToUpdate, PersonDTO personWithNewInformation) throws NothingToUpdateException, NotTheSamePersonException {
        log.debug("The function updatePerson in PersonService is beginning.");
        String upperCaseFirstName = personToUpdate.getFirstName().toUpperCase();
        String upperCaseLastName = personToUpdate.getLastName().toUpperCase();
        String firstName;
        String lastName;
        boolean updated = false;
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
        if (((firstName.equals("")) || (upperCaseFirstName.equals(firstName)))
                &&
                ((lastName.equals("")) || (upperCaseLastName.equals(lastName)))) {

            Person person = getPersonByName(upperCaseFirstName, upperCaseLastName);
            String street = personWithNewInformation.getAddress();
            String city = personWithNewInformation.getCity();
            if (city != null) {
                city = city.toUpperCase();
            }
            String zip = personWithNewInformation.getZip();
            if ((street != null) || (city != null) || (zip != null)) {
                Address address;
                try {
                    address = addressService.getAddress(street, zip, city);
                } catch (AddressNotFoundException e) {
                    address = new Address(street, zip, city);
                }
                address.addPerson(person);
                updated = true;
            }
            String mail = personWithNewInformation.getMail();
            if (mail != null) {
                person.setMail(mail);
                updated = true;
            }
            String phoneNumber = personWithNewInformation.getPhoneNumber();
            if (phoneNumber != null) {
                person.setPhoneNumber(phoneNumber);
                updated = true;
            }
            if (updated) {
                Person personSaved = personRepository.save(person);

                log.info("The person " + personSaved.getFirstName() + " " + personSaved.getLastName() + " have been updated.");
                log.debug("The function updatePerson in PersonService is ending with updating a person.");
                return personSaved;
            } else {
                log.debug("The function updatePerson in PersonService is ending without updating anything.");
                throw new NothingToUpdateException("The person " + upperCaseFirstName + " " + upperCaseLastName + " wasn't updated, there was no element to update.\n");
            }
        } else {
            log.debug("The function updatePerson in PersonService is ending without updating anything.");
            throw new NotTheSamePersonException("It's not possible to update person first name or person last name,\n" +
                    "so the person called " + upperCaseFirstName + " " + upperCaseLastName + " cannot be updated to a person called " + firstName + " " + lastName + ".\n");
        }
    }

    /**
     * Delete one person from his id
     *
     * @param firstName - an int which is the primary key of the researched person
     */
    @Override
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
            throw new PersonNotFoundException("The person " + upperCaseFirstName + " " + upperCaseLastName + " was not found, so it cannot have been deleted.\n");
        }
    }
}
