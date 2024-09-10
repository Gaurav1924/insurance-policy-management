package com.insurance.policy_management.services;

import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @CachePut(value = "customer", key = "#customer.id")
    public Customer createCustomer(Customer customer) {
        Optional<Customer> customerExists = customerRepository.findByContactInformation(customer.getContactInformation());
        if (customerExists.isPresent()) {
            throw new ResourceNotFoundException("Customer already exists");
        }
        return customerRepository.save(customer);
    }

    @Cacheable(value = "customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Cacheable(value = "customer", key = "#id")
    public Optional<Customer> getCustomerById(Long id) {
        return Optional.ofNullable(customerRepository.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @CachePut(value = "customer", key = "#id")
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setName(customerDetails.getName());
        customer.setContactInformation(customerDetails.getContactInformation());
        customer.setAddress(customerDetails.getAddress());
        customer.setIdentificationDetails(customerDetails.getIdentificationDetails());
        return customerRepository.save(customer);
    }

    @CacheEvict(value = "customer", key = "#id")
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }
}
