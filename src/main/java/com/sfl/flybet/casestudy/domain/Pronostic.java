package com.sfl.flybet.casestudy.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Pronostic {

    private String id;
    private StatusProno statusProno;
    private BigDecimal cote;

    public Pronostic(String pronoId) {
        statusProno = StatusProno.DRAFT;
        id = pronoId;
    }

    public String getId() {
        return id;
    }


    public boolean isDecided() {
        return statusProno == StatusProno.WON || statusProno == StatusProno.LOST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pronostic pronostic = (Pronostic) o;
        return Objects.equals(id, pronostic.id) &&
                statusProno == pronostic.statusProno;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statusProno);
    }

    @Override
    public String toString() {
        return "Pronostic{" +
                "id='" + id + '\'' +
                ", statusProno=" + statusProno +
                ", cote=" + cote +
                '}';
    }

    public boolean isDraft() {
        return statusProno == StatusProno.DRAFT;
    }

    public boolean isPublished() {
        return statusProno != StatusProno.DRAFT && statusProno != StatusProno.WON && statusProno != StatusProno.LOST &&
                statusProno != StatusProno.CERTIFIED_LOST && statusProno != StatusProno.CERTIFIED_WON;
    }

    public void setStatus(StatusProno statusProno) {
        this.statusProno = statusProno;
    }

    public void setCote(BigDecimal cote) {
        this.cote = cote;
    }

    public BigDecimal getCote() {
        return cote;
    }

    public boolean isDisapprovalable() {
        return isDecided() && !isCertified();
    }

    public boolean isCertified() {
        return statusProno.equals(StatusProno.CERTIFIED_LOST) || statusProno.equals(StatusProno.CERTIFIED_WON);
    }

    public StatusProno getStatusProno() {
        return statusProno;
    }

    public boolean isDecidedWon() {
        return this.statusProno == StatusProno.WON;
    }
}
