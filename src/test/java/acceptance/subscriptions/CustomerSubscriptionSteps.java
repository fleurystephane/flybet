package acceptance.subscriptions;

import acceptance.account.facilities.SubscriptionAttempt;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.customerrate.model.CustomerRate;
import com.sfl.flybet.domain.customerrate.ports.outgoing.CustomerRateDatabase;
import com.sfl.flybet.domain.project.exceptions.SoldeInsuffisantException;
import com.sfl.flybet.domain.subscription.SubscriptionManagementFacade;
import com.sfl.flybet.domain.subscription.SubscriptionPrizeFacade;
import com.sfl.flybet.domain.subscription.model.Subscription;
import com.sfl.flybet.domain.subscription.ports.incoming.SubscriptionManagement;
import com.sfl.flybet.domain.subscription.ports.incoming.SubscriptionPrize;
import com.sfl.flybet.domain.subscription.ports.outgoing.SubscriptionDatabase;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import io.cucumber.java8.PendingException;
import org.junit.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.fail;

public class CustomerSubscriptionSteps implements En {

    public CustomerSubscriptionSteps(AuthenticationCustomerGateway authenticationCustomerGateway,
                                     CustomerAccountDatabase customerAccountDatabase,
                                     CustomerDatabase customerDatabase,
                                     CustomerRateDatabase customerRateDatabase,
                                     SubscriptionDatabase subscriptionDatabase,
                                     ScenarioPronosticContext scenarioPronosticContext) {
        final SubscriptionPrize subscriptionPrize =
                new SubscriptionPrizeFacade(
                        customerRateDatabase
                );
        final SubscriptionManagement subscriptionManagement =
                new SubscriptionManagementFacade(
                        subscriptionDatabase, customerAccountDatabase,
                        customerRateDatabase, subscriptionPrize
                );
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");


        And("^le coût de l'abonnement au compte de \"([^\"]*)\" est de \"([^\"]*)\" crédits pour (\\d+) mois$",
                (String customerId, String subscriptionAmount, Integer nbMonths) -> {
                    Optional<Customer> customer = customerDatabase.getCustomerById(Long.valueOf(customerId));
                    if(customer.isPresent()) {
                        CustomerRate customerRate = new CustomerRate(customer.get(),
                                new Amount(new BigDecimal(subscriptionAmount), Devise.CREDIT),
                                nbMonths);
                        customerRateDatabase.add(customerRate);
                    }
                    else{
                        fail("Customer non présent dans le repository!");
                    }
                });
        And("^je suis abonné au compte de \"([^\"]*)\" le \"([^\"]*)\" pour (\\d+) mois$",
                (String customerId, String dateDebutSubscription, Integer nbMonths) -> {
                    LocalDate ld = LocalDate.parse(dateDebutSubscription, dtf);
                    Subscription subscription = new Subscription(authenticationCustomerGateway.currentCustomer().get(),
                            customerDatabase.getCustomerById(Long.valueOf(customerId)).get(), ld, nbMonths);
                    subscriptionDatabase.add(subscription);
                });

        When("^je tente de m'abonner au compte de \"([^\"]*)\" pour (\\d+) mois le \"([^\"]*)\"$",
                (String customerId, Integer nbMonths, String dateDebutSubscription) -> {
                    if(customerDatabase.getCustomerById(Long.valueOf(customerId)).isPresent()) {
                        Customer customerToSubscribeTo = customerDatabase.getCustomerById(Long.valueOf(customerId)).get();

                        try {
                            LocalDate ld = LocalDate.parse(dateDebutSubscription, dtf);
                            //noinspection OptionalGetWithoutIsPresent because done in the step "je suis authnetifié..."
                            subscriptionManagement.subscribe(authenticationCustomerGateway.currentCustomer().get(),
                                    customerToSubscribeTo, ld, nbMonths);

                            //noinspection OptionalGetWithoutIsPresent because done in the step "je suis authnetifié..."
                            SubscriptionAttempt subscriptionAttempt =
                                    new SubscriptionAttempt(authenticationCustomerGateway.currentCustomer().get(),
                                            customerToSubscribeTo, ld, nbMonths);
                            scenarioPronosticContext.setContextValue(PronosContext.SUBSCRIBE_ATTEMPT, subscriptionAttempt);
                        } catch (SoldeInsuffisantException e) {
                            scenarioPronosticContext.setContextValue(PronosContext.SUBSCRIBE_ERROR, "SoldeInsuffisantException");
                        }
                    }
                });
        When("^je demande le tarif pour s'abonner au compte de \"([^\"]*)\" pour (\\d+) mois$",
                (String customerId, Integer nbMonths) -> {
                    if(customerDatabase.getCustomerById(Long.valueOf(customerId)).isPresent()) {
                        Customer customerToSubscribeTo = customerDatabase.getCustomerById(Long.valueOf(customerId)).get();

                        Amount amount = subscriptionPrize.getBestPrize(customerToSubscribeTo, nbMonths);
                        scenarioPronosticContext.setContextValue(PronosContext.SUBSCRIBE_PRIZE, amount);
                    }

                });

        Then("^l'abonnement au compte de \"([^\"]*)\" est effectif$", (String tipsterAccount) -> {

            SubscriptionAttempt attempt = (SubscriptionAttempt) scenarioPronosticContext.getContextValue(
                    PronosContext.SUBSCRIBE_ATTEMPT);
            Optional<Subscription> subscription = subscriptionDatabase.byCustomerId(
                    authenticationCustomerGateway.currentCustomer().get().getId())
                    .stream().filter(s -> s.getSubscriptionTo().equals(attempt.getSubscriptionTo())).findFirst();
            Assert.assertTrue(subscription.isPresent());
        });
        Then("^une erreur de solde insuffisant est remontée$", () -> {
            Assert.assertTrue(scenarioPronosticContext.getContextValue(PronosContext.SUBSCRIBE_ERROR).equals("SoldeInsuffisantException"));
        });
        Then("^le tarif de l'abonnement est de \"([^\"]*)\"$", (String tarif) -> {
            Amount amount = (Amount) scenarioPronosticContext.getContextValue(PronosContext.SUBSCRIBE_PRIZE);
            Assert.assertTrue(new BigDecimal(tarif).equals(amount.getValue()));
        });

        /* Les steps utilises dans Given et dans Then */
        And("^la fin de mon abonnement est le \"([^\"]*)\" inclus$", (String dateFin) -> {
            SubscriptionAttempt attempt = (SubscriptionAttempt) scenarioPronosticContext.getContextValue(
                    PronosContext.SUBSCRIBE_ATTEMPT);
            if(null != attempt) {
                LocalDate ld = LocalDate.parse(dateFin, dtf);
                Set<Subscription> latestSubscription = subscriptionDatabase.byCustomerId(authenticationCustomerGateway.currentCustomer().get().getId());
                Comparator<? super Subscription> dateSubscriptionComparator = new Comparator<Subscription>() {
                    public int compare(Subscription s1, Subscription s2) {
                        return s2.getSubscriptionDate().plusMonths(s2.getNbMonths()).compareTo(
                                s1.getSubscriptionDate().plusMonths(s1.getNbMonths())
                        );
                    }
                };
                Subscription subscription = latestSubscription.stream().sorted(dateSubscriptionComparator).findFirst().get();

                Assert.assertTrue(subscription.getSubscriptionDate().plusMonths(subscription.getNbMonths()).isEqual(ld)
                );
            }
            else{
                throw new PendingException();
            }
        });
        And("^le nombre d'abonné à \"([^\"]*)\" est (\\d+)$", (String pseudo, Integer nbAbonnes) -> {
            Assert.assertTrue(
                    subscriptionDatabase.getSubscribersCountFor(customerDatabase.getCustomerByPseudo(pseudo).get()) == 0);
        });
        And("^la fin de mon abonnement à \"([^\"]*)\" est le \"([^\"]*)\" inclus$", (String tipsterAccount, String dateFin) -> {

        });

    }

}
