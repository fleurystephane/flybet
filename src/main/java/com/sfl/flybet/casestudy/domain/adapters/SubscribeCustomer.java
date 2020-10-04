package com.sfl.flybet.casestudy.domain.adapters;

import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.SoldeInsuffisantException;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRateRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.SubscriptionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

public class SubscribeCustomer implements com.sfl.flybet.casestudy.domain.ports.customer.SubscribeCustomerPort {
    private CustomerAccountRepository customerAccountRepository;
    private CustomerRateRepository customerRateRepository;
    private SubscriptionRepository subscriptionRepository;

    Comparator<? super CustomerRate> monthDescComparator = (Comparator<CustomerRate>) (o1, o2) -> o2.getNbMonths() - o1.getNbMonths();

    public SubscribeCustomer(CustomerAccountRepository customerAccountRepository,
                             CustomerRateRepository customerRateRepository,
                             SubscriptionRepository subscriptionRepository) {

        this.customerAccountRepository = customerAccountRepository;
        this.customerRateRepository = customerRateRepository;
        this.subscriptionRepository = subscriptionRepository;
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
        Amount amountPaied = getBestPrize(customerToSubscribeTo, nbMonths);
        subscriberAccount.paySubscription(amountPaied);

        redistributeSubscription(customerToSubscribeToAccount, amountPaied);

        Subscription subscription = new Subscription(
                subscriber, customerToSubscribeTo,
                calculStartDate(subscriptionDate, subscriber, customerToSubscribeTo), nbMonths);
        subscriptionRepository.add(subscription);

    }

    private void redistributeSubscription(CustomerAccount customerToSubscribeToAccount, Amount amount) {
        BigDecimal amountToRedistributeToCustomerToSubscribeTo = retrieveAmountForCustomer(customerToSubscribeToAccount, amount);
        Amount newBalance = new Amount(customerToSubscribeToAccount.getBalance().getValue()
                .add(amountToRedistributeToCustomerToSubscribeTo), Devise.CREDIT);
        customerToSubscribeToAccount.setBalance(newBalance);
        customerAccountRepository.update(customerToSubscribeToAccount);
        customerAccountRepository.addSystemAmount(retrieveAmountForSystem(customerToSubscribeToAccount, amount));
    }

    private Amount retrieveAmountForSystem(CustomerAccount customerToSubscribeToAccount, Amount amount) {
        return new Amount(amount.getValue().multiply(BigDecimal.TEN).divide(new BigDecimal(100)),
                Devise.CREDIT);
    }

    private BigDecimal retrieveAmountForCustomer(CustomerAccount customerToSubscribeToAccount, Amount amount) {
        return amount.getValue().subtract(amount.getValue().multiply(BigDecimal.TEN).divide(new BigDecimal(100)));
    }

    private boolean isInTheFutur(LocalDate subscriptionDate) {
        return subscriptionDate.isAfter(LocalDate.now());
    }

    private LocalDate calculStartDate(LocalDate subscriptionDate, Customer subscriber, Customer customerToSubscribeTo) {
        // Si le souscripteur est deja actuellement abonné à ce Tipster,
        // le nouvel abonnement prendra effet qu'à partir de la date de fin de l'abonnment actuel.
        Optional<Subscription> subscription = subscriptionRepository.byCustomerId(
                subscriber.getId()).stream().filter(
                        s -> s.getSubscriptionTo().equals(customerToSubscribeTo) && s.isActiveNow(subscriptionDate)).findFirst();
        return subscription.map(value -> value.getSubscriptionDate().plusMonths(value.getNbMonths()).plusDays(1)).orElse(subscriptionDate);
    }

    @Override
    public Amount getBestPrize(Customer customerToSubscribeTo, int nbMonths) {
        Set<CustomerRate> customerRates = customerRateRepository.findByCustomerId(customerToSubscribeTo.getId());
        Optional<CustomerRate> cr = customerRates.stream().sorted(monthDescComparator)
                .filter(rate -> (rate.getNbMonths() == nbMonths ||
                        (rate.getNbMonths()>1 && nbMonths % rate.getNbMonths() == 0))).findFirst();
        return cr.map(customerRate -> produit(customerRate.getPrize(), (nbMonths / customerRate.getNbMonths()))).orElseGet(() -> addition(
                getBestPrize(customerToSubscribeTo, nbMonths - 1),
                getBestPrize(customerToSubscribeTo, 1)));
    }

    private Amount produit(Amount prize, int i) {
        return new Amount(prize.getValue().multiply(new BigDecimal(i)), Devise.CREDIT);
    }

    private Amount addition(Amount bestPrize, Amount bestPrize1) {
        return new Amount(bestPrize.getValue().add(bestPrize1.getValue()), Devise.CREDIT);
    }

    private CustomerAccount getCustomerAccount(Customer customer) {
        return customerAccountRepository.getAccountOf(customer);
    }

    private Amount getRateFor(Customer customer, int nbMonths) {
        return customerRateRepository.getRateFor(customer, nbMonths);

    }

}
