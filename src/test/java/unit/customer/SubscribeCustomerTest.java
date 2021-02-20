package unit.customer;



import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;
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
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import unit.customer.adapter.InMemoryCustomerAccountDatabaseAdapter;
import unit.customer.adapter.InMemoryCustomerRateDatabaseAdapter;
import unit.customer.adapter.InMemorySubscriptionDatabaseAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;


@RunWith(HierarchicalContextRunner.class)
public class SubscribeCustomerTest {
    public static final int ONE_MONTH = 1;
    public static final int THREE_MONTH = 3;
    public static final int FOUR_MONTH = 4;
    public static final int SIX_MONTHS = 6;
    private static final Long ABC = 123L;
    private static final Long TRY = 456L;
    private static final Long ADM = 0L;
    Customer massiCustomer = new Customer(ABC, "Massi");
    Customer bobbyCustomer = new Customer(TRY, "Bobby");
    Customer adminCustomer = new Customer(ADM, "Admin");

    @Test
    public void shouldBobbyHaveSoldeOf5EurosAfterHavingSubscribeToMassiForOneMonth() throws SoldeInsuffisantException {
        SubscriptionDatabase subscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();
        CustomerRateDatabase customerRateDatabase =
                new InMemoryCustomerRateDatabaseAdapter();
        CustomerAccountDatabase customerAccountDatabase =
                new InMemoryCustomerAccountDatabaseAdapter();

        CustomerAccount accountBobby = new CustomerAccount(TRY, new Amount(new BigDecimal("10"), Devise.CREDIT));
        customerAccountDatabase.addAccount(accountBobby);
        CustomerAccount accountMassi = new CustomerAccount(ABC, new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
        customerAccountDatabase.addAccount(accountMassi);
        CustomerAccount accountSystem = new CustomerAccount(ADM, new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
        customerAccountDatabase.addAccount(accountSystem);


        CustomerRate massiRate = new CustomerRate(massiCustomer, new Amount(new BigDecimal("5"), Devise.CREDIT), ONE_MONTH);
        customerRateDatabase.add(massiRate);

        SubscriptionPrize subscriptionPrize = new SubscriptionPrizeFacade(
                customerRateDatabase
        );
        SubscriptionManagement subscriptionManagement =
                new SubscriptionManagementFacade(
                        subscriptionDatabase, customerAccountDatabase,
                        customerRateDatabase, subscriptionPrize
                );

        Customer customerToSubscribeTo = new Customer(ABC, "Massi");
        LocalDate ld = LocalDate.of(2019, Month.APRIL, 15); // 15 Avril 2019
        subscriptionManagement.subscribe(bobbyCustomer, customerToSubscribeTo, ld, ONE_MONTH);

        assertTrue(subscriptionDatabase.byCustomerId(TRY).contains(new Subscription(bobbyCustomer, massiCustomer, ld, ONE_MONTH)));
        assertTrue(subscriptionDatabase.byCustomerId(TRY).size() == 1);
        Assert.assertEquals(new BigDecimal("5"), customerAccountDatabase.byId(bobbyCustomer.getId()).get().getBalance().getValue());
    }

    @Test(expected = SoldeInsuffisantException.class)
    public void shouldThrowExceptionDuToSoldeInsuffisant() throws SoldeInsuffisantException {
        SubscriptionDatabase subscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();
        CustomerRateDatabase customerRateDatabase =
                new InMemoryCustomerRateDatabaseAdapter();
        CustomerAccountDatabase customerAccountDatabase =
                new InMemoryCustomerAccountDatabaseAdapter();

        CustomerAccount accountBobby = new CustomerAccount(TRY, new Amount(new BigDecimal("4"), Devise.CREDIT));
        customerAccountDatabase.addAccount(accountBobby);
        CustomerAccount accountMassi = new CustomerAccount(ABC, new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
        customerAccountDatabase.addAccount(accountMassi);
        CustomerAccount accountSystem = new CustomerAccount(ADM, new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
        customerAccountDatabase.addAccount(accountSystem);

        CustomerRate massiRate = new CustomerRate(massiCustomer, new Amount(new BigDecimal("5"), Devise.CREDIT), ONE_MONTH);
        customerRateDatabase.add(massiRate);

        SubscriptionPrize subscriptionPrize = new SubscriptionPrizeFacade(
                customerRateDatabase
        );
        SubscriptionManagement subscriptionManagement =
                new SubscriptionManagementFacade(
                        subscriptionDatabase, customerAccountDatabase,
                        customerRateDatabase, subscriptionPrize
                );
        Customer customerToSubscribeTo = new Customer(ABC, "Massi");
        subscriptionManagement.subscribe(bobbyCustomer, customerToSubscribeTo, LocalDate.of(2019,Month.APRIL,15), ONE_MONTH);
        fail("L'exception SoldeInsuffisantException aurait dû être levée.....");
    }

    public class BestRateFor {
        CustomerAccount accountBobby = new CustomerAccount(TRY, new Amount(new BigDecimal("12"), Devise.CREDIT));

        CustomerRateDatabase customerRateDatabase = new InMemoryCustomerRateDatabaseAdapter();
        CustomerRate massiRate1Month = new CustomerRate(massiCustomer, new Amount(new BigDecimal("10.00"), Devise.CREDIT), ONE_MONTH);
        CustomerRate massiRate3Month = new CustomerRate(massiCustomer, new Amount(new BigDecimal("25.00"), Devise.CREDIT), THREE_MONTH);
        CustomerRate massiRate6Month = new CustomerRate(massiCustomer, new Amount(new BigDecimal("45.00"), Devise.CREDIT), SIX_MONTHS);
        SubscriptionPrize subscriptionPrize = new SubscriptionPrizeFacade(customerRateDatabase);

        @Test
        public void shouldRetrieveTheBestRateFor3MonthWithRateFor3Month() {
            customerRateDatabase.add(massiRate1Month);
            customerRateDatabase.add(massiRate3Month);
            customerRateDatabase.add(massiRate6Month);

            Amount bestPrize = subscriptionPrize.getBestPrize(massiCustomer, THREE_MONTH);
            Assert.assertEquals(new BigDecimal("25.00"), bestPrize.getValue());
        }

        @Test
        public void shouldRetrieveTheBestRateFor4MonthWithNoRateFor4Month() {

            customerRateDatabase.add(massiRate1Month);
            customerRateDatabase.add(massiRate3Month);
            customerRateDatabase.add(massiRate6Month);

            Amount bestPrize = subscriptionPrize.getBestPrize(massiCustomer, FOUR_MONTH);
            Assert.assertEquals(new BigDecimal("35.00"), bestPrize.getValue());
        }

        @Test
        public void shouldRetrieveTheBestRateFor6MonthWithNoRateFor6Month() {
            customerRateDatabase.add(massiRate1Month);
            customerRateDatabase.add(massiRate3Month);
            customerRateDatabase.add(massiRate6Month);

            Amount bestPrize = subscriptionPrize.getBestPrize(massiCustomer, SIX_MONTHS);
            Assert.assertEquals(new BigDecimal("45.00"), bestPrize.getValue());
        }
        @Test
        public void shouldRetrieveTheBestRateFor7MonthWithNoRateFor7Month() {
            customerRateDatabase.add(massiRate1Month);
            customerRateDatabase.add(massiRate3Month);
            customerRateDatabase.add(massiRate6Month);

            Amount bestPrize = subscriptionPrize.getBestPrize(massiCustomer, 7);
            Assert.assertEquals(new BigDecimal("55.00"), bestPrize.getValue());
        }
        @Test
        public void shouldRetrieveTheBestRateFor8MonthWithNoRateFor8Month() {
            customerRateDatabase.add(massiRate1Month);
            customerRateDatabase.add(massiRate3Month);
            customerRateDatabase.add(massiRate6Month);

            Amount bestPrize = subscriptionPrize.getBestPrize(massiCustomer, 8);
            Assert.assertEquals(new BigDecimal("65.00"), bestPrize.getValue());
        }
    }

    public class prolongSubscription {
        @Test
        public void shouldVerifyThatTheNewSubscriptionStartAfterTheCurrentSubscriptionWhichRemain5Days() throws SoldeInsuffisantException {
            SubscriptionDatabase subscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();
            Subscription subscription = new Subscription(
                    bobbyCustomer,
                    massiCustomer, LocalDate.of(2019,Month.JUNE,20),
                    THREE_MONTH);
            subscriptionDatabase.add(subscription);

            CustomerAccountDatabase customerAccountDatabase = new InMemoryCustomerAccountDatabaseAdapter();
            CustomerAccount accountBobby = new CustomerAccount(TRY, new Amount(new BigDecimal("120"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountBobby);
            CustomerAccount accountMassi = new CustomerAccount(ABC, new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountMassi);
            CustomerAccount accountSystem = new CustomerAccount(ADM, new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountSystem);

            CustomerRateDatabase customerRateDatabase = new InMemoryCustomerRateDatabaseAdapter();
            CustomerRate massiRate = new CustomerRate(massiCustomer, new Amount(new BigDecimal("15"), Devise.CREDIT), ONE_MONTH);
            customerRateDatabase.add(massiRate);

            SubscriptionPrize subscriptionPrize = new SubscriptionPrizeFacade(
                    customerRateDatabase
            );
            SubscriptionManagement subscriptionManagement =
                    new SubscriptionManagementFacade(
                            subscriptionDatabase, customerAccountDatabase,
                            customerRateDatabase, subscriptionPrize
                    );

            subscriptionManagement.subscribe(bobbyCustomer, massiCustomer, LocalDate.of(2019,Month.SEPTEMBER,15), ONE_MONTH);
            Set<Subscription> latestSubscription = subscriptionDatabase.byCustomerId(bobbyCustomer.getId());
            Comparator<? super Subscription> dateSubscriptionComparator = new Comparator<Subscription>() {
                public int compare(Subscription s1, Subscription s2) {
                    return s2.getSubscriptionDate().plusMonths(s2.getNbMonths()).compareTo(
                            s1.getSubscriptionDate().plusMonths(s1.getNbMonths())
                    );
                }
            };

            Subscription sub = latestSubscription.stream().sorted(dateSubscriptionComparator).findFirst().get();
            assertTrue(LocalDate.of(2019, Month.OCTOBER, 21)
                    .isEqual(sub.getSubscriptionDate().plusMonths(sub.getNbMonths())
                    )
            );
        }
    }

    public class DistributionAmountTest {

        @Test
        public void shouldDistributeAdminEndTipsterForOneMonth() throws SoldeInsuffisantException {
            CustomerRateDatabase customerRateDatabase = new InMemoryCustomerRateDatabaseAdapter();
            SubscriptionDatabase subscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();
            CustomerAccountDatabase customerAccountDatabase = new InMemoryCustomerAccountDatabaseAdapter();

            CustomerAccount accountBobby = new CustomerAccount(TRY, new Amount(new BigDecimal("40.00"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountBobby);
            CustomerAccount accountMassi = new CustomerAccount(ABC, new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountMassi);
            CustomerAccount accountSystem = new CustomerAccount(ADM, new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountSystem);
            CustomerRate massiRate1 = new CustomerRate(massiCustomer, new Amount(new BigDecimal("10.00"), Devise.CREDIT), ONE_MONTH);
            customerRateDatabase.add(massiRate1);
            CustomerRate massiRate3 = new CustomerRate(massiCustomer, new Amount(new BigDecimal("25.00"), Devise.CREDIT), THREE_MONTH);
            customerRateDatabase.add(massiRate3);

            SubscriptionPrize subscriptionPrize = new SubscriptionPrizeFacade(customerRateDatabase);
            SubscriptionManagement subscriptionManagement =
                    new SubscriptionManagementFacade(
                            subscriptionDatabase, customerAccountDatabase,
                            customerRateDatabase, subscriptionPrize);

            subscriptionManagement.subscribe(bobbyCustomer, massiCustomer, LocalDate.of(2019,Month.SEPTEMBER,15), ONE_MONTH);

            assertTrue(accountBobby.getBalance().getValue().compareTo(new BigDecimal("30.00")) == 0);
            assertTrue(accountMassi.getBalance().getValue().compareTo(new BigDecimal("2509.00")) == 0);
            assertTrue(accountSystem.getBalance().getValue().compareTo(new BigDecimal("150001.00")) == 0);
        }

        @Test
        public void shouldDistributeAdminEndTipsterForThreeMonth() throws SoldeInsuffisantException {
            CustomerRateDatabase customerRateDatabase = new InMemoryCustomerRateDatabaseAdapter();
            SubscriptionDatabase subscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();
            CustomerAccountDatabase customerAccountDatabase = new InMemoryCustomerAccountDatabaseAdapter();

            CustomerAccount accountBobby = new CustomerAccount(TRY, new Amount(new BigDecimal("40.00"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountBobby);
            CustomerAccount accountMassi = new CustomerAccount(ABC, new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountMassi);
            CustomerAccount accountSystem = new CustomerAccount(ADM, new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
            customerAccountDatabase.addAccount(accountSystem);
            CustomerRate massiRate1 = new CustomerRate(massiCustomer, new Amount(new BigDecimal("10.00"), Devise.CREDIT), ONE_MONTH);
            customerRateDatabase.add(massiRate1);
            CustomerRate massiRate3 = new CustomerRate(massiCustomer, new Amount(new BigDecimal("25.00"), Devise.CREDIT), THREE_MONTH);
            customerRateDatabase.add(massiRate3);

            SubscriptionPrize subscriptionPrize = new SubscriptionPrizeFacade(customerRateDatabase);
            SubscriptionManagement subscriptionManagement =
                    new SubscriptionManagementFacade(
                            subscriptionDatabase, customerAccountDatabase,
                            customerRateDatabase, subscriptionPrize);

            subscriptionManagement.subscribe(bobbyCustomer, massiCustomer, LocalDate.of(2019,Month.SEPTEMBER,15), THREE_MONTH);

            assertTrue(accountBobby.getBalance().getValue().compareTo(new BigDecimal("15.00")) == 0);
            assertTrue(accountMassi.getBalance().getValue().compareTo(new BigDecimal("2522.50")) == 0);
            assertTrue(accountSystem.getBalance().getValue().compareTo(new BigDecimal("150002.50")) == 0);
        }
    }
}