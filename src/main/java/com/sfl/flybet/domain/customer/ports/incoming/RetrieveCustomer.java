package com.sfl.flybet.domain.customer.ports.incoming;


import com.sfl.flybet.domain.customer.model.Customer;

import java.util.Optional;

public interface RetrieveCustomer {
    Optional<Customer> retrieveCustomerById(Long id);

    Optional<Customer> retrieveCustomerByPseudo(String pseudo);
}
