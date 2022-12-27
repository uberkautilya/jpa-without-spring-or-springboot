package org.uberkautilya;

import org.uberkautilya.entity.EmailGroup;
import org.uberkautilya.entity.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.math.BigInteger;

public class JpaStarterUpdate {
    public static void main(String[] args) {
        //Since table definitions are kept stable, <property name="hibernate.hbm2ddl.auto" value="none"/> is valid in an update of record
        EntityManagerFactory myAppEntityManagerFactory = Persistence.createEntityManagerFactory("readApp");
        EntityManager entityManager = myAppEntityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        Employee employee = entityManager.find(Employee.class, BigInteger.valueOf(2));
        EmailGroup emailGroup = entityManager.find(EmailGroup.class, 7);
        employee.addEmailGroup(emailGroup);
        emailGroup.addEmployee(employee);

        transaction.begin();

        entityManager.persist(employee);
        entityManager.persist(emailGroup);

        transaction.commit();
        entityManager.close();
        myAppEntityManagerFactory.close();
    }
}
