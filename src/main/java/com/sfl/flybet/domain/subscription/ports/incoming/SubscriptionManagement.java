package com.sfl.flybet.domain.subscription.ports.incoming;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.project.exceptions.SoldeInsuffisantException;

import java.time.LocalDate;

public interface SubscriptionManagement {
    void subscribe(Customer subscriber, Customer customerToSubscribeTo, LocalDate subscriptionDate, int nbMonths) throws SoldeInsuffisantException;

    boolean isSubscribed(Customer customer, Customer tipster);


}
