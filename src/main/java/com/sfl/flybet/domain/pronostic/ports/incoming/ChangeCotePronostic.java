package com.sfl.flybet.domain.pronostic.ports.incoming;

import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

import java.math.BigDecimal;

public interface ChangeCotePronostic {
    void changeCoteValue(Pronostic pronostic, BigDecimal newCote) throws PronosticNotFoundException;
}
