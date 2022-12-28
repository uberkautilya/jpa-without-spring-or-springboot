package org.uberkautilya;

import org.uberkautilya.entity.Employee;

import javax.persistence.*;
import java.util.List;


public class JpaJPQLExample {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("readApp");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        /**
         * JPQL:
         * Instead of the table name, need to use the name of class annotated with @Entity
         * Instead of *, need to use an alias for the entity class. Also note that unless querying by ID, the result is a list
         * Without the second argument in the .createQuery() method, the result type would be a Query, which will give a list of objects
         */
        TypedQuery<Employee> entityManagerQuery = entityManager.createQuery(
                "select e from Employee e where e.name is not null and e.age < 25 order by e.age desc", Employee.class);
        List<Employee> resultList = entityManagerQuery.getResultList();
        resultList.forEach(System.out::println);
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        TypedQuery<Employee> entityManagerLikeQuery = entityManager.createQuery(
                "select e from Employee e where e.name like '%47' and e.age between -1 and 100", Employee.class);
        List<Employee> likeResultList = entityManagerLikeQuery.getResultList();
        likeResultList.forEach(System.out::println);
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        /**
         * The below caused an error as true was getting converted into 1 for some reason - Todo not yet figured out
         * select e from Employee e join AccessCard a on e.id = a.id where a.isActive= true - also does the similar function
         */
        TypedQuery<Employee> entityManagerJoinQuery = entityManager.createQuery(
                "select e from Employee e where e.accessCard.firmwareVersion = 32", Employee.class);
        List<Employee> joinQueryResultList = entityManagerJoinQuery.getResultList();
        joinQueryResultList.forEach(System.out::println);
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        /**
         * Custom Query Results: Single return value type
         */
        TypedQuery<String> entityManagerCustomQuery = entityManager.createQuery(
                "select e.name from Employee e where e.accessCard.firmwareVersion = 32", String.class);
        List<String> customQueryResultList = entityManagerCustomQuery.getResultList();
        customQueryResultList.forEach(System.out::println);
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        /**
         * Custom Query Results: Array of types return object - can query from multiple entities at the same time
         */
        TypedQuery<Object[]> query = entityManager.createQuery(
                "select e.name, e.age, e.dob, a.issueDate from Employee e, AccessCard a where e.accessCard.id = a.id and a.firmwareVersion = 32", Object[].class);
        // Will contain a list of object arrays
        List<Object[]> customTypeQueryResultList = query.getResultList();
        customTypeQueryResultList.forEach(x -> System.out.println("Name: " + x[0] + "| Age: " + x[1] + "| dob: " + x[2] + "| issueDate: " + x[3]));
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");


        /**
         * JPQL parameters to stub where clause:
         * "select e from Employee e where e.accessCard.firmwareVersion = " + minAge not a good idea due to SQL injection:
         * Malicious actor could inject a value to manipulate queries. Params allow only stubbing a single value: Safe
         */
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°JPQL parameters°°°°°°°");
        int minAge = 20;
        String queryStr = "select e from Employee e where e.age = :minAge";
        TypedQuery<Employee> entityManagerParametersQuery = entityManager.createQuery(queryStr, Employee.class);
        entityManagerParametersQuery.setParameter("minAge", minAge);
        List<Employee> jpqlParametersQueryResultList = entityManagerParametersQuery.getResultList();
        jpqlParametersQueryResultList.forEach(System.out::println);
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");


        /**
         * Calling a @NamedQuery 'emp name asc' defined on the entity Employee class
         */
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°Named Query°°°°°°°°°°°");
        int age = 21;
        TypedQuery<Employee> namedQuery = entityManager.createNamedQuery("emp name asc", Employee.class);
        namedQuery.setParameter("minAge", age);
        List<Employee> namedQueryResultList = namedQuery.getResultList();
        namedQueryResultList.forEach(System.out::println);
        System.out.println("°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°");

        entityManager.close();
        entityManagerFactory.close();
    }
}
