package acceptance.account.facilities;



import com.sfl.flybet.domain.customer.model.Customer;

import java.time.LocalDate;

public class SubscriptionAttempt {
    private final Customer subscriberCustomer;
    private final Customer customerToSubscribeTo;
    private LocalDate subscriptionFrom;
    private final Integer nbMonths;

    public SubscriptionAttempt(Customer currentCustomer, Customer customerToSubscribeTo, LocalDate ld, Integer nbMonths) {

        this.subscriberCustomer = currentCustomer;
        this.customerToSubscribeTo = customerToSubscribeTo;
        subscriptionFrom = ld;
        this.nbMonths = nbMonths;
    }

    public Customer getSubscriptionTo() {
        return customerToSubscribeTo;
    }

    @Override
    public String toString() {
        return "SubscriptionAttempt{" +
                "subscriberCustomer=" + subscriberCustomer +
                ", customerToSubscribeTo=" + customerToSubscribeTo +
                ", subscriptionFrom=" + subscriptionFrom +
                ", nbMonths=" + nbMonths +
                '}';
    }

    public LocalDate getEndDate() {
        return subscriptionFrom.plusMonths(nbMonths);
    }
}
