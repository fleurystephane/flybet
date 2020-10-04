package unit.customer;

import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryCustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemorySubscriptionRepository;
import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.SoldeInsuffisantException;
import com.sfl.flybet.casestudy.domain.gateways.InMemoryCustomerRateRepository;
import com.sfl.flybet.casestudy.domain.ports.customer.SubscribeCustomerPort;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRateRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.SubscriptionRepository;
import com.sfl.flybet.casestudy.domain.adapters.SubscribeCustomer;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.exparity.hamcrest.date.LocalDateMatchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;


@RunWith(HierarchicalContextRunner.class)
public class SubscribeCustomerTest {
    public static final int ONE_MONTH = 1;
    public static final int THREE_MONTH = 3;
    public static final int FOUR_MONTH = 4;
    public static final int SIX_MONTHS = 6;
    Customer massiCustomer = new Customer("ABC", "Massi");
    Customer bobbyCustomer = new Customer("TRY", "Bobby");
    Customer adminCustomer = new Customer("ADM", "Admin");

    @Test
    public void shouldBobbyHaveSoldeOf5EurosAfterHavingSubscribeToMassiForOneMonth() throws SoldeInsuffisantException {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();

        CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
        CustomerAccount accountBobby = new CustomerAccount("TRY", new Amount(new BigDecimal("10"), Devise.CREDIT));
        customerAccountRepository.add(accountBobby);
        CustomerAccount accountMassi = new CustomerAccount("ABC", new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
        customerAccountRepository.add(accountMassi);
        CustomerAccount accountSystem = new CustomerAccount("ADM", new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
        customerAccountRepository.add(accountSystem);

        CustomerRateRepository customerRateRepository = new InMemoryCustomerRateRepository();

        CustomerRate massiRate = new CustomerRate(massiCustomer, new Amount(new BigDecimal("5"), Devise.CREDIT), ONE_MONTH);
        customerRateRepository.add(massiRate);

        SubscribeCustomerPort subscribeCustomerPort = new SubscribeCustomer(customerAccountRepository, customerRateRepository, subscriptionRepository);

        Customer customerToSubscribeTo = new Customer("ABC", "Massi");
        LocalDate ld = LocalDate.of(2019, Month.APRIL, 15); // 15 Avril 2019
        subscribeCustomerPort.subscribe(bobbyCustomer, customerToSubscribeTo, ld, ONE_MONTH);

        assertThat(subscriptionRepository.byCustomerId("TRY"), hasItem(new Subscription(bobbyCustomer, massiCustomer, ld, ONE_MONTH)));
        assertThat(subscriptionRepository.byCustomerId("TRY"), IsCollectionWithSize.hasSize(1));
        Assert.assertEquals(new BigDecimal("5"), customerAccountRepository.byId(bobbyCustomer.getId()).get().getBalance().getValue());
    }

    @Test(expected = SoldeInsuffisantException.class)
    public void shouldThrowExceptionDuToSoldeInsuffisant() throws SoldeInsuffisantException {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();

        CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
        CustomerAccount accountBobby = new CustomerAccount("TRY", new Amount(new BigDecimal("12"), Devise.CREDIT));
        customerAccountRepository.add(accountBobby);
        CustomerAccount accountMassi = new CustomerAccount("ABC", new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
        customerAccountRepository.add(accountMassi);
        CustomerAccount accountSystem = new CustomerAccount("ADM", new Amount(new BigDecimal("130000.00"), Devise.CREDIT));
        customerAccountRepository.add(accountSystem);

        CustomerRateRepository customerRateRepository = new InMemoryCustomerRateRepository();

        CustomerRate massiRate = new CustomerRate(massiCustomer, new Amount(new BigDecimal("15"), Devise.CREDIT), ONE_MONTH);
        customerRateRepository.add(massiRate);

        SubscribeCustomerPort subscribeCustomerPort = new SubscribeCustomer(customerAccountRepository, customerRateRepository, subscriptionRepository);

        Customer customerToSubscribeTo = new Customer("ABC", "Massi");
        subscribeCustomerPort.subscribe(bobbyCustomer, customerToSubscribeTo, LocalDate.of(2019,Month.APRIL,15), ONE_MONTH);
        fail("L'exception SoldeInsuffisantException aurait dû être levée.....");
    }

    public class BestRateFor {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
        CustomerAccount accountBobby = new CustomerAccount("TRY", new Amount(new BigDecimal("12"), Devise.CREDIT));
        CustomerRateRepository customerRateRepository = new InMemoryCustomerRateRepository();
        CustomerRate massiRate1Month = new CustomerRate(massiCustomer, new Amount(new BigDecimal("10.00"), Devise.CREDIT), ONE_MONTH);
        CustomerRate massiRate3Month = new CustomerRate(massiCustomer, new Amount(new BigDecimal("25.00"), Devise.CREDIT), THREE_MONTH);
        CustomerRate massiRate6Month = new CustomerRate(massiCustomer, new Amount(new BigDecimal("45.00"), Devise.CREDIT), SIX_MONTHS);
        SubscribeCustomerPort subscribeCustomerPort = new SubscribeCustomer(customerAccountRepository, customerRateRepository, subscriptionRepository);

        @Test
        public void shouldRetrieveTheBestRateFor3MonthWithRateFor3Month() {
            customerAccountRepository.add(accountBobby);
            customerRateRepository.add(massiRate1Month);
            customerRateRepository.add(massiRate3Month);
            customerRateRepository.add(massiRate6Month);

            Amount bestPrize = subscribeCustomerPort.getBestPrize(massiCustomer, THREE_MONTH);
            Assert.assertEquals(new BigDecimal("25.00"), bestPrize.getValue());
        }

        @Test
        public void shouldRetrieveTheBestRateFor4MonthWithNoRateFor4Month() {
            customerAccountRepository.add(accountBobby);
            customerRateRepository.add(massiRate1Month);
            customerRateRepository.add(massiRate3Month);
            customerRateRepository.add(massiRate6Month);

            Amount bestPrize = subscribeCustomerPort.getBestPrize(massiCustomer, FOUR_MONTH);
            Assert.assertEquals(new BigDecimal("35.00"), bestPrize.getValue());
        }

        @Test
        public void shouldRetrieveTheBestRateFor6MonthWithNoRateFor6Month() {
            customerAccountRepository.add(accountBobby);
            customerRateRepository.add(massiRate1Month);
            customerRateRepository.add(massiRate3Month);
            customerRateRepository.add(massiRate6Month);

            Amount bestPrize = subscribeCustomerPort.getBestPrize(massiCustomer, 6);
            Assert.assertEquals(new BigDecimal("45.00"), bestPrize.getValue());
        }
        @Test
        public void shouldRetrieveTheBestRateFor7MonthWithNoRateFor7Month() {
            customerAccountRepository.add(accountBobby);
            customerRateRepository.add(massiRate1Month);
            customerRateRepository.add(massiRate3Month);
            customerRateRepository.add(massiRate6Month);

            Amount bestPrize = subscribeCustomerPort.getBestPrize(massiCustomer, 7);
            Assert.assertEquals(new BigDecimal("55.00"), bestPrize.getValue());
        }
        @Test
        public void shouldRetrieveTheBestRateFor8MonthWithNoRateFor8Month() {
            customerAccountRepository.add(accountBobby);
            customerRateRepository.add(massiRate1Month);
            customerRateRepository.add(massiRate3Month);
            customerRateRepository.add(massiRate6Month);

            Amount bestPrize = subscribeCustomerPort.getBestPrize(massiCustomer, 8);
            Assert.assertEquals(new BigDecimal("65.00"), bestPrize.getValue());
        }
    }

    public class prolongSubscription {
        @Test
        public void shouldVerifyThatTheNewSubscriptionStartAfterTheCurrentSubscriptionWhichRemain5Days() throws SoldeInsuffisantException {
            SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
            Subscription subscription = new Subscription(
                    bobbyCustomer,
                    massiCustomer, LocalDate.of(2019,Month.JUNE,20),
                    THREE_MONTH);
            subscriptionRepository.add(subscription);

            CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
            CustomerAccount accountBobby = new CustomerAccount("TRY", new Amount(new BigDecimal("120"), Devise.CREDIT));
            customerAccountRepository.add(accountBobby);
            CustomerAccount accountMassi = new CustomerAccount("ABC", new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
            customerAccountRepository.add(accountMassi);
            CustomerAccount accountSystem = new CustomerAccount("ADM", new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
            customerAccountRepository.add(accountSystem);

            CustomerRateRepository customerRateRepository = new InMemoryCustomerRateRepository();

            CustomerRate massiRate = new CustomerRate(massiCustomer, new Amount(new BigDecimal("15"), Devise.CREDIT), ONE_MONTH);
            customerRateRepository.add(massiRate);

            SubscribeCustomerPort subscribeCustomerPort = new SubscribeCustomer(customerAccountRepository, customerRateRepository, subscriptionRepository);

            subscribeCustomerPort.subscribe(bobbyCustomer, massiCustomer, LocalDate.of(2019,Month.SEPTEMBER,15), ONE_MONTH);
            Set<Subscription> latestSubscription = subscriptionRepository.byCustomerId(bobbyCustomer.getId());
            Comparator<? super Subscription> dateSubscriptionComparator = new Comparator<Subscription>() {
                public int compare(Subscription s1, Subscription s2) {
                    return s2.getSubscriptionDate().plusMonths(s2.getNbMonths()).compareTo(
                            s1.getSubscriptionDate().plusMonths(s1.getNbMonths())
                    );
                }
            };

            Subscription sub = latestSubscription.stream().sorted(dateSubscriptionComparator).findFirst().get();
            assertThat(sub.getSubscriptionDate().plusMonths(sub.getNbMonths()),
                    LocalDateMatchers.sameDay(LocalDate.of(2019, Month.OCTOBER, 21))
            );
        }
    }

    public class DistributionAmountTest {

        @Test
        public void shouldDistributeAdminEndTipsterForOneMonth() throws SoldeInsuffisantException {
            CustomerRateRepository customerRateRepository = new InMemoryCustomerRateRepository();
            SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();

            CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
            CustomerAccount accountBobby = new CustomerAccount("TRY", new Amount(new BigDecimal("40.00"), Devise.CREDIT));
            customerAccountRepository.add(accountBobby);
            CustomerAccount accountMassi = new CustomerAccount("ABC", new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
            customerAccountRepository.add(accountMassi);
            CustomerAccount accountSystem = new CustomerAccount("ADM", new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
            customerAccountRepository.add(accountSystem);
            CustomerRate massiRate1 = new CustomerRate(massiCustomer, new Amount(new BigDecimal("10.00"), Devise.CREDIT), ONE_MONTH);
            customerRateRepository.add(massiRate1);
            CustomerRate massiRate3 = new CustomerRate(massiCustomer, new Amount(new BigDecimal("25.00"), Devise.CREDIT), THREE_MONTH);
            customerRateRepository.add(massiRate3);

            SubscribeCustomerPort subscribeCustomerPort = new SubscribeCustomer(customerAccountRepository, customerRateRepository, subscriptionRepository);

            subscribeCustomerPort.subscribe(bobbyCustomer, massiCustomer, LocalDate.of(2019,Month.SEPTEMBER,15), ONE_MONTH);

            assertThat(accountBobby.getBalance().getValue(), equalTo(new BigDecimal("30.00")));
            assertThat(accountMassi.getBalance().getValue(), equalTo(new BigDecimal("2509.00")));
            assertThat(accountSystem.getBalance().getValue(), equalTo(new BigDecimal("150001.00")));
        }

        @Test
        public void shouldDistributeAdminEndTipsterForThreeMonth() throws SoldeInsuffisantException {
            CustomerRateRepository customerRateRepository = new InMemoryCustomerRateRepository();
            SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();

            CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
            CustomerAccount accountBobby = new CustomerAccount("TRY", new Amount(new BigDecimal("40.00"), Devise.CREDIT));
            customerAccountRepository.add(accountBobby);
            CustomerAccount accountMassi = new CustomerAccount("ABC", new Amount(new BigDecimal("2500.00"), Devise.CREDIT));
            customerAccountRepository.add(accountMassi);
            CustomerAccount accountSystem = new CustomerAccount("ADM", new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
            customerAccountRepository.add(accountSystem);
            CustomerRate massiRate1 = new CustomerRate(massiCustomer, new Amount(new BigDecimal("10.00"), Devise.CREDIT), ONE_MONTH);
            customerRateRepository.add(massiRate1);
            CustomerRate massiRate3 = new CustomerRate(massiCustomer, new Amount(new BigDecimal("25.00"), Devise.CREDIT), THREE_MONTH);
            customerRateRepository.add(massiRate3);

            SubscribeCustomerPort subscribeCustomerPort = new SubscribeCustomer(customerAccountRepository, customerRateRepository, subscriptionRepository);

            subscribeCustomerPort.subscribe(bobbyCustomer, massiCustomer, LocalDate.of(2019,Month.SEPTEMBER,15), THREE_MONTH);

            assertThat(accountBobby.getBalance().getValue(), equalTo(new BigDecimal("15.00")));
            assertThat(accountMassi.getBalance().getValue(), equalTo(new BigDecimal("2522.50")));
            assertThat(accountSystem.getBalance().getValue(), equalTo(new BigDecimal("150002.50")));
        }
    }
}