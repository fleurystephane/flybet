package com.sfl.flybet.casestudy.domain.gateways;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.CustomerRate;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRateRepository;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryCustomerRateRepository implements CustomerRateRepository {
    Set<CustomerRate> rates = new LinkedHashSet<>();

    @Override
    public Set<CustomerRate> findByCustomerId(String customerId) {
        // TODO : normalement, on aura plusieurs CustomerRate par customerId (1 pour 1 mois, 1 pour 3 mois, 1 pour 6 mois...)
        Stream<CustomerRate> streamRates = this.rates.stream().filter(rate -> rate.getCustomerId().equals(customerId)).distinct();
        return streamRates.collect(Collectors.toSet());
    }

    @Override
    public void add(CustomerRate customerRate) {
        rates.add(customerRate);
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
