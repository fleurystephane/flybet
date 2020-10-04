package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.CustomerAccount;
import com.sfl.flybet.casestudy.domain.Devise;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class InMemoryCustomerAccountRepository implements CustomerAccountRepository {
    Set<CustomerAccount> accounts = new LinkedHashSet<>();
    public Optional<CustomerAccount> byId(String id) {
        return accounts.stream().filter(customerAccount ->
            customerAccount.getId().equals(id)
        ).findFirst();
    }

    public void add(CustomerAccount account) {
        accounts.add(account);
    }

    public Set<CustomerAccount> all() {
        return accounts;
    }

    @Override
    public CustomerAccount getAccountOf(Customer customer) {
        Optional<CustomerAccount> account = byId(customer.getId());
        if(account.isPresent()){
            return account.get();
        }
        throw new IllegalStateException("Customer "+customer.getPseudo()+" must always have an account!");
    }

    @Override
    public void update(CustomerAccount customerToSubscribeToAccount) {
        accounts.remove(accounts.stream().filter(customerAccount ->
                customerAccount.getId().equals(customerToSubscribeToAccount.getId())
        ).findFirst().get());
        accounts.add(customerToSubscribeToAccount);
    }

    @Override
    public void addSystemAmount(Amount amount) {
        CustomerAccount adminAccount = accounts.stream().filter(acc -> acc.getId().equals("ADM")).findFirst().get();
        accounts.remove(adminAccount);
        adminAccount.setBalance(new Amount(adminAccount.getBalance().getValue().add(amount.getValue()), Devise.CREDIT));
        accounts.add(adminAccount);
    }
}
