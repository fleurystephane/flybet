package com.sfl.flybet.casestudy.domain.ports.reliability;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.exceptions.*;

public interface PronosticReliabilityPort {
    int NB_CUSTOMER_DISAPPROVALS = 3;
    String NO_DISAPPROVAL_REMAINING = "No disapproval remaining.";
    String PRONOSTIC_IS_NOT_IN_DISAPPROVALABLE_STATUS = "Pronostic is not in disapprovalable status";
    String IMPOSSIBLE_TO_DISAPPROVE_OWN_PRONOSTIC = "Impossible to disapprove own pronostic";

    void disapprovePronostic(Customer customer, Pronostic pronostic) throws PronosticNotFoundException, DisapprovalableException, AlreadyDisapprovedException;

    int getDisapprovalRaminingCounter(Customer customer);

    void declarePronosticWon(String pronoId) throws PronosticNotFoundException, PronosticNotDecidableException;

    void changePronosticToLostAsAdmin(Customer customer, Pronostic pronostic) throws AuthorisationException, PronosticNotFoundException;
}
