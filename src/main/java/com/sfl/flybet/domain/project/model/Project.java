package com.sfl.flybet.domain.project.model;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.customer.model.Customer;

import java.time.LocalDate;

public class Project {
    private String projectTitle;
    private Amount bankrolInit;
    private LocalDate endProject;
    private String objectif;
    private Customer owner;
    private Long id;

    public Project() {}

    public Project(String projectTitle, Amount bankrolInit, String objectif, Customer owner, Long id) {
        this.projectTitle = projectTitle;
        this.bankrolInit = bankrolInit;
        this.objectif = objectif;
        this.owner = owner;
        this.id = id;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public Amount getBankrolInit() {
        return bankrolInit;
    }

    public void setBankrolInit(Amount bankrolInit) {
        this.bankrolInit = bankrolInit;
    }

    public LocalDate getEndProject() {
        return endProject;
    }

    public void setEndProject(LocalDate endProject) {
        this.endProject = endProject;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
