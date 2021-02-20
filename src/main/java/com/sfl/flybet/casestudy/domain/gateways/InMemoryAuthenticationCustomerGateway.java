package com.sfl.flybet.casestudy.domain.gateways;

import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.customer.model.Customer;

import java.util.Optional;

public class InMemoryAuthenticationCustomerGateway implements AuthenticationCustomerGateway {
    private Customer currentCustomer;

    public void authenticate(Customer customer) {
        currentCustomer = customer;
    }

    public Optional<Customer> currentCustomer() {
        return Optional.ofNullable(currentCustomer);
    }

    @Override
    public boolean isAdmin(Customer customer) {
        return customer != null && customer.getId().equals(0L);
    }
}
