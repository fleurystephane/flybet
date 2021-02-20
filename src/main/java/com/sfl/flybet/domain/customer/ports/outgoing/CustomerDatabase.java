package com.sfl.flybet.domain.customer.ports.outgoing;

import com.sfl.flybet.domain.customer.model.Customer;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface CustomerDatabase {
    void add(Customer customer);

    Collection<Customer> all();
    Optional<Customer> getCustomerById(Long id);
    Optional<Customer> getCustomerByPseudo(String pseudo);
}
