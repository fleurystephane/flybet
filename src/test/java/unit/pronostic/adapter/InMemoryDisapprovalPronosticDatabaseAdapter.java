package unit.pronostic.adapter;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.pronostic.model.Disapproval;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.ports.outgoing.DisapprovalPronosticDatabase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDisapprovalPronosticDatabaseAdapter implements DisapprovalPronosticDatabase {
    ConcurrentHashMap<Long, Set<Disapproval>> disapprovalsByPronoId = new ConcurrentHashMap<>();
    @Override
    public long countDisapprouval(Pronostic pronostic) {
        if(null == disapprovalsByPronoId.get(pronostic.getId()))
            return 0;
        return disapprovalsByPronoId.get(pronostic.getId()).size();
    }

    @Override
    public void add(Disapproval disapproval) {
        Set<Disapproval> currentDisapprovals = disapprovalsByPronoId.get(disapproval.getPronostic().getId());
        if(null == currentDisapprovals){
            Set<Disapproval> newSet = new HashSet<>();
            newSet.add(disapproval);
            disapprovalsByPronoId.put(disapproval.getPronostic().getId(), newSet);
        }
        else{
            currentDisapprovals.add(disapproval);
            disapprovalsByPronoId.put(disapproval.getPronostic().getId(), currentDisapprovals);
        }
    }

    @Override
    public int countDisapprovalFor(Customer customer) {
        int count = 0;
        for(Map.Entry<Long, Set<Disapproval>> entry : disapprovalsByPronoId.entrySet()){
            count += entry.getValue().stream().filter(disapproval -> disapproval.getAuthor().getId().equals(customer.getId())).count();
        }
        return count;
    }

    @Override
    public Set<Disapproval> byCustomer(Long customerId) {
        Set<Disapproval> disapprovalsForCustomer = new HashSet<>();
        for(Map.Entry<Long, Set<Disapproval>> entry : disapprovalsByPronoId.entrySet()){
            for (Disapproval disapp : entry.getValue()) {
                if(disapp.getAuthor().getId().equals(customerId)){
                    disapprovalsForCustomer.add(disapp);
                }
            }
        }
        return disapprovalsForCustomer;
    }

    @Override
    public void removeAllFor(Pronostic pronostic) {
        disapprovalsByPronoId.put(pronostic.getId(), new HashSet<>());
    }
}
