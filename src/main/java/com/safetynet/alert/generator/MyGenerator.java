package com.safetynet.alert.generator;


import com.safetynet.alert.model.Person;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class MyGenerator implements IdentifierGenerator, Configurable {


    @Override
    public Serializable generate(
            SharedSessionContractImplementor session, Object object)
            throws HibernateException {

//        String createdId = ((Person) object).getFirstName().toUpperCase() + ((Person) object).getLastName().toUpperCase();
//
//        List<Person> personList = session
//                .createQuery("from Person", Person.class)
//                .getResultList();
//
//        List<String> idList = personList.stream().map(Person::getId).collect(Collectors.toList());
//
//        if (idList.contains(createdId)) {
//            int idComplement = 1;
//            String createdIdWithComplement = createdId + idComplement;
//            while (idComplement != 0) {
//                if (idList.contains(createdIdWithComplement)) {
//                    createdIdWithComplement = createdId + idComplement;
//                    idComplement++;
//                } else {
//                    idComplement = 0;
//                }
//            }
//            return createdIdWithComplement;
//
//        } else {
//            return createdId;
//        }
        return ((Person) object).getFirstName().toUpperCase() + ((Person) object).getLastName().toUpperCase();
    }


    @Override
    public void configure(Type type, Properties properties,
                          ServiceRegistry serviceRegistry) throws MappingException {

    }
}

