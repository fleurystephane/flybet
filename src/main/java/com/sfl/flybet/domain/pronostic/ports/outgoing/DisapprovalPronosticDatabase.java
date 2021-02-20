package com.sfl.flybet.domain.pronostic.ports.outgoing;


import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.pronostic.model.Disapproval;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

import java.util.Set;

public interface DisapprovalPronosticDatabase {
    long countDisapprouval(Pronostic pronostic);

    void add(Disapproval disapproval);

    int countDisapprovalFor(Customer customer);

    Set<Disapproval> byCustomer(Long customerId);

    void removeAllFor(Pronostic pronostic);
}
