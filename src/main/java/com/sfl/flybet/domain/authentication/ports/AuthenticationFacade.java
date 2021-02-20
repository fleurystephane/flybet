package com.sfl.flybet.domain.authentication.ports;

import com.sfl.flybet.domain.authentication.model.UserDescriptor;
import com.sfl.flybet.domain.authentication.ports.incoming.AuthenticationCustomer;
import com.sfl.flybet.domain.authentication.ports.outgoing.AuthenticationDatabase;
import com.sfl.flybet.domain.customer.model.Customer;

import java.util.Optional;

public class AuthenticationFacade implements AuthenticationCustomer {
    private final AuthenticationDatabase database;

    public AuthenticationFacade(AuthenticationDatabase database) {
        this.database = database;
    }

    @Override
    public UserDescriptor authenticate(Customer customer) {
        return null;
    }

    @Override
    public Optional<Customer> currentCustomer() {
        return Optional.empty();
    }

    @Override
    public boolean isAdmin(Customer customer) {
        return false;
    }
}
