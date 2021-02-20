package com.sfl.flybet.domain.authentication.model;

public class UserDescriptor {
    private final String pseudo;
    private final Long id;

    public UserDescriptor(String pseudo, Long id) {
        this.pseudo = pseudo;
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public Long getId() {
        return id;
    }
}
