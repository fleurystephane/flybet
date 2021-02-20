package com.sfl.flybet.domain.customeraccount.ports.outgoing;


import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;

import java.util.Collection;
import java.util.Optional;

public interface CustomerAccountDatabase {
    public void addAccount(CustomerAccount account);
    Optional<CustomerAccount> byId(Long id);
    Collection<CustomerAccount> all();
    Optional<CustomerAccount> getAccountOf(Customer customer);
    void update(CustomerAccount customerToSubscribeToAccount);
    void addSystemAmount(Amount amount);
}
