package com.sfl.flybet.casestudy.domain;

import java.util.Objects;

public class Customer {
    private final String id;
    private final String pseudo;

    public Customer(String id, String pseudo) {
        this.id = id;
        this.pseudo = pseudo;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", pseudo='" + pseudo + '\'' +
                '}';
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                Objects.equals(pseudo, customer.pseudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pseudo);
    }
}
