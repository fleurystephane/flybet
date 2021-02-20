package unit.pronostic.adapter;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.pronostic.model.Penalty;
import com.sfl.flybet.domain.pronostic.model.PenaltyIdentifier;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PenaltyCustomerDatabase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPenaltyCustomerDatabaseAdapter implements PenaltyCustomerDatabase {
    ConcurrentHashMap<Long, Penalty> penalties = new ConcurrentHashMap<>();
    @Override
    public PenaltyIdentifier addPenalty(Customer tipster, Pronostic pronostic) {
        Penalty peno = new Penalty(tipster, pronostic);
        Long newId;
        if(penalties.isEmpty())
            newId = 0L;
        else {
            newId = penalties.keySet().stream().mapToLong(k -> k).max().getAsLong() + 1;
        }
        penalties.put(newId, peno);
        return new PenaltyIdentifier(newId);
    }

    @Override
    public Set<Penalty> getPenaltiesFor(Customer customer) {
        Set<Penalty> penos = new HashSet<>();
        penalties.values().stream().filter(
                penalty -> penalty.getOwner().getId().equals(customer.getId())).forEach(penos::add);
        return penos;
    }
}
