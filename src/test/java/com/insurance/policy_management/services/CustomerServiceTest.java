package com.insurance.policy_management.services;

import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.repository.CustomerRepository;
import com.insurance.policy_management.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setContactInformation("customer1@example.com");

        when(redissonClient.getLock(anyString())).thenReturn(lock);
    }

    @Test
    void testCreateCustomer_SingleThread_Success() throws Exception {
        // Mocking lock acquisition
        when(lock.tryLock(10, TimeUnit.SECONDS)).thenReturn(true);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        customerService.createCustomer(customer);

        // Assert
        verify(customerRepository, times(1)).save(customer);
        verify(lock, times(1)).unlock(); // Ensure lock is released
    }

    @Test
    void testCreateCustomer_MultipleThreads_OnlyOneSucceeds() throws InterruptedException {
        // Mocking lock acquisition
        when(lock.tryLock(10, TimeUnit.SECONDS)).thenReturn(true).thenReturn(false); // First thread gets the lock, second fails

        // Simulate multiple threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Runnable task1 = () -> customerService.createCustomer(customer);
        Runnable task2 = () -> customerService.createCustomer(customer);

        executor.submit(task1);
        executor.submit(task2);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Assert
        verify(lock, times(2)).tryLock(10, TimeUnit.SECONDS); // Both threads tried to lock
        verify(lock, times(1)).unlock(); // Only the thread that got the lock releases it
    }

    @Test
    void testCreateCustomer_LockNotAcquired_ThrowsException() throws Exception {
        // Mock lock acquisition failure
        when(lock.tryLock(10, TimeUnit.SECONDS)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> customerService.createCustomer(customer));
        verify(lock, never()).unlock(); // Lock shouldn't be released since it was never acquired
    }
}
