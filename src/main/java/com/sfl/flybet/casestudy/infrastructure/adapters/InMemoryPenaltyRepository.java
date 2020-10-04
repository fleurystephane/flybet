package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Penalty;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.repositories.PenaltyRepository;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class InMemoryPenaltyRepository implements PenaltyRepository {
    Set<Penalty> penalties = new LinkedHashSet<>();

    @Override
    public void addPenalty(Customer tipster, Pronostic pronostic) {
        penalties.add(new Penalty(tipster, pronostic));
    }

    @Override
    public Set<Penalty> getPenaltiesFor(Customer customer) {
        return penalties.stream().filter(penalty -> penalty.getOwner().getId().equals(customer.getId())).collect(Collectors.toSet());
    }
}
