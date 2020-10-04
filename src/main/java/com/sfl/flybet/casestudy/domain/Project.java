package com.sfl.flybet.casestudy.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Project {
    private String projectTitle;
    private Amount bankrol;
    private LocalDate endProject;
    private String objectif;
    private Customer owner;
    private String id;

    public Project(Customer owner, String projectTitle, Amount bankrol) {
        this.owner = owner;
        this.projectTitle = projectTitle;
        this.bankrol = bankrol;
    }

    public Project(Customer owner, String projectTitle) {
        this.owner = owner;
        this.projectTitle = projectTitle;
        bankrol = new Amount(BigDecimal.ZERO, Devise.EURO);
    }

    public Project(Customer owner, String projectTitle, Amount bkAmount, LocalDate dateFin) {
        this.owner = owner;
        this.projectTitle = projectTitle;
        this.bankrol = bkAmount;
        this.endProject = dateFin;
    }

    public void setBankrol(Amount bankrol) {
        this.bankrol = bankrol;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public Amount getBankrol() {
        return bankrol;
    }

    public LocalDate getEndProject() {
        return endProject;
    }

    public void setEndProject(LocalDate endProject) {
        this.endProject = endProject;
    }

    public void setObjectif(String objectif) { this.objectif = objectif; }

    public void setProjectTitle(String newTitle) { this.projectTitle = newTitle; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(projectTitle, project.projectTitle) &&
                Objects.equals(owner, project.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectTitle, owner);
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectTitle='" + projectTitle + '\'' +
                ", bankrol=" + bankrol +
                ", endProject=" + endProject +
                '}';
    }

    public String getObjectif() {
        return objectif;
    }

    public Customer getOwner() {
        return owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
