package com.safetynet.alert.init;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alert.model.AttachedAddress;
import com.safetynet.alert.model.Firestation;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.FirestationRepository;
import com.safetynet.alert.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Optional;

@Profile("!test")
@Slf4j
@Component
public class DataInit implements ApplicationRunner {


    private final PersonRepository personRepository;
    private final FirestationRepository firestationRepository;


    @Autowired
    public DataInit(PersonRepository personRepository, FirestationRepository firestationRepository) {
        this.personRepository = personRepository;
        this.firestationRepository = firestationRepository;
    }


    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        log.debug("DataInit is beginning.");
        try {
            //getting json file from which information has to be read
            RestTemplate restTemplate = new RestTemplate();
            DataFile dataFile = new DataFile();
            ResponseEntity<ObjectNode> response =
                    restTemplate.getForEntity(dataFile.getDataUrl(), ObjectNode.class);
            log.debug("Getting a response from dataUrl.");
            ObjectNode jsonObject = response.getBody();
            log.info("The file data.json have been get : " + response.getStatusCode());


            if (jsonObject != null) {
                //getting each person in the json file and create a person objet with information and save it into database
                for (int numberOfPersons = 0; numberOfPersons < jsonObject.path("persons").size(); numberOfPersons++) {
                    Person person = new Person();
                    person.setFirstName(jsonObject.path("persons").path(numberOfPersons).path("firstName").asText().toUpperCase());
                    person.setLastName(jsonObject.path("persons").path(numberOfPersons).path("lastName").asText().toUpperCase());
                    person.setAddress(jsonObject.path("persons").path(numberOfPersons).path("address").asText());
                    person.setCity(jsonObject.path("persons").path(numberOfPersons).path("city").asText().toUpperCase());
                    person.setZip(jsonObject.path("persons").path(numberOfPersons).path("zip").asInt());
                    person.setPhoneNumber(jsonObject.path("persons").path(numberOfPersons).path("phone").asText());
                    person.setMail(jsonObject.path("persons").path(numberOfPersons).path("email").asText());
                    personRepository.save(person);
                    log.info(person.getFirstName().toUpperCase() + " " + person.getLastName().toUpperCase() + " have been saved. \n");
                }
                log.info("All the persons from data.json file have been registered in dataBase.\n");

                //getting each the mapping firestation/address in the json file
                for (int numberOfAddresses = 0; numberOfAddresses < jsonObject.path("firestations").size(); numberOfAddresses++) {
                    int stationNumber = jsonObject.path("firestations").path(numberOfAddresses).path("station").asInt();
                    String address = jsonObject.path("firestations").path(numberOfAddresses).path("address").asText();
                    Optional<Firestation> firestationToUpdate = firestationRepository.findById(stationNumber);
                    if (firestationToUpdate.isPresent()) {
                        //if the firestation already exists, the address is added to its attached addresses and saved in database
                        firestationToUpdate.get().addAttachedAddress(new AttachedAddress(address));
                        firestationRepository.save(firestationToUpdate.get());
                        log.info("The fire station number " + stationNumber + " have been updated, adding the address: " + address + ". \n");
                    } else {
                        //if the firestation doesn't exist, a firestation object is created, the address is added to its attached addresses and then the firestation is saved into database
                        Firestation firestation = new Firestation();
                        firestation.setStationId(stationNumber);
                        AttachedAddress myAddress = new AttachedAddress(address);
                        firestation.addAttachedAddress(myAddress);
                        firestationRepository.save(firestation);
                        log.info("Firestation number" + stationNumber + " have been created in dataBase with the address " + address + ".\n");
                    }
                }
                log.info("All the firestations with all their addresses from data.json file have been registered in dataBase.\n");
            }
        } catch (Exception e) {
            log.error("Something went wrong while calling data.json file : " + e.getMessage() + "\n");
        }
    }
}
