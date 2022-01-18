package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Address;
import com.safetynet.alert.model.DTO.PersonDTO;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressService addressService;

    /**
     * Get all the persons presents in data
     *
     * @return a list containing all the persons presents in data
     * @throws EmptyObjectException When the list is empty
     */
    @Override
    public List<Person> getPersons() throws EmptyObjectException {
        log.debug("The function getPersons in PersonService is beginning.");
        List<Person> allPersons = (List<Person>) personRepository.findAll();
        if (!allPersons.isEmpty()) {
            log.debug("The function getPersons in PersonService is ending. Some persons were found.");
            return allPersons;
        } else {
            throw new EmptyObjectException("There are no persons registered.\n");
        }
    }

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
     * Get one person from his id
     *
     * @param id A String which is composed by the person's first name and last name
     * @return a Person object which corresponds to researched person
     * @throws ObjectNotFoundException When the researched person is not found
     */
    public Person getPersonById(String id) throws ObjectNotFoundException {
        log.debug("The function getPersonById in PersonService is beginning.");
        Optional<Person> personResearched = personRepository.findById(id);
        if (personResearched.isPresent()) {
            Person personFound = personResearched.get();
            log.debug("The function getPersonById in PersonService is ending, a person was found");
            return personFound;
        } else {
            log.debug("The function getPersonByName in PersonService is ending, no person was found.");
            throw new ObjectNotFoundException("The person with id " + id + " was not found.\n");
        }
    }

    /**
     * Get one personDTO from its id
     *
     * @param id A String which is composed by person's first name and last name
     * @return a PersonDTO object which corresponds to researched person
     */
    @Override
    public PersonDTO getPersonDTOById(String id) {
        log.debug("The function getPersonDTOById in PersonService is beginning.");
        Person personFound = getPersonById(id);
        PersonDTO personDTOFound = transformPersonToPersonDTO(personFound);
        log.debug("The function getPersonDTOById in PersonService is ending, a person was found");
        return personDTOFound;
    }

    /**
     * Get one person from his first name and last name
     *
     * @param firstName A String which is the first name of the researched person
     * @param lastName  A String which is the last name of the researched person
     * @return a Person object which corresponds to researched person
     * @throws ObjectNotFoundException When the researched person is not found
     */
    @Override
    public List<Person> getPersonsByName(String firstName, String lastName) throws ObjectNotFoundException {
        log.debug("The function getPersonByName in PersonService is beginning.");
        String upperCaseFirstName = firstName.toUpperCase();
        String upperCaseLastName = lastName.toUpperCase();
        List<Person> personResearched = personRepository.findByFirstNameAndLastName(upperCaseFirstName, upperCaseLastName);
        if (!personResearched.isEmpty()) {
            log.debug("The function getPersonByName in PersonService is ending, a person was found");
            return personResearched;
        } else {
            log.debug("The function getPersonByName in PersonService is ending, no person was found.");
            throw new ObjectNotFoundException("The person " + upperCaseFirstName + " " + upperCaseLastName + " was not found.\n");
        }
    }

    /**
     * Create a new person
     *
     * @param person A PersonDTO object which has to be saved
     * @return the person object which was saved
     * @throws ObjectAlreadyExistingException When the person to create already exists
     * @throws NotRightFormatToPostException  When the first name or the last name is missing
     */
    @Override
    public Person createPerson(PersonDTO person) throws ObjectAlreadyExistingException, NotRightFormatToPostException {
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
        boolean exists = existPersonByName(upperCaseFirstName, upperCaseLastName);
        boolean itsTheSame = false;
        if (exists) {
            List<Person> samePerson = getPersonsByName(upperCaseFirstName, upperCaseLastName)
                    .stream()
                    .filter(p -> p.getAddress().getStreet().equalsIgnoreCase(person.getAddress()))
                    .collect(Collectors.toList());
            if (!samePerson.isEmpty()) {
                itsTheSame = true;
            }
        }
        if (!itsTheSame) {
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
            throw new ObjectAlreadyExistingException("The person " + upperCaseFirstName + " " + upperCaseLastName + " was already existing, so it couldn't have been created.\n");
        }
    }

    /**
     * Indicates if a person with this name already exists or not
     *
     * @param firstName A String which is the first name of the researched person
     * @param lastName  A String which is the last name of the researched person
     * @return a boolean indicating if a person with the given first name and last name exists
     */
    public boolean existPersonByName(String firstName, String lastName) {
        log.debug("The function existPersonByName in PersonService is beginning.");
        boolean exists = !personRepository.findByFirstNameAndLastName(firstName, lastName).isEmpty();
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
        } catch (ObjectNotFoundException e) {
            address = new Address(street, zip, city);
        }
        if (address != null) {
            address.addPerson(person);
        }
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
     * Update a person
     *
     * @param personToUpdate           - A personDTO object representing an exiting person to update
     * @param personWithNewInformation - A personDTO object containing new information to update
     * @throws NothingToUpdateException  - when there was nothing to update
     * @throws NotTheSamePersonException - When the personWithNewInformation's name doesn't correspond to the personToUpdate's name
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

            Person person = getPersonById(upperCaseFirstName + upperCaseLastName);
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
                } catch (ObjectNotFoundException e) {
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
     * @param id - a String which is composed by the person's first name and last name
     * @throws ObjectNotFoundException - when the person to delete is not found
     */
    @Override
    public void deletePersonById(String id) throws ObjectNotFoundException {
        log.debug("The function deletePersonByName in PersonService is beginning.");
        try {
            getPersonById(id);
            personRepository.deleteById(id);
            log.info("The person with id " + id + " have been deleted. \n");
            log.debug("The function deletePersonByName in PersonService is ending, a person have been deleted.");
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("The person with id " + id + " was not found, so it cannot have been deleted.\n");
        }
    }
}
