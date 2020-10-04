package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Subscription;
import com.sfl.flybet.casestudy.infrastructure.ports.SubscriptionRepository;

import java.util.LinkedHashSet;
import java.util.Set;

public class InMemorySubscriptionRepository implements SubscriptionRepository {
    Set<Subscription> subscriptions = new LinkedHashSet<>();

    @Override
    public Set<Subscription> byCustomerId(String customerId) {
        Set<Subscription> subscriptionsOfCustomer = new LinkedHashSet<>();
        subscriptions.stream().filter(s -> s.getSubscriberId().equals(customerId)).forEach(
                subscriptionsOfCustomer::add
        );
        return subscriptionsOfCustomer;
    }

    @Override
    public void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    public int getSubscribersCountFor(Customer customer) {
        return (int) subscriptions.stream().filter(subscription -> subscription.getSubscriptionTo().equals(customer)).count();
    }

    @Override
    public String toString() {
        return "InMemorySubscriptionRepository{" +
                "subscriptions.size=" + subscriptions.size() +
                '}';
    }
}
