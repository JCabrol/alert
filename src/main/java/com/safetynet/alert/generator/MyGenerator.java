package com.safetynet.alert.generator;


import com.safetynet.alert.model.Person;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public class MyGenerator implements IdentifierGenerator, Configurable {


    @Override
    public Serializable generate(
            SharedSessionContractImplementor session, Object object)
            throws HibernateException {
            String firstName = ((Person) object).getFirstName().toUpperCase();
            String lastName = ((Person) object).getLastName().toUpperCase();
            String createdId = firstName + lastName;
//            Query query = session.createQuery("FROM Person");
//            List<Person> allPersons = query.getResultList();
//            long numberOfHomonyms = allPersons
//                    .stream()
//                    .filter(person -> person.getFirstName().equals(firstName))
//                    .filter(person -> person.getLastName().equals(lastName))
//                    .count();
//            if (numberOfHomonyms != 0) {
//                createdId = createdId + (numberOfHomonyms + 1);
//            }
            return createdId;
    }

        @Override
        public void configure (Type type, Properties properties,
                ServiceRegistry serviceRegistry) throws MappingException {

        }
    }

