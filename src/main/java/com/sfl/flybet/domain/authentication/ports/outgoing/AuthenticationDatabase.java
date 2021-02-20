package com.sfl.flybet.domain.authentication.ports.outgoing;

import com.sfl.flybet.domain.authentication.model.UserDescriptor;
import com.sfl.flybet.domain.customer.model.Customer;

public interface AuthenticationDatabase {
    UserDescriptor authenticateCustomer(Customer customer);
}
