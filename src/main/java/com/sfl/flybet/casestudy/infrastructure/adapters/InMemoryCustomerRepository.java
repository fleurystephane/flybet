package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Set<Customer> customers = new LinkedHashSet<>();
    public void add(Customer customer) {
        customers.add(customer);
    }

    public Set<Customer> all() {
        return customers;
    }

    @Override
    public Optional<Customer> byId(String customerId) {
        return customers.stream().filter(c -> c.getId().equals(customerId)).findFirst();
    }

    @Override
    public Optional<Customer> byPseudo(String pseudo) {
        return customers.stream().filter(c -> c.getPseudo().equals(pseudo)).findFirst();
    }
}
