package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Disapproval;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.infrastructure.ports.DisapprovalRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InMemoryDisapprovalRepository implements DisapprovalRepository {
    private final Set<Disapproval> disapprovals = new LinkedHashSet<>();

    @Override
    public long countDisapprouval(Pronostic pronostic) {
        return disapprovals.stream().filter(disapproval -> disapproval.getPronostic().equals(pronostic)).count();
    }

    @Override
    public void add(Disapproval disapproval) {
        disapprovals.add(disapproval);
    }

    @Override
    public int countDisapprovalFor(Customer customer) {
        long nbDisapprovalsForCustomer = disapprovals.stream().filter(disapproval -> disapproval.getAuthor().equals(customer)).count();

        return (int)nbDisapprovalsForCustomer;
    }

    @Override
    public Set<Disapproval> byCustomer(String customerId) {
        return disapprovals.stream().filter(
                disapproval -> disapproval.getAuthor().getId().equals(customerId)
        ).collect(Collectors.toSet());
    }

    @Override
    public void removeAllFor(Pronostic pronostic) {
        List<Disapproval> disapprovalsToRemove = disapprovals.stream().filter(disapproval -> disapproval.getPronostic().equals(pronostic)).collect(Collectors.toList());
        disapprovals.removeAll(disapprovalsToRemove);
    }
}
