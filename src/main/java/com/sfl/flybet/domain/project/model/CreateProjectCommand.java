package com.sfl.flybet.domain.project.model;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.customer.model.Customer;

import java.time.LocalDate;

public class CreateProjectCommand {
    private final String projectTitle;
    private final Amount bankrolInit;
    private final LocalDate endProject;
    private final String objectif;
    private final Customer owner;

    public CreateProjectCommand(String projectTitle, Amount bankrolInit, LocalDate endProject, String objectif, Customer owner) {
        this.projectTitle = projectTitle;
        this.bankrolInit = bankrolInit;
        this.endProject = endProject;
        this.objectif = objectif;
        this.owner = owner;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public Amount getBankrolInit() {
        return bankrolInit;
    }

    public LocalDate getEndProject() {
        return endProject;
    }

    public String getObjectif() {
        return objectif;
    }

    public Customer getOwner() {
        return owner;
    }
}
