package com.sfl.flybet.domain.subscription;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customerrate.model.CustomerRate;
import com.sfl.flybet.domain.customerrate.ports.outgoing.CustomerRateDatabase;
import com.sfl.flybet.domain.subscription.ports.incoming.SubscriptionPrize;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

public class SubscriptionPrizeFacade implements SubscriptionPrize {
    private final CustomerRateDatabase customerRateDatabase;
    Comparator<? super CustomerRate> monthDescComparator =
            (Comparator<CustomerRate>) (o1, o2) -> o2.getNbMonths() - o1.getNbMonths();

    public SubscriptionPrizeFacade(CustomerRateDatabase customerRateDatabase) {
        this.customerRateDatabase = customerRateDatabase;
    }
    @Override
    public Amount getBestPrize(Customer customerToSubscribeTo, int nbMonths) {
        Set<CustomerRate> customerRates = customerRateDatabase.findByCustomerId(customerToSubscribeTo.getId());
        Optional<CustomerRate> cr = customerRates.stream().sorted(monthDescComparator)
                .filter(rate -> (rate.getNbMonths() == nbMonths ||
                        (rate.getNbMonths()>1 && nbMonths % rate.getNbMonths() == 0))).findFirst();
        return cr.map(customerRate -> produit(customerRate.getPrize(), (nbMonths / customerRate.getNbMonths()))).orElseGet(() -> addition(
                getBestPrize(customerToSubscribeTo, nbMonths - 1),
                getBestPrize(customerToSubscribeTo, 1)));
    }

    private Amount produit(Amount prize, int i) {
        return new Amount(prize.getValue().multiply(new BigDecimal(i)), Devise.CREDIT);
    }

    private Amount addition(Amount bestPrize, Amount bestPrize1) {
        return new Amount(bestPrize.getValue().add(bestPrize1.getValue()), Devise.CREDIT);
    }

}
