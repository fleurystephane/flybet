package com.sfl.flybet.casestudy.infrastructure.ports;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.CustomerRate;

import java.util.Set;

public interface CustomerRateRepository {
    Set<CustomerRate> findByCustomerId(String customerId);

    void add(CustomerRate customerRate);

    Amount getRateFor(Customer customer, int nbMonths);
}
