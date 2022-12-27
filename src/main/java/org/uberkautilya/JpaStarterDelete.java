package org.uberkautilya;

import org.uberkautilya.entity.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.math.BigInteger;

public class JpaStarterDelete {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("readApp");
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        /**
         * Without cascade enabled, constraint violations occur as employee_id is referred in payStub table if there are entries
         * This is because deleting from payStub table involves data loss, unlike a simple delete from a relationship table
         * In summary, delete removes entries from relationship tables, while in other entity tables - it throws JdbcSQLIntegrityConstraintViolationException
         */
        Employee employee = entityManager.find(Employee.class, BigInteger.valueOf(1));
        transaction.begin();

        entityManager.remove(employee);

        transaction.commit();
        entityManager.close();
        factory.close();
    }
}
