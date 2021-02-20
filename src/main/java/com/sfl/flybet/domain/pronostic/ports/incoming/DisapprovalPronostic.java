package com.sfl.flybet.domain.pronostic.ports.incoming;

import com.sfl.flybet.casestudy.domain.exceptions.AlreadyDisapprovedException;
import com.sfl.flybet.casestudy.domain.exceptions.DisapprovalableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

public interface DisapprovalPronostic {

    void disapprovePronostic(Customer customer, Pronostic pronostic) throws PronosticNotFoundException, DisapprovalableException, AlreadyDisapprovedException;

    int getDisapprovalRaminingCounter(Customer customer);
}
