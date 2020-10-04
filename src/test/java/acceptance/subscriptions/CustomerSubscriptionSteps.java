package acceptance.subscriptions;

import acceptance.account.facilities.SubscriptionAttempt;
import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.SoldeInsuffisantException;
import com.sfl.flybet.casestudy.domain.ports.customer.SubscribeCustomerPort;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRateRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.SubscriptionRepository;
import com.sfl.flybet.casestudy.domain.adapters.SubscribeCustomer;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import io.cucumber.java8.PendingException;
import org.exparity.hamcrest.date.LocalDateMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CustomerSubscriptionSteps implements En {

        public CustomerSubscriptionSteps(AuthenticationCustomerGateway authenticationCustomerGateway,
                                     CustomerAccountRepository customerAccountRepository,
                                     CustomerRepository customerRepository,
                                     CustomerRateRepository customerRateRepository,
                                     SubscriptionRepository subscriptionRepository,
                                     ScenarioPronosticContext scenarioPronosticContext) {
        final SubscribeCustomerPort subscribeCustomerPort = new SubscribeCustomer(
                customerAccountRepository, customerRateRepository, subscriptionRepository);
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");


        And("^le coût de l'abonnement au compte de \"([^\"]*)\" est de \"([^\"]*)\" crédits pour (\\d+) mois$",
                (String customerId, String subscriptionAmount, Integer nbMonths) -> {
                    Optional<Customer> customer = customerRepository.byId(customerId);
                    if(customer.isPresent()) {
                        CustomerRate customerRate = new CustomerRate(customer.get(),
                                new Amount(new BigDecimal(subscriptionAmount), Devise.CREDIT),
                                nbMonths);
                        customerRateRepository.add(customerRate);
                    }
                    else{
                        fail("Customer non présent dans le repository!");
                    }
        });
        And("^je suis abonné au compte de \"([^\"]*)\" le \"([^\"]*)\" pour (\\d+) mois$",
                (String customerId, String dateDebutSubscription, Integer nbMonths) -> {
                    LocalDate ld = LocalDate.parse(dateDebutSubscription, dtf);
                    Subscription subscription = new Subscription(authenticationCustomerGateway.currentCustomer().get(),
                            customerRepository.byId(customerId).get(), ld, nbMonths);
                    subscriptionRepository.add(subscription);
                });

        When("^je tente de m'abonner au compte de \"([^\"]*)\" pour (\\d+) mois le \"([^\"]*)\"$",
                (String customerId, Integer nbMonths, String dateDebutSubscription) -> {
                    if(customerRepository.byId(customerId).isPresent()) {
                        Customer customerToSubscribeTo = customerRepository.byId(customerId).get();

                        try {
                            LocalDate ld = LocalDate.parse(dateDebutSubscription, dtf);
                            //noinspection OptionalGetWithoutIsPresent because done in the step "je suis authnetifié..."
                            subscribeCustomerPort.subscribe(authenticationCustomerGateway.currentCustomer().get(),
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
                    if(customerRepository.byId(customerId).isPresent()) {
                        Customer customerToSubscribeTo = customerRepository.byId(customerId).get();

                        Amount amount = subscribeCustomerPort.getBestPrize(customerToSubscribeTo, nbMonths);
                        scenarioPronosticContext.setContextValue(PronosContext.SUBSCRIBE_PRIZE, amount);
                    }

        });

        Then("^l'abonnement est effectif$", () -> {
            SubscriptionAttempt attempt = (SubscriptionAttempt) scenarioPronosticContext.getContextValue(
                    PronosContext.SUBSCRIBE_ATTEMPT);
            Optional<Subscription> subscription = subscriptionRepository.byCustomerId(
                    authenticationCustomerGateway.currentCustomer().get().getId())
                    .stream().filter(s -> s.getSubscriptionTo().equals(attempt.getSubscriptionTo())).findFirst();
            Assert.assertTrue(subscription.isPresent());
        });
        Then("^une erreur de solde insuffisant est remontée$", () -> {
            Assert.assertThat(scenarioPronosticContext.getContextValue(PronosContext.SUBSCRIBE_ERROR),
                    CoreMatchers.equalTo("SoldeInsuffisantException"));
        });
        Then("^le tarif de l'abonnement est de \"([^\"]*)\"$", (String tarif) -> {
            Amount amount = (Amount) scenarioPronosticContext.getContextValue(PronosContext.SUBSCRIBE_PRIZE);
            assertThat(new BigDecimal(tarif), CoreMatchers.is(CoreMatchers.equalTo(amount.getValue())));
        });

        /* Les steps utilises dans Given et dans Then */
        And("^la fin de mon abonnement est le \"([^\"]*)\" inclus$", (String dateFin) -> {
            SubscriptionAttempt attempt = (SubscriptionAttempt) scenarioPronosticContext.getContextValue(
                    PronosContext.SUBSCRIBE_ATTEMPT);
            if(null != attempt) {
                LocalDate ld = LocalDate.parse(dateFin, dtf);
                Set<Subscription> latestSubscription = subscriptionRepository.byCustomerId(authenticationCustomerGateway.currentCustomer().get().getId());
                Comparator<? super Subscription> dateSubscriptionComparator = new Comparator<Subscription>() {
                    public int compare(Subscription s1, Subscription s2) {
                        return s2.getSubscriptionDate().plusMonths(s2.getNbMonths()).compareTo(
                                s1.getSubscriptionDate().plusMonths(s1.getNbMonths())
                        );
                    }
                };
                Subscription subscription = latestSubscription.stream().sorted(dateSubscriptionComparator).findFirst().get();

                assertThat(subscription.getSubscriptionDate().plusMonths(subscription.getNbMonths()),
                        LocalDateMatchers.sameDay(ld)
                        );
            }
            else{
                throw new PendingException();
            }
        });
            And("^le nombre d'abonné à \"([^\"]*)\" est (\\d+)$", (String pseudo, Integer nbAbonnes) -> {
                Assert.assertThat(subscriptionRepository.getSubscribersCountFor(customerRepository.byPseudo(pseudo).get()), Matchers.equalTo(0));
            });

        }

}
