package com.sfl.flybet.domain.pronostic.ports.incoming;

import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotDecidableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

public interface ManageResultPronostic {
    void declarePronosticWon(Long pronoId) throws PronosticNotFoundException, PronosticNotDecidableException;

    void changePronosticToLostAsAdmin(Customer customer, Pronostic pronostic) throws AuthorizationException, PronosticNotFoundException;
}
