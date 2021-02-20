package com.sfl.flybet.domain.customer.model;

import java.util.Objects;

public class Customer {
    private final Long id;
    private final String pseudo;
    private int nbClaims;

    public Customer(Long id, String pseudo) {
        this.id = id;
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", pseudo='" + pseudo + '\'' +
                '}';
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

    public int getNbClaims() {
        return nbClaims;
    }

    public void setNbClaims(int nbClaims) {
        this.nbClaims = nbClaims;
    }
}
