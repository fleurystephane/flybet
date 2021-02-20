package com.sfl.flybet.domain.subscription.ports.incoming;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.customer.model.Customer;

public interface SubscriptionPrize {
    Amount getBestPrize(Customer customerToSubscribeTo, int nbMonths);
}
