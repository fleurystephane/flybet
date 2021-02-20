package com.sfl.flybet.domain.pronostic.ports.incoming;


import com.sfl.flybet.casestudy.domain.exceptions.AlreadyPublishedPronosticException;
import com.sfl.flybet.casestudy.domain.exceptions.BankrolInsufficientException;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

public interface PublishPronostic {
    void publish(Pronostic pronostic, Long projectId) throws AlreadyPublishedPronosticException, BankrolInsufficientException, ProjectNotFoundException;
}
