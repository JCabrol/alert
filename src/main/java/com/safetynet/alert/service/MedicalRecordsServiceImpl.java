package com.safetynet.alert.service;

import com.safetynet.alert.exceptions.*;
import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.model.DTO.MedicalRecordDTO;
import com.safetynet.alert.model.MedicalRecords;
import com.safetynet.alert.model.Medication;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.MedicalRecordsRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter

@Service
@Slf4j
public class MedicalRecordsServiceImpl implements MedicalRecordsService {

    @Autowired
    private MedicalRecordsRepository medicalRecordsRepository;

    @Autowired
    private PersonService personService;

    /**
     * Get all the medical records presents in data
     *
     * @return a list containing all the medical records
     * @throws EmptyMedicalRecordsException - when there are no medical records found
     */
    @Override
    public List<MedicalRecordDTO> getMedicalRecords() throws EmptyMedicalRecordsException {
        log.debug("The function getMedicalRecords in MedicalRecordsService is beginning.");
        List<MedicalRecords> allMedicalRecords = (List<MedicalRecords>) medicalRecordsRepository.findAll();
        if (!allMedicalRecords.isEmpty()) {
            List<MedicalRecordDTO> allMedicalRecordsDTO = allMedicalRecords.stream().map(this::transformMedicalRecordsToMedicalRecordDTO).collect(Collectors.toList());
            log.debug("The function getMedicalRecords in MedicalRecordsService is ending. Some medical records were found.");
            return allMedicalRecordsDTO;
        } else {
            log.debug("The function getMedicalRecords in MedicalRecordsService is ending without founding any medical record.");
            throw new EmptyMedicalRecordsException("There are no medical records registered.\n");
        }
    }

    /**
     * Create a MedicalRecordDTO object containing information about medicalRecords
     *
     * @param medicalRecords - a medical records
     * @return a String with all information about the medical records presents in the list
     */
    @Override
    public MedicalRecordDTO transformMedicalRecordsToMedicalRecordDTO(MedicalRecords medicalRecords) {
        log.debug("The function transformMedicalRecordsToMedicalRecordDTO in MedicalRecordsService is beginning.");
        String firstName = medicalRecords.getPerson().getFirstName();
        String lastName = medicalRecords.getPerson().getLastName();
        String birthdate = medicalRecords.getBirthdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        List<String> medications = medicalRecords.getMedications().stream().map(Medication::getMedicationName).collect(Collectors.toList());
        List<String> allergies = medicalRecords.getAllergies().stream().map(Allergy::getAllergyName).collect(Collectors.toList());
        MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO(firstName, lastName, birthdate, medications, allergies);
        log.debug("The function transformMedicalRecordsToMedicalRecordDTO in MedicalRecordsService is ending without any exception.");
        return medicalRecordDTO;
    }


    /**
     * Get person's medical records having his first name and his last name
     *
     * @param firstName - a String which is the first name of the person whose medical records is researched
     * @param lastName  - a String which is the last name of the person whose medical records is researched
     * @return MedicalRecords object concerning the researched person
     */
    @Override
    public MedicalRecords getMedicalRecordsByName(String firstName, String lastName) throws MedicalRecordsNotFoundException {
        log.debug("The function getMedicalRecordsByName in MedicalRecordsService is beginning.");
        Person person = personService.getPersonByName(firstName, lastName);
        if (person.getMedicalRecords() != null) {
            MedicalRecords medicalRecords = person.getMedicalRecords();
            log.debug("The function getMedicalRecordsByName in MedicalRecordsService is ending without any exception.");
            return medicalRecords;
        } else {
            throw new MedicalRecordsNotFoundException("There is no medical records found for the person " + firstName + " " + lastName + ".\n");
        }
    }

    @Override
    public MedicalRecords addNewMedicalRecords(MedicalRecordDTO medicalRecords) throws NotRightFormatToPostException {
        log.debug("The function addNewMedicalRecords in MedicalRecordsService is beginning.");
        //if there's no first name and last name an exception is thrown
        String firstName = medicalRecords.getFirstName();
        String lastName = medicalRecords.getLastName();
        if ((firstName == null) || (lastName == null)) {
            throw new NotRightFormatToPostException("To add new medical records, the body's request should contains, at least, a \"firstName\" and a \"lastName\" fields.");
        } else {
            MedicalRecords medicalRecordsToSave;
            Person person;
            //verifying the researched person is existing
            try {
                person = personService.getPersonByName(firstName, lastName);
                //if the person already have medical records, they are selected to be replaced
                if (person.getMedicalRecords() != null) {
                    medicalRecordsToSave = person.getMedicalRecords();
                    //if the person doesn't have medical records, they are created and attached to the person
                } else {
                    medicalRecordsToSave = new MedicalRecords();
                    medicalRecordsToSave.addPerson(person);
                }
                //if the person doesn't exist, it's created and medical records are created and attached to this new person
            } catch (PersonNotFoundException ex) {
                person = new Person(firstName.toUpperCase(), lastName.toUpperCase());
                medicalRecordsToSave = new MedicalRecords();
                medicalRecordsToSave.addPerson(person);
            }
            //putting birthdate to medical records
            String birthdate = medicalRecords.getBirthdate();
            LocalDate birthdateFormatted = null;
            if (birthdate != null) {
                int birthdateDay;
                int birthdateMonth;
                int birthdateYear;
                try {
                    birthdateDay = Integer.parseInt(birthdate.substring(0, 2));
                    birthdateMonth = Integer.parseInt(birthdate.substring(3, 5));
                    birthdateYear = Integer.parseInt(birthdate.substring(6, 10));
                } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                    throw new NotRightFormatToPostException("The birthdate should be given at the format \"dd/MM/yyyy\".\n");
                }
                //
                try {
                    birthdateFormatted = LocalDate.of(birthdateYear, birthdateMonth, birthdateDay);
                } catch (DateTimeException e) {
                    throw new NotRightFormatToPostException("The birthdate should be given at the format \"dd/MM/yyyy\"\n" + e.getMessage());
                }
            }
            medicalRecordsToSave.setBirthdate(birthdateFormatted);
            //clearing medications (if not empty) and putting new ones
            medicalRecordsToSave.getMedications().clear();
            for (String medication : medicalRecords.getMedications()) {
                medicalRecordsToSave.addMedication(new Medication(medication));
            }
            //clearing allergies (if not empty) and putting new ones
            medicalRecordsToSave.getAllergies().clear();
            for (String allergy : medicalRecords.getAllergies()) {
                medicalRecordsToSave.addAllergy(new Allergy(allergy));
            }
            //saving new medical records and returning it
            medicalRecordsRepository.save(medicalRecordsToSave);
            return medicalRecordsToSave;
        }
    }

    /**
     * Update person's medical records having his first name and his last name and information to update
     *
     * @param firstName        - a String which is the first name of the person whose medical records is researched
     * @param lastName         - a String which is the last name of the person whose medical records is researched
     * @param medicalRecordDTO - an object containing information to update
     * @return a String containing the information which have been updated
     * @throws NotTheSamePersonException - when the information to update contains a different first name or a different last name
     * @throws NothingToUpdateException  - when there are no information to update in the medicalRecordDTO
     * @throws PersonNotFoundException   - when no person is found with the given firstName and lastName
     */
    @Override
    public String updateMedicalRecord(String firstName, String lastName, MedicalRecordDTO medicalRecordDTO) throws NotTheSamePersonException, NothingToUpdateException, PersonNotFoundException {
        log.debug("The function updateMedicalRecords in MedicalRecordsService is beginning.");
        //getting the person concerned using firstName and lastName
        String upperCaseFirstName = firstName.toUpperCase();
        String upperCaseLastName = lastName.toUpperCase();
        Person personToUpdateMedicals = personService.getPersonByName(upperCaseFirstName, upperCaseLastName);
        //if there's a different firstName or lastName in the MedicalRecordDTO information an exception is thrown
        String firstNameNew = medicalRecordDTO.getFirstName();
        String lastNameNew = medicalRecordDTO.getLastName();
        if (((firstNameNew != null) && (!(firstNameNew.toUpperCase()).equals(upperCaseFirstName))) || ((lastNameNew != null) && (!lastNameNew.toUpperCase().equals(upperCaseLastName)))) {
            log.debug("The function updateMedicalRecords in MedicalrecordsService is ending without updating anything.");
            throw new NotTheSamePersonException("It's not possible to update first name or last name.");
        }
        //getting person's medical records. If it doesn't exist creating some and attaching it to the person
        MedicalRecords medicalRecords = personToUpdateMedicals.getMedicalRecords();
        if (medicalRecords == null) {
            medicalRecords = new MedicalRecords();
            medicalRecords.addPerson(personToUpdateMedicals);
        }
        //using a boolean and a String to remember if there are changes and which ones
        boolean updated = false;
        String itemsChanged = "";
        //if there's a birthdate in the MedicalRecordDTO information, it's set to the person's medical records
        String birthdate = medicalRecordDTO.getBirthdate();
        LocalDate birthdateFormatted;
        if (birthdate != null) {
            int birthdateDay;
            int birthdateMonth;
            int birthdateYear;
            try {
                birthdateDay = Integer.parseInt(birthdate.substring(0, 2));
                birthdateMonth = Integer.parseInt(birthdate.substring(3, 5));
                birthdateYear = Integer.parseInt(birthdate.substring(6, 10));
            } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                throw new NotRightFormatToPostException("The birthdate should be given at the format \"dd/MM/yyyy\".\n");
            }
            //
            try {
                birthdateFormatted = LocalDate.of(birthdateYear, birthdateMonth, birthdateDay);
            } catch (DateTimeException e) {
                throw new NotRightFormatToPostException("The birthdate should be given at the format \"dd/MM/yyyy\"\n" + e.getMessage());
            }
            medicalRecords.setBirthdate(birthdateFormatted);
            updated = true;
            itemsChanged = itemsChanged + "- the birthdate : " + birthdate + "\n";
        }
        //if there are medications in the MedicalRecordDTO information, older medications are cleared and new ones are set to the person's medical records
        List<String> medications = medicalRecordDTO.getMedications();
        if (!medications.equals(Collections.emptyList())) {
            medicalRecords.getMedications().clear();
            for (String medication : medications) {
                medicalRecords.addMedication(new Medication(medication));
            }
            updated = true;
            itemsChanged = itemsChanged + "- the medications : " + medications + "\n";
        }
        //if there are allergies in the MedicalRecordDTO information, older allergies are cleared and new ones set to the person's medical records
        List<String> allergies = medicalRecordDTO.getAllergies();
        if (!allergies.equals(Collections.emptyList())) {
            medicalRecords.getAllergies().clear();
            for (String allergy : allergies) {
                medicalRecords.addAllergy(new Allergy(allergy));
            }
            updated = true;
            itemsChanged = itemsChanged + "- the allergies : " + allergies + "\n";
        }
        //if there are changes, they are registered and a String containing changed information is returned
        if (updated) {
            medicalRecordsRepository.save(medicalRecords);
            String updatingMessage = "The medical records about the person " + upperCaseFirstName + " " + upperCaseLastName + " have been updated with following items :\n" + itemsChanged;
            log.info(updatingMessage);
            log.debug("The function updateMedicalRecords in MedicalRecordsService is ending with updating medical records.");
            return updatingMessage;
            //if nothing has been changed, an exception is thrown
        } else {
            log.debug("The function updateMedicalRecords in MedicalrecordsService is ending without updating anything.");
            throw new NothingToUpdateException("The medical records about the person " + upperCaseFirstName + " " + upperCaseLastName + " wasn't updated, there was no element to update.\n");
        }
    }

    /**
     * Delete medical records
     *
     * @param firstName -a String which is the first name of the person whose medical records have to be deleted
     * @param lastName  -a String which is the last name of the person whose medical records have to be deleted
     * @return a String which indicates the person whose medical records have been deleted
     * @throws NothingToDeleteException - when the medical records to delete don't exist anyway
     * @throws PersonNotFoundException  - when no person is found with the given firstName and lastName
     */
    @Override
    public String deleteMedicalRecords(String firstName, String lastName) throws NothingToDeleteException, PersonNotFoundException {
        log.debug("The function deleteMedicalRecords in MedicalRecordsService is beginning.");
        //getting the person concerned using firstName and lastName
        String upperCaseFirstName = firstName.toUpperCase();
        String upperCaseLastName = lastName.toUpperCase();
        Person personDeleteMedicalRecords = personService.getPersonByName(upperCaseFirstName, upperCaseLastName);
        //getting person's medical records
        MedicalRecords medicalRecordsToDelete = personDeleteMedicalRecords.getMedicalRecords();
        //removing person's medical records if they're existing and returning confirmation message
        if (medicalRecordsToDelete != null) {
            medicalRecordsToDelete.removePerson(personDeleteMedicalRecords);
            medicalRecordsRepository.delete(medicalRecordsToDelete);
            String message = "The medical records about the person " + upperCaseFirstName + " " + upperCaseLastName + " have been deleted.\n";
            log.debug("The function deleteMedicalRecords in MedicalRecordsService is ending.");
            return message;
            //if person's medical records are empty an exception is thrown
        } else {
            throw new NothingToDeleteException("The medical records about the person " + upperCaseFirstName + " " + upperCaseLastName + " weren't found, so it couldn't have been deleted.\n");
        }
    }
}

