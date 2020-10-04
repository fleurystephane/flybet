package com.sfl.flybet.casestudy.domain;

import java.math.BigDecimal;

public class CustomerRate {
    private final Customer customer;
    private final Amount prize;
    private final Integer nbMonths;

    public CustomerRate(Customer customer, Amount prize, Integer nbMonths) {

        this.customer = customer;
        this.prize = prize;
        this.nbMonths = nbMonths;
    }

    public String getCustomerId() {
        return customer.getId();
    }

    public Amount getPrize() {
        return prize;
    }

    public Integer getNbMonths() {
        return nbMonths;
    }
}
