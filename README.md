# SafetyNet Alert

A Java application which purpose is to send information to emergency service systems.

A project done as part of my Java application developper training (project n°5).

The main skills acquired with this project were:

* select the appropriate programming languages for the development of the application
* respect the good development current practices
* develop an application offering the expected functionalities

 Here are the [presentation slideshows](https://github.com/JCabrol/alert/blob/master/P5_04_syntheseClient_CabrolJustine.ppsx) I made for this project.

## Technical specifications

Code:
* Java 11
* SpringBoot 2.5.4
* Maven 3.0.0

Tests:
* JUnit 5.7.2
* Mockito 3.9.0

Data:
* H2 database
* Spring Data JPA 2.5.4

Documentation,monitoring and maintenance:
* Slf4j 1.7.32
* Swagger 2.9.2
* SpringBoot Actuator 2.5.4
* Sonarcloud ([here is my Sonarcloud repository](https://sonarcloud.io/summary/overall?id=JCabrol_alert))

## Application conception

### General Architecture Diagram

The application is developped with an MVC architecture, the codebase adheres to SOLID principles

![image](https://user-images.githubusercontent.com/74394605/189656433-94189959-0ec2-425a-b7fd-331244dfe2b9.png)

## UML class diagram

![image](https://user-images.githubusercontent.com/74394605/189656018-899d60eb-ba70-42ee-b35f-b0d9a629f674.png)

## Exposed endpoints

The documentation is available on Swagger

![image](https://user-images.githubusercontent.com/74394605/189667466-c9659a78-1e4e-4827-80fb-933f52f1237e.png)
![image](https://user-images.githubusercontent.com/74394605/189667520-a1371175-0788-4637-a416-89fc9ae9a1e4.png)
![image](https://user-images.githubusercontent.com/74394605/189667574-db2ac4c1-0056-4ac2-b8fd-2ff060e7e889.png)
![image](https://user-images.githubusercontent.com/74394605/189667680-46bb489f-ab71-42b1-8725-993892878aa0.png)
