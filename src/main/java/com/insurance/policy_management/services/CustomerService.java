package com.insurance.policy_management.services;

import com.insurance.policy_management.dto.ApiResponse;
import com.insurance.policy_management.exceptions.ResourceNotFoundException;
import com.insurance.policy_management.model.Customer;
import com.insurance.policy_management.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        Optional<Customer> customerExists = customerRepository.findByContactInformation(customer.getContactInformation());
        if (customerExists.isPresent()) {
            throw new ResourceNotFoundException("Customer already exists");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return Optional.ofNullable(customerRepository.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setName(customerDetails.getName());
        customer.setContactInformation(customerDetails.getContactInformation());
        customer.setAddress(customerDetails.getAddress());
        customer.setIdentificationDetails(customerDetails.getIdentificationDetails());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }
}
