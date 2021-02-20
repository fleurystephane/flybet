package unit.customer.adapter;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customerrate.model.CustomerRate;
import com.sfl.flybet.domain.customerrate.ports.outgoing.CustomerRateDatabase;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryCustomerRateDatabaseAdapter implements CustomerRateDatabase {
    ConcurrentHashMap<Long, CustomerRate> rates = new ConcurrentHashMap<>();
    @Override
    public Set<CustomerRate> findByCustomerId(Long customerId) {
        Stream<CustomerRate> customerRateStream =
                rates.entrySet().stream().filter(customerRate -> customerRate.getValue().getCustomerId().equals(customerId)).map(Map.Entry::getValue).distinct();
        return customerRateStream.collect(Collectors.toSet());
    }

    @Override
    public void add(CustomerRate customerRate) {
        Long newId;
        if(rates.isEmpty()){
            newId = 0L;
        }
        else {
            newId = rates.keySet().stream().mapToLong(k -> k).max().getAsLong() + 1;
        }
        rates.put(newId, customerRate);
    }

    @Override
    public Amount getRateFor(Customer customerToSubscribeTo, int nbMonths) {
        Set<CustomerRate> customerRates = findByCustomerId(customerToSubscribeTo.getId());
        Optional<CustomerRate> result = customerRates.stream().filter(rate -> rate.getNbMonths() == nbMonths).findFirst();
        if(result.isPresent()){
            return result.get().getPrize();
        }
        throw new IllegalStateException("Aucun tarif trouvé pour "+nbMonths+" mois.");
    }
}
