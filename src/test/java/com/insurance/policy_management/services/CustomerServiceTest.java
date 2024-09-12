package com.insurance.policy_management.service;

import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Customer customer;

    @BeforeEach
    void setUp() {
        // Create and save a customer
        customer = new Customer();
        customer.setName("John Doe");
        customer.setAddress("123 Main St");
        customer.setContactInformation("john.doe@example.com");
        customer.setIdentificationDetails("ID12345");

        customer = customerRepository.save(customer);
    }

    @Test
    @Transactional
    void testCreateCustomer_ConcurrentThreads() throws InterruptedException, ExecutionException {
        // Use ExecutorService to simulate multiple threads concurrently trying to create a customer
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Callable<Customer> task = () -> customerService.createCustomer("Jane Doe", "456 Another St", "jane.doe@example.com", "ID67890");

        // Submit three concurrent tasks
        Future<Customer> future1 = executorService.submit(task);
        Future<Customer> future2 = executorService.submit(task);
        Future<Customer> future3 = executorService.submit(task);

        // Wait for tasks to complete
        Customer result1 = future1.get();
        Customer result2 = future2.get();
        Customer result3 = future3.get();

        // Assert that all customers are not null and persisted
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);

        // Verify that the customer was persisted in the database
        Optional<Customer> persistedCustomer = customerRepository.findByContactInformation("jane.doe@example.com");
        assertTrue(persistedCustomer.isPresent());

        // Ensure all transactions are flushed to the database
        entityManager.flush();

        // Shutdown the executor service
        executorService.shutdown();
    }

    @Test
    @Transactional
    void testUpdateCustomer_ConcurrentThreads() throws InterruptedException, ExecutionException {
        // Use ExecutorService to simulate multiple threads concurrently trying to update the same customer
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Callable<Customer> task = () -> {
            customer.setName("John Updated");
            return customerService.updateCustomer(customer.getId(), customer);
        };

        // Submit three concurrent tasks
        Future<Customer> future1 = executorService.submit(task);
        Future<Customer> future2 = executorService.submit(task);
        Future<Customer> future3 = executorService.submit(task);

        // Wait for tasks to complete
        Customer result1 = future1.get();
        Customer result2 = future2.get();
        Customer result3 = future3.get();

        // Assert that the customer was updated
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals("John Updated", result1.getName());
        assertEquals("John Updated", result2.getName());
        assertEquals("John Updated", result3.getName());

        // Verify that the update was persisted in the database
        Optional<Customer> persistedCustomer = customerRepository.findById(customer.getId());
        assertTrue(persistedCustomer.isPresent());
        assertEquals("John Updated", persistedCustomer.get().getName());

        // Ensure all transactions are flushed to the database
        entityManager.flush();

        // Shutdown the executor service
        executorService.shutdown();
    }

    @Test
    @Transactional
    void testDeleteCustomer_ConcurrentThreads() throws InterruptedException, ExecutionException {
        // Use ExecutorService to simulate multiple threads concurrently trying to delete the same customer
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Callable<Void> task = () -> {
            customerService.deleteCustomer(customer.getId());
            return null;
        };

        // Submit three concurrent tasks
        Future<Void> future1 = executorService.submit(task);
        Future<Void> future2 = executorService.submit(task);
        Future<Void> future3 = executorService.submit(task);

        // Wait for tasks to complete
        future1.get();
        future2.get();
        future3.get();

        // Verify that the customer was deleted
        Optional<Customer> deletedCustomer = customerRepository.findById(customer.getId());
        assertFalse(deletedCustomer.isPresent());

        // Ensure all transactions are flushed to the database
        entityManager.flush();

        // Shutdown the executor service
        executorService.shutdown();
    }
}
