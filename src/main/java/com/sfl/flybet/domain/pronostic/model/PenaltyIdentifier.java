package com.sfl.flybet.domain.pronostic.model;

public class PenaltyIdentifier {
    private Long id;

    public PenaltyIdentifier(Long newId) {
        id = newId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
