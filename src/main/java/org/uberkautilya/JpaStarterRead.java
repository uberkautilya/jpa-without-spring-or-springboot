package org.uberkautilya;

import org.uberkautilya.entity.EmailGroup;
import org.uberkautilya.entity.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigInteger;

public class JpaStarterRead {
    public static void main(String[] args) {
        //Creates a entityManagerFactory from the context pulling the persistence-unit of name 'myApp' from the persistence.xml
        EntityManagerFactory myAppEntityManagerFactory = Persistence.createEntityManagerFactory("readApp");
        EntityManager entityManager = myAppEntityManagerFactory.createEntityManager();

        readEmployeeWithPrimaryKey(entityManager);
        readEmailGroup(entityManager);

        entityManager.close();
        myAppEntityManagerFactory.close();
    }

    private static void readEmailGroup(EntityManager entityManager) {
        EmailGroup emailGroup = entityManager.find(EmailGroup.class, 6);
        System.out.println("Email Group: " + emailGroup);
        System.out.println("**** Accessing employee list in the fetched email group. @ManyToMany default fetchType is LAZY ****");
        System.out.println(emailGroup.getEmployeeList());
    }

    private static Employee readEmployeeWithPrimaryKey(EntityManager entityManager) {
        /**
         * The second argument is value of the primary key of the table
         * No need transactions while fetching. If the id doesn't exist - a null object is returned
         */
        Employee employee = entityManager.find(Employee.class, BigInteger.valueOf(1));
        System.out.println(employee.toStringWithNoAccessCard());

        /**
         * AccessCard is fetched lazily. Hibernate fetch query is fired only when it is accessed
         */
        System.out.println("***** About to access the accessCard. If LAZY fetchType, DB accessed at this point *****");
        System.out.println(employee);
        System.out.println("***** About to access the payStubList in employee. If LAZY fetchType, DB accessed at this point *****");
        System.out.println(employee.getPayStubList());
        return employee;
    }
}
