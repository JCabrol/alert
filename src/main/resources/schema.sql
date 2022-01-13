  --CREATE TABLE person (
 -- id INT AUTO_INCREMENT NOT NULL,
 -- first_name VARCHAR(255) NULL,
 -- last_name VARCHAR(255) NULL,
 -- address_id INT NULL,
 -- phone_number VARCHAR(255) NULL,
 -- mail VARCHAR(255) NULL,
--  medical_id INT NULL,
--  CONSTRAINT pk_person PRIMARY KEY (id)
--);



--CREATE TABLE address (
--  address_id INT AUTO_INCREMENT NOT NULL,
--  street VARCHAR(255) NULL,
--  zip VARCHAR(255) NULL,
--  city VARCHAR(255) NULL,
--  station_id INT NULL,
 -- CONSTRAINT pk_address PRIMARY KEY (address_id)
--);



--CREATE TABLE firestation (
 -- station_id INT NOT NULL,
 -- CONSTRAINT pk_firestation PRIMARY KEY (station_id)
--);

--ALTER TABLE person ADD CONSTRAINT FK_PERSON_ON_ADDRESS FOREIGN KEY (address_id) REFERENCES address (address_id);
--ALTER TABLE address ADD CONSTRAINT FK_ADDRESS_ON_STATION FOREIGN KEY (station_id) REFERENCES firestation (station_id);