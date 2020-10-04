package com.sfl.flybet.casestudy.domain.gateways;

import com.sfl.flybet.casestudy.domain.Customer;

import java.util.Optional;

public interface AuthenticationCustomerGateway {
    void authenticate(Customer customer);

    Optional<Customer> currentCustomer();

    boolean isAdmin(Customer customer);
}
