package com.sfl.flybet.casestudy.infrastructure.ports;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Disapproval;
import com.sfl.flybet.casestudy.domain.Pronostic;

import java.util.Set;

public interface DisapprovalRepository {
    long countDisapprouval(Pronostic pronostic);

    void add(Disapproval disapproval);

    int countDisapprovalFor(Customer customer);

    Set<Disapproval> byCustomer(String customerId);

    void removeAllFor(Pronostic pronostic);
}
