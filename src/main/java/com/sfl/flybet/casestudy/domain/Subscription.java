package com.sfl.flybet.casestudy.domain;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
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
    public String getSubscriberId() {
        return subscriberCustomer.getId();
    }

    public int getNbMonths() {
        return nbMonths;
    }

    public LocalDate getSubscriptionDate() {
        return subscriptionDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return nbMonths == that.nbMonths &&
                Objects.equals(subscriberCustomer, that.subscriberCustomer) &&
                Objects.equals(customerToSubscribeTo, that.customerToSubscribeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriberCustomer, nbMonths, customerToSubscribeTo);
    }

    public boolean isActiveNow(LocalDate now) {
        return this.subscriptionDate.isBefore(LocalDate.now()) &&
                this.subscriptionDate.plusMonths(this.nbMonths).isAfter(now);
    }
}
