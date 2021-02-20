package com.sfl.flybet.domain.subscription;

import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.customerrate.model.CustomerRate;
import com.sfl.flybet.domain.customerrate.ports.outgoing.CustomerRateDatabase;
import com.sfl.flybet.domain.project.exceptions.SoldeInsuffisantException;
import com.sfl.flybet.domain.subscription.model.Subscription;
import com.sfl.flybet.domain.subscription.ports.incoming.SubscriptionManagement;
import com.sfl.flybet.domain.subscription.ports.incoming.SubscriptionPrize;
import com.sfl.flybet.domain.subscription.ports.outgoing.SubscriptionDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

public class SubscriptionManagementFacade implements SubscriptionManagement {

    private final SubscriptionDatabase subscriptionDatabase;
    private final CustomerAccountDatabase accountDatabase;
    private final CustomerRateDatabase customerRateDatabase;
    private final SubscriptionPrize subscriptionPrize;

    Comparator<? super CustomerRate> monthDescComparator =
            (Comparator<CustomerRate>) (o1, o2) -> o2.getNbMonths() - o1.getNbMonths();

    public SubscriptionManagementFacade(SubscriptionDatabase subscriptionDatabase, CustomerAccountDatabase accountDatabase,
                                        CustomerRateDatabase customerRateDatabase, SubscriptionPrize subscriptionPrize) {
        this.subscriptionDatabase = subscriptionDatabase;
        this.accountDatabase = accountDatabase;
        this.customerRateDatabase = customerRateDatabase;
        this.subscriptionPrize = subscriptionPrize;
    }

    @Override
    public void subscribe(Customer subscriber, Customer customerToSubscribeTo, LocalDate subscriptionDate, int nbMonths) throws SoldeInsuffisantException {
        if(isInTheFutur(subscriptionDate)){
            throw new IllegalStateException("Impossible de souscrire un abonnement dans le futur!");
        }
        CustomerAccount subscriberAccount = getCustomerAccount(subscriber);

        if(subscriberAccount.hasBalanceLessThan(getRateFor(customerToSubscribeTo, nbMonths))){
            throw new SoldeInsuffisantException();
        }
        CustomerAccount customerToSubscribeToAccount = getCustomerAccount(customerToSubscribeTo);
        Amount amountPaied = subscriptionPrize.getBestPrize(customerToSubscribeTo, nbMonths);
        subscriberAccount.paySubscription(amountPaied);

        redistributeSubscription(customerToSubscribeToAccount, amountPaied);

        Subscription subscription = new Subscription(
                subscriber, customerToSubscribeTo,
                calculStartDate(subscriptionDate, subscriber, customerToSubscribeTo), nbMonths);
        subscriptionDatabase.add(subscription);
    }

    @Override
    public boolean isSubscribed(Customer customer, Customer tipster) {
        return false;
    }


    private boolean isInTheFutur(LocalDate subscriptionDate) {
        return subscriptionDate.isAfter(LocalDate.now());
    }

    private Amount getRateFor(Customer customer, int nbMonths) {
        return customerRateDatabase.getRateFor(customer, nbMonths);

    }

    private CustomerAccount getCustomerAccount(Customer customer) {
        Optional<CustomerAccount> account = accountDatabase.getAccountOf(customer);
        return account.get();
    }

    private void redistributeSubscription(CustomerAccount customerToSubscribeToAccount, Amount amount) {
        BigDecimal amountToRedistributeToCustomerToSubscribeTo = retrieveAmountForCustomer(customerToSubscribeToAccount, amount);
        Amount newBalance = new Amount(customerToSubscribeToAccount.getBalance().getValue()
                .add(amountToRedistributeToCustomerToSubscribeTo), Devise.CREDIT);
        customerToSubscribeToAccount.setBalance(newBalance);
        accountDatabase.update(customerToSubscribeToAccount);
        accountDatabase.addSystemAmount(retrieveAmountForSystem(customerToSubscribeToAccount, amount));
    }

    private Amount retrieveAmountForSystem(CustomerAccount customerToSubscribeToAccount, Amount amount) {
        return new Amount(amount.getValue().multiply(BigDecimal.TEN).divide(new BigDecimal(100)),
                Devise.CREDIT);
    }

    private BigDecimal retrieveAmountForCustomer(CustomerAccount customerToSubscribeToAccount, Amount amount) {
        return amount.getValue().subtract(amount.getValue().multiply(BigDecimal.TEN).divide(new BigDecimal(100)));
    }


    private LocalDate calculStartDate(LocalDate subscriptionDate, Customer subscriber, Customer customerToSubscribeTo) {
        // Si le souscripteur est deja actuellement abonné à ce Tipster,
        // le nouvel abonnement prendra effet qu'à partir de la date de fin de l'abonnment actuel.
        Optional<Subscription> subscription = subscriptionDatabase.byCustomerId(
                subscriber.getId()).stream().filter(
                s -> s.getSubscriptionTo().equals(customerToSubscribeTo) && s.isActiveNow(subscriptionDate)).findFirst();
        return subscription.map(value -> value.getSubscriptionDate().plusMonths(value.getNbMonths()).plusDays(1)).orElse(subscriptionDate);
    }

}
