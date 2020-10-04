package com.sfl.flybet.casestudy.infrastructure.ports;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Subscription;

import java.util.Set;

public interface SubscriptionRepository {
    Set<Subscription> byCustomerId(String customerId);

    void add(Subscription subscription);

    int getSubscribersCountFor(Customer customer);
}
