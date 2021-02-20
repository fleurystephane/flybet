package configuration.customer;

import com.sfl.flybet.domain.customer.model.Customer;
import configuration.authorization.AuthorizationContext;

import java.util.HashMap;
import java.util.Map;

public class ScenarioCustomerContext {
    private Map<CustomerContext, Customer> contextObjectMap = new HashMap<>();

    public Customer getContextValue(CustomerContext context) {
        return contextObjectMap.get(context);
    }

    public void setContextValue(CustomerContext context, Customer customer){
        contextObjectMap.put(context, customer);
    }
}
