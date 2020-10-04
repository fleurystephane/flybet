package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;

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
        return customer != null && customer.getId().equals("ADM");
    }
}
