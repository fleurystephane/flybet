package unit.customer.adapter;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCustomerDatabaseAdapter implements CustomerDatabase {

    ConcurrentHashMap<Long, Customer> customers = new ConcurrentHashMap<>();

    @Override
    public void add(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    public Collection<Customer> all() {
        return customers.values();
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public Optional<Customer> getCustomerByPseudo(String pseudo) {
        return customers.values().stream().filter(customer -> customer.getPseudo().equals(pseudo)).findAny();
    }
}
