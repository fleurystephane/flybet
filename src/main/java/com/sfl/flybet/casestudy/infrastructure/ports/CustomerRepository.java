package com.sfl.flybet.casestudy.infrastructure.ports;

import com.sfl.flybet.casestudy.domain.Customer;

import java.util.Optional;
import java.util.Set;

public interface CustomerRepository {
    void add(Customer customer);

    Set<Customer> all();
    Optional<Customer> byId(String customerId);

    Optional<Customer> byPseudo(String pseudo);
}
