package org.uberkautilya;

import org.uberkautilya.entity.AccessCard;
import org.uberkautilya.entity.Employee;
import org.uberkautilya.entity.EmployeeType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.math.BigInteger;
import java.util.Date;

public class JpaStarterWrite {
    public static void main(String[] args) {

        //Need an entityManager to persist - a service object from JPA that manages your entities (data holders)
        //Creates a entityManagerFactory from the context pulling the persistence-unit of name 'myApp' from the persistence.xml
        EntityManagerFactory myAppEntityManagerFactory = Persistence.createEntityManagerFactory("myApp");
        EntityManager entityManager = myAppEntityManagerFactory.createEntityManager();

        createEmployees(entityManager);
        Employee employee = readEmployeeWithPrimaryKey(entityManager);
        updateEmployee(entityManager, employee);
        if (true) {
            deleteEmployee(entityManager, employee);
        }

//        createAccessCards(entityManager);

        entityManager.close();
        myAppEntityManagerFactory.close();

    }

    private static void createAccessCards(EntityManager entityManager) {
        //Without one-to-one mapping, there would be no link between employee instance and the access cards.
        //Even if linked via the accessCard.getId() it would not be a foreign key relationship
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        AccessCard accessCard = new AccessCard(new Date(), true, "0.1");
        entityManager.persist(accessCard);

        transaction.commit();
    }

    private static void deleteEmployee(EntityManager entityManager, Employee employee) {
        //The entity to delete can be fetched using the ID, which can then be passed into this method to execute delete on the DB
        EntityTransaction transaction = entityManager.getTransaction();
        //The below 1 line has no effect on the result as ID is the field used for delete query
        employee.setSsn("Some random value");

        //Delete using JPA needs the object containing the ID - which has to be fetched with a DB call
        //A new Employee created where the ID is set WON'T WORK - as JPA context will not know the instance being removed
        //Because of the way the entities get attached to the context - entity lifecycle (Attaching and Detaching entities)

        transaction.begin();
        entityManager.remove(employee);
        transaction.commit();
    }

    private static void updateEmployee(EntityManager entityManager, Employee employee) {
        //Since the entity exists currently in the DB (via the primary key), JPA runs an update instead of an insert when entityManager.persist() is executed
        //If the id isn't available in the Java object, JPA would interpret it as a case for an insert entry
        EntityTransaction transaction = entityManager.getTransaction();
        employee.setAge(44);
        employee.setType(EmployeeType.FULL_TIME);

        transaction.begin();
        entityManager.persist(employee);
        transaction.commit();
    }

    private static void createEmployees(EntityManager entityManager) {
        AccessCard accessCard = getAccessCard();
        Employee employee1 = getEmployee(null, "Kautilya1", "sss", null, accessCard);
        Employee employee2 = getEmployee(null, "Kautilya2", "ssn", null, accessCard);

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(employee1);
        entityManager.persist(employee2);
        //The order of insertion doesn't matter - as it is within a transaction and managed by JPA?
        //Also, when there is no cascade strategy, the save of the accessCard needs to be explicit
        entityManager.persist(accessCard);

        transaction.commit();
    }

    private static AccessCard getAccessCard() {
        AccessCard accessCard = new AccessCard();
        accessCard.setIssueDate(new Date());
        accessCard.setActive(true);
        accessCard.setFirmwareVersion("0.0.1");
        return accessCard;
    }

    private static Employee readEmployeeWithPrimaryKey(EntityManager entityManager) {
        //The second argument is value of the primary key of the table
        //No need transactions while fetching. If the id doesn't exist - a null object is returned
        Employee employee = entityManager.find(Employee.class, BigInteger.valueOf(2));
        System.out.println(employee);
        return employee;
    }

    private static Employee getEmployee(BigInteger id, String name, String ssn, Date dob, AccessCard accessCard) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setSsn(ssn);
        employee.setDob(dob);
        employee.setType(EmployeeType.CONTRACTOR);

        employee.setAccessCard(accessCard);
        return employee;
    }
}
