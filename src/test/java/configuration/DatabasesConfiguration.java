package configuration;


import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.customerrate.ports.outgoing.CustomerRateDatabase;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.DisapprovalPronosticDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PenaltyCustomerDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PronosticNotificationDatabase;
import com.sfl.flybet.domain.subscription.ports.outgoing.SubscriptionDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import unit.customer.adapter.InMemoryCustomerAccountDatabaseAdapter;
import unit.customer.adapter.InMemoryCustomerDatabaseAdapter;
import unit.customer.adapter.InMemoryCustomerRateDatabaseAdapter;
import unit.customer.adapter.InMemorySubscriptionDatabaseAdapter;
import unit.projects.InMemoryProjectDatabaseAdapter;
import unit.pronostic.adapter.InMemoryDisapprovalPronosticDatabaseAdapter;
import unit.pronostic.adapter.InMemoryNotificationRepositoryAdapter;
import unit.pronostic.adapter.InMemoryPenaltyCustomerDatabaseAdapter;

@Configuration
public class DatabasesConfiguration {
    @Bean
    @Scope("cucumber-glue")
    public CustomerDatabase customerDatabase() {
        return new InMemoryCustomerDatabaseAdapter();
    }

    @Bean
    @Scope("cucumber-glue")
    public ProjectDatabase projectDatabase() { return new InMemoryProjectDatabaseAdapter(); }

    @Bean
    @Scope("cucumber-glue")
    public CustomerAccountDatabase customerAccountDatabase(){
        return new InMemoryCustomerAccountDatabaseAdapter();
    }

    @Bean
    @Scope("cucumber-glue")
    public SubscriptionDatabase subscriptionDatabase(){
        return new InMemorySubscriptionDatabaseAdapter();
    }

    @Bean
    @Scope("cucumber-glue")
    public CustomerRateDatabase customerRateDatabase() { return new InMemoryCustomerRateDatabaseAdapter(); }

    /*@Bean
    @Scope("cucumber-glue")
    public Pronostic pronosticRepository() { return new InMemoryPronosticRepository(); }*/

    @Bean
    @Scope("cucumber-glue")
    public DisapprovalPronosticDatabase disapprovalPronosticDatabase() { return new InMemoryDisapprovalPronosticDatabaseAdapter(); }

    @Bean
    @Scope("cucumber-glue")
    public PronosticNotificationDatabase notificationRepository() { return new InMemoryNotificationRepositoryAdapter(); }

    @Bean
    @Scope("cucumber-glue")
    public PenaltyCustomerDatabase penaltyCustomerDatabase() { return new InMemoryPenaltyCustomerDatabaseAdapter(); }

}
