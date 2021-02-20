package com.sfl.flybet.domain.customer;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.incoming.RetrieveCustomer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;

import java.util.Optional;


public class CustomerFacade implements RetrieveCustomer {

    private final CustomerDatabase database;


    public CustomerFacade(CustomerDatabase db) {
        database = db;
    }

    @Override
    public Optional<Customer> retrieveCustomerById(Long id) {
        return database.getCustomerById(id);
    }

    @Override
    public Optional<Customer> retrieveCustomerByPseudo(String pseudo) {
        return database.getCustomerByPseudo(pseudo);
    }
}
