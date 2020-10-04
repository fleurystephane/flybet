package com.sfl.flybet.casestudy.domain.ports.customer;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.exceptions.SoldeInsuffisantException;

import java.time.LocalDate;

public interface SubscribeCustomerPort {
    void subscribe(Customer subscriber, Customer customerToSubscribeTo, LocalDate subscriptionDate, int nbMonths) throws SoldeInsuffisantException;

    Amount getBestPrize(Customer customerToSubscribeTo, int nbMonths);
}
