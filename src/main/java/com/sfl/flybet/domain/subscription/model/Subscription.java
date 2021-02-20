package com.sfl.flybet.domain.subscription.model;


import com.sfl.flybet.domain.customer.model.Customer;

import java.time.LocalDate;
import java.util.Objects;

public class Subscription {

    private final Customer subscriberCustomer;
    private LocalDate subscriptionDate;
    private final int nbMonths;
    private Customer customerToSubscribeTo;

    public Subscription(Customer customer, Customer customerToSubscribeTo,
                        LocalDate ld, int nbMonths) {
        this.subscriberCustomer = customer;
        this.customerToSubscribeTo = customerToSubscribeTo;
        this.subscriptionDate = ld;
        this.nbMonths = nbMonths;
    }


    public Customer getSubscriptionTo() {
        return customerToSubscribeTo;
    }

    public Customer getSubscriberCustomer() {
        return subscriberCustomer;
    }
    public Long getSubscriberId() {
        return subscriberCustomer.getId();
    }

    public int getNbMonths() {
        return nbMonths;
    }

    public LocalDate getSubscriptionDate() {
        return subscriptionDate;
    }

    public boolean isActiveNow(LocalDate now) {
        return this.subscriptionDate.isBefore(LocalDate.now()) &&
                this.subscriptionDate.plusMonths(this.nbMonths).isAfter(now);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;
        Subscription that = (Subscription) o;
        return nbMonths == that.nbMonths &&
                subscriberCustomer.equals(that.subscriberCustomer) &&
                subscriptionDate.equals(that.subscriptionDate) &&
                customerToSubscribeTo.equals(that.customerToSubscribeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriberCustomer, subscriptionDate, nbMonths, customerToSubscribeTo);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "subscriberCustomer=" + subscriberCustomer +
                ", subscriptionDate=" + subscriptionDate +
                ", nbMonths=" + nbMonths +
                ", customerToSubscribeTo=" + customerToSubscribeTo +
                '}';
    }
}