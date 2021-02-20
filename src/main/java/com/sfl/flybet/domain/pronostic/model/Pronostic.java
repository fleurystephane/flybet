package com.sfl.flybet.domain.pronostic.model;


import java.math.BigDecimal;
import java.util.Objects;

public class Pronostic {
    private Long id;
    private StatusProno statusProno;
    private BigDecimal cote;
    private BigDecimal mise;
    private String uniteMise;

    public Pronostic() {
    }

    public Pronostic(Long id) {
        this.id = id;
        this.statusProno = StatusProno.DRAFT;
    }

    public Pronostic(Long id, StatusProno statusProno, BigDecimal cote, BigDecimal mise, String uniteMise) {
        this.id = id;
        this.statusProno = statusProno;
        this.cote = cote;
        this.mise = mise;
        this.uniteMise = uniteMise;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusProno getStatusProno() {
        return statusProno;
    }

    public void setStatusProno(StatusProno statusProno) {
        this.statusProno = statusProno;
    }

    public BigDecimal getCote() {
        return cote;
    }

    public void setCote(BigDecimal cote) {
        this.cote = cote;
    }

    public BigDecimal getMise() {
        return mise;
    }

    public void setMise(BigDecimal mise) {
        this.mise = mise;
    }

    public String getUniteMise() {
        return uniteMise;
    }

    public void setUniteMise(String uniteMise) {
        this.uniteMise = uniteMise;
    }

    public boolean isDisapprovalable() {
        return isDecided() && !isCertified();
    }

    public boolean isCertified() {
        return statusProno.equals(StatusProno.CERTIFIED_LOST) || statusProno.equals(StatusProno.CERTIFIED_WON);
    }
    public boolean isCounted() {
        return statusProno != StatusProno.DRAFT;
    }
    public boolean isDecidedWon() {
        return this.statusProno == StatusProno.WON;
    }
    public boolean isDraft() {
        return statusProno == StatusProno.DRAFT;
    }

    public boolean isPublished() {
        return statusProno != StatusProno.DRAFT && statusProno != StatusProno.WON && statusProno != StatusProno.LOST &&
                statusProno != StatusProno.CERTIFIED_LOST && statusProno != StatusProno.CERTIFIED_WON;
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
                statusProno == pronostic.statusProno &&
                Objects.equals(cote, pronostic.cote) &&
                Objects.equals(mise, pronostic.mise) &&
                Objects.equals(uniteMise, pronostic.uniteMise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statusProno, cote, mise, uniteMise);
    }

    @Override
    public String toString() {
        return "Pronostic{" +
                "id=" + id +
                ", statusProno=" + statusProno +
                ", cote=" + cote +
                ", mise=" + mise +
                ", uniteMise='" + uniteMise + '\'' +
                '}';
    }
}
