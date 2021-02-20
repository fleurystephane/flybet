package com.sfl.flybet.domain.authentication.ports.incoming;


import com.sfl.flybet.domain.authentication.model.UserDescriptor;
import com.sfl.flybet.domain.customer.model.Customer;

import java.util.Optional;

public interface AuthenticationCustomer {
    UserDescriptor authenticate(Customer customer);

    Optional<Customer> currentCustomer();

    boolean isAdmin(Customer customer);
}
