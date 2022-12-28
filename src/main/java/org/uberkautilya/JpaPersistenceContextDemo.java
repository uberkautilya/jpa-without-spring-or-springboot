package org.uberkautilya;

import org.uberkautilya.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.math.BigInteger;
import java.util.Date;

public class JpaPersistenceContextDemo {
    public static void main(String[] args) {
        /** Need an entityManager to persist - a service object from JPA that manages your entities (data holders)
         * Creates a entityManagerFactory from the context pulling the persistence-unit of name 'myApp' from the persistence.xml
         */
        EntityManagerFactory myAppEntityManagerFactory = Persistence.createEntityManagerFactory("myApp");
        EntityManager entityManager = myAppEntityManagerFactory.createEntityManager();

        persistEmployees(entityManager);
        detachedEmployeeCase(entityManager);

        entityManager.close();
        myAppEntityManagerFactory.close();

    }

    private static void persistEmployees(EntityManager entityManager) {
        /**
         * employee1 instance starts out in the 1. transient state: A new Java object created
         * Doesn't have any relation to the DB yet
         */
        Employee employee1 = getEmployee(null, "new Employee", "12345", new Date(), null);

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        System.out.println("**** Transaction started");

        /**
         * Once entityManager.persist(employee1) pushes the employee1 object into the 2. Managed state: JPA manages this entity now
         * A managed instance needn't have been saved to the DB.
         * A managed entity instance can also be created by JPA entityManager.find(Employee.class, BigInteger.valueOf(1)) - fetch from DB if so needed
         *
         */
        entityManager.persist(employee1);
        System.out.println("**** Employee persisted");

        /**
         * Fetch employee from the entityManager. Note here the employee has not yet been saved in the DB (transaction not yet committed)
         * i.e., entityManager is a primary cache for all entities.
         * Saving into DB is handled by JPA when absolutely necessary - when transaction is committed
         */
        Employee employeeFound = entityManager.find(Employee.class, BigInteger.valueOf(1));
        System.out.println("Employee Found: " + employeeFound + "\n Instances are same?: " + (employee1 == employeeFound));

        transaction.commit();
        System.out.println("**** Transaction committed");
    }

    private static Employee getEmployee(BigInteger id, String name, String ssn, Date dob, AccessCard accessCard) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setSsn(ssn);
        employee.setDob(dob);
        employee.setType(EmployeeType.CONTRACTOR);

        employee.setAccessCard(accessCard);
        System.out.println("**** Created Employee instance: " + employee);
        return employee;
    }

    private static Employee detachedEmployeeCase(EntityManager entityManager) {
        System.out.println("°°°°°°°°°°°°°°°°°°°°****** In detachedEmployeeCase method ******°°°°°°°°°°°°°°°°°°°°");
        EntityTransaction transaction = entityManager.getTransaction();
        /**
         * Would be in managed state
         */
        Employee employee = entityManager.find(Employee.class, BigInteger.valueOf(1));
        System.out.println(employee);
        employee.setName("Agent47");
        System.out.println("Edited Employee: " + entityManager.find(Employee.class, BigInteger.valueOf(1)));
        transaction.begin();
        /**
         * flush(): Force Moves the managed entries into the db, also deletes removed entities
         */
        entityManager.flush();
        transaction.commit();

        sleepTenSeconds();

        employee = entityManager.find(Employee.class, BigInteger.valueOf(1));
        System.out.println("Managed Employee: " + employee);
        /**
         * Detaches the object from the persistence context. Any changes performed on it wouldn't be carried to the DB
         * entityManager.merge(employee) can be used to take such a detached entity back to managed state
         * Detached entities may have changes not available in the DB. Hence such changes need to be merged
         */
        entityManager.detach(employee);
        employee.setName("Arya");
        System.out.println("Detached Employee: " + employee);
        System.out.println("Employee re-fetched: " + entityManager.find(Employee.class, BigInteger.valueOf(1)));
        return employee;
    }

    private static void sleepTenSeconds() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("Exception in thread sleep");
        }
    }
}
