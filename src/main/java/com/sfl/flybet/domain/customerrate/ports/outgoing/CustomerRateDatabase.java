package com.sfl.flybet.domain.customerrate.ports.outgoing;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customerrate.model.CustomerRate;

import java.util.Set;

public interface CustomerRateDatabase {
    Set<CustomerRate> findByCustomerId(Long customerId);

    void add(CustomerRate customerRate);

    Amount getRateFor(Customer customer, int nbMonths);
}
