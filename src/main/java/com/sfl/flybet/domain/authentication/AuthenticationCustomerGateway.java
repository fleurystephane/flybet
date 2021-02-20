package com.sfl.flybet.domain.authentication;



import com.sfl.flybet.domain.customer.model.Customer;

import java.util.Optional;

public interface AuthenticationCustomerGateway {
    void authenticate(Customer customer);

    Optional<Customer> currentCustomer();

    boolean isAdmin(Customer customer);
}
