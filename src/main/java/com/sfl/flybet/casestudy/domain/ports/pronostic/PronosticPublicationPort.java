package com.sfl.flybet.casestudy.domain.ports.pronostic;

import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.exceptions.AlreadyPublishedPronosticException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;

import java.math.BigDecimal;

public interface PronosticPublicationPort {
    void publish(Pronostic pronostic, String projectId) throws AlreadyPublishedPronosticException;

    void save(Pronostic pronostic, String projectId);

    void changeCoteValue(Pronostic pronostic, BigDecimal newCote) throws PronosticNotFoundException;
}
