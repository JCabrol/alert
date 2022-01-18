INSERT INTO person VALUES
('FIRSTNAME1LASTNAME1','firstName1', 'lastName1', null,'1111111111','person1@mail.com',null);
--(3,'FIRSTNAME3', 'LASTNAME3', 3 ,'3333333333','person3@mail.com',3);

--INSERT INTO firestation VALUES
--1),(2),(3);

INSERT INTO address VALUES
(1,'addressTest1','11111','cityTest1',null);
--(2,'ADDRESS12','CITY1','11111',1),
--(3,'ADDRESS13','CITY2','22222',1);
--(4,'ADDRESS21','CITY1','11111',2),
--(5,'ADDRESS22','CITY2','22222',2),
--(6,'ADDRESS31','CITY1','11111',3);
UPDATE person SET address_id = 1 WHERE id='FIRSTNAME1LASTNAME1';
--INSERT INTO medical_records VALUES
--(1,'12/11/1982'),
--(2,'15/04/2013'),
--(3,'13/08/2020');

--INSERT INTO allergy VALUES
--(1,'allergy1',1),
--(2,'allergy2',1),
--(3,'allergy3',2);

--INSERT INTO medication VALUES
--(1,'medication1',1),
--(2,'medication2',1),
--(3,'medication3',2);