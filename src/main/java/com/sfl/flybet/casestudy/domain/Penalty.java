package com.sfl.flybet.casestudy.domain;

public class Penalty {
    private Customer owner;
    private Pronostic pronostic;

    public Penalty(Customer owner, Pronostic pronostic) {
        this.owner = owner;
        this.pronostic = pronostic;
    }

    public Customer getOwner() {
        return owner;
    }

    public Pronostic getPronostic() {
        return pronostic;
    }
}
