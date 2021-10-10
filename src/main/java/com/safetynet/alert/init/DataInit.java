package com.safetynet.alert.init;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Profile("!test")
@Slf4j
@Component
public class DataInit implements ApplicationRunner {



    private final PersonRepository personRepository;



    @Autowired
    public DataInit(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


        @Override
        public void run(ApplicationArguments args) {

            log.debug("DataInit is beginning.");
            try {
                RestTemplate restTemplate = new RestTemplate();
                DataFile dataFile = new DataFile();
                ResponseEntity<ObjectNode> response =
                        restTemplate.getForEntity(dataFile.getDataUrl(), ObjectNode.class);
                log.debug("Getting a response from dataUrl.");
                ObjectNode jsonObject = response.getBody();
                log.info("The file data.json have been get : " + response.getStatusCode());


                if (jsonObject != null) {
                    for(int numberOfPersons = 0; numberOfPersons< jsonObject.path("persons").size();numberOfPersons++){

                        Person person = new Person();
                        person.setFirstName(jsonObject.path("persons").path(numberOfPersons).path("firstName").asText().toUpperCase());
                        person.setLastName(jsonObject.path("persons").path(numberOfPersons).path("lastName").asText().toUpperCase());
                        person.setAddress(jsonObject.path("persons").path(numberOfPersons).path("address").asText());
                        person.setCity(jsonObject.path("persons").path(numberOfPersons).path("city").asText().toUpperCase());
                        person.setZip(jsonObject.path("persons").path(numberOfPersons).path("zip").asInt());
                        person.setPhoneNumber(jsonObject.path("persons").path(numberOfPersons).path("phone").asText());
                        person.setMail(jsonObject.path("persons").path(numberOfPersons).path("email").asText());
                        personRepository.save(person);
                        log.info(person.getFirstName().toUpperCase()+" "+person.getLastName().toUpperCase()+" have been saved. \n");
                    }
                    log.info("All the persons from data.json file have been registered in dataBase.\n");
                }



            } catch (Exception e) {
                log.error("Something went wrong while calling data.json file : " + e.getMessage()+"\n");

            }
        }

}
