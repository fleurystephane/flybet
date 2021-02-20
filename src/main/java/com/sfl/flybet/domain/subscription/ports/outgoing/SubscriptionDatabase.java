package com.sfl.flybet.domain.subscription.ports.outgoing;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.subscription.model.Subscription;
import com.sfl.flybet.domain.subscription.ports.incoming.SubscriptionManagement;

import java.util.Set;

public interface SubscriptionDatabase {
    public boolean isSubscribed(Customer customer, Customer tipster);
    void add(Subscription subscription);
    Set<Subscription> byCustomerId(Long customerId);
    int getSubscribersCountFor(Customer customer);
}
