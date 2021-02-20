package unit.customer.adapter;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCustomerAccountDatabaseAdapter implements CustomerAccountDatabase {
    ConcurrentHashMap<Long, CustomerAccount> accounts = new ConcurrentHashMap<>();
    @Override
    public void addAccount(CustomerAccount account) {
        accounts.put(account.getId(), account);
    }

    @Override
    public Optional<CustomerAccount> byId(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public Collection<CustomerAccount> all() {
        return accounts.values();
    }

    public Optional<CustomerAccount> getAccountOf(Customer customer) {
        return accounts.entrySet().stream().filter(c -> customer.getId().equals(c.getKey())).map(Map.Entry::getValue).findFirst();
    }

    @Override
    public void update(CustomerAccount customerToSubscribeToAccount) {
        System.out.println("TODO-----------------------");

    }

    @Override
    public void addSystemAmount(Amount amount) {
        Optional<CustomerAccount> adminAccount =
                accounts.entrySet().stream().filter(acc -> acc.getKey().equals(0L)).map(Map.Entry::getValue).findFirst();
        if(!adminAccount.isPresent()){
            throw new IllegalStateException("Admin account must always have an account!");
        }
        accounts.remove(adminAccount.get());
        adminAccount.get().setBalance(new Amount(adminAccount.get().getBalance().getValue().add(amount.getValue()), Devise.CREDIT));
        accounts.put(adminAccount.get().getId(), adminAccount.get());
    }
}
