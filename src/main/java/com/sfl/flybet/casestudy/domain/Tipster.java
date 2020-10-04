package com.sfl.flybet.casestudy.domain;

public class Tipster {
    private final String id;
    private final String pseudo;

    public Tipster(String id, String pseudo){
        this.id = id;
        this.pseudo = pseudo;
    }

    @Override
    public String toString() {
        return "Tipster{" +
                "id='" + id + '\'' +
                ", pseudo='" + pseudo + '\'' +
                '}';
    }

    public String getPseudo() {
        return pseudo;
    }
}
