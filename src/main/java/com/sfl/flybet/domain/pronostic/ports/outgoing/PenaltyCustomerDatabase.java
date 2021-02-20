package com.sfl.flybet.domain.pronostic.ports.outgoing;


import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.pronostic.model.Penalty;
import com.sfl.flybet.domain.pronostic.model.PenaltyIdentifier;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

import java.util.Set;

public interface PenaltyCustomerDatabase {
    PenaltyIdentifier addPenalty(Customer tipster, Pronostic pronostic);
    Set<Penalty> getPenaltiesFor(Customer customer);
}
