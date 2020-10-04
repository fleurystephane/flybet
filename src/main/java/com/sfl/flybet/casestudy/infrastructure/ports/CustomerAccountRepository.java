package com.sfl.flybet.casestudy.infrastructure.ports;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.CustomerAccount;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface CustomerAccountRepository {

    Optional<CustomerAccount> byId(String id);

    void add(CustomerAccount account);

    Set<CustomerAccount> all();

    CustomerAccount getAccountOf(Customer customer);

    void update(CustomerAccount customerToSubscribeToAccount);

    void addSystemAmount(Amount amount);
}
