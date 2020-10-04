package com.sfl.flybet.casestudy.domain.repositories;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Penalty;
import com.sfl.flybet.casestudy.domain.Pronostic;

import java.util.Set;

public interface PenaltyRepository {
    void addPenalty(Customer tipster, Pronostic pronostic);

    Set<Penalty> getPenaltiesFor(Customer customer);
}
