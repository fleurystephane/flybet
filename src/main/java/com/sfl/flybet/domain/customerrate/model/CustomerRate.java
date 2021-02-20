package com.sfl.flybet.domain.customerrate.model;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.customer.model.Customer;

public class CustomerRate {
    private final Customer customer;
    private final Amount prize;
    private final Integer nbMonths;

    public CustomerRate(Customer customer, Amount prize, Integer nbMonths) {
        this.customer = customer;
        this.prize = prize;
        this.nbMonths = nbMonths;
    }

    public Long getCustomerId() {
        return customer.getId();
    }

    public Amount getPrize() {
        return prize;
    }

    public Integer getNbMonths() {
        return nbMonths;
    }
}
