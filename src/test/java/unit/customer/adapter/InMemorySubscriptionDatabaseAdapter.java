package unit.customer.adapter;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.subscription.model.Subscription;
import com.sfl.flybet.domain.subscription.ports.outgoing.SubscriptionDatabase;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySubscriptionDatabaseAdapter implements SubscriptionDatabase {
    ConcurrentHashMap<Long, Subscription> subscriptions = new ConcurrentHashMap<>();

    @Override
    public boolean isSubscribed(Customer customer, Customer tipster) {
        return byCustomerId(customer.getId()).stream().anyMatch(subscription -> subscription.getSubscriptionTo().equals(tipster));
    }

    @Override
    public void add(Subscription subscription) {
        subscriptions.put(subscription.getSubscriberId(), subscription);
    }

    @Override
    public Set<Subscription> byCustomerId(Long customerId) {
        Set<Subscription> subscriptionsOfCustomer = new LinkedHashSet<>();
        subscriptions.entrySet().stream().filter(s -> s.getKey().equals(customerId)).map(Map.Entry::getValue).forEach(
                subscriptionsOfCustomer::add
        );
        return subscriptionsOfCustomer;
    }

    @Override
    public int getSubscribersCountFor(Customer customer) {
        return (int) subscriptions.entrySet().stream().filter(subscription -> subscription.getValue().getSubscriptionTo().equals(customer)).count();
    }
}
