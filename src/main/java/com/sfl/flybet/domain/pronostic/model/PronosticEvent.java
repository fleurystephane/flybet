package com.sfl.flybet.domain.pronostic.model;

public class PronosticEvent {
    private final Pronostic pronostic;

    public PronosticEvent(Pronostic pronostic) {
        this.pronostic = pronostic;
    }

    public Pronostic getPronostic() {
        return pronostic;
    }
}
