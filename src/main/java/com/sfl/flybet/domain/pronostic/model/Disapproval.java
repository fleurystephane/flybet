package com.sfl.flybet.domain.pronostic.model;


import com.sfl.flybet.domain.customer.model.Customer;

public class Disapproval {
    private Pronostic pronostic;
    private Customer author;

    public Disapproval(Pronostic pronostic, Customer author){
        this.pronostic = pronostic;
        this.author = author;
    }
    public Pronostic getPronostic() {
        return pronostic;
    }

    public Customer getAuthor() {
        return author;
    }
}
