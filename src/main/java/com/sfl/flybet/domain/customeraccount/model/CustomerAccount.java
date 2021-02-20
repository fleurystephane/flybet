package com.sfl.flybet.domain.customeraccount.model;


import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;

import java.util.Objects;

public class CustomerAccount {
    private final Long id;

    private Amount balance;

    public CustomerAccount(Long id, Amount balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public Amount getBalance() {
        return balance;
    }

    public void setBalance(Amount balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "CustomerAccount{" +
                "id='" + id + '\'' +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerAccount that = (CustomerAccount) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance);
    }

    public boolean hasBalanceLessThan(Amount amount) {
        return this.balance.getValue().compareTo(amount.getValue()) == -1;
    }

    public void paySubscription(Amount prize) {
        pay(prize);
    }

    public void payForProjectCreation(Amount prize) {
        pay(prize);
    }

    private void pay(Amount prize) {
        this.balance = new Amount(balance.getValue().subtract(prize.getValue()), Devise.CREDIT);
    }
}
