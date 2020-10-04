package configuration;

import com.sfl.flybet.casestudy.domain.gateways.InMemoryCustomerRateRepository;
import com.sfl.flybet.casestudy.domain.repositories.PenaltyRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.*;
import com.sfl.flybet.casestudy.infrastructure.ports.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RepositoriesConfiguration {
    @Bean
    @Scope("cucumber-glue")
    public CustomerRepository customerRepository() {
        return new InMemoryCustomerRepository();
    }

    @Bean
    @Scope("cucumber-glue")
    public ProjectRepository projectRepository() { return new InMemoryProjectRepository(); }

    @Bean
    @Scope("cucumber-glue")
    public CustomerAccountRepository customerAccountRepository(){
        return new InMemoryCustomerAccountRepository();
    }

    @Bean
    @Scope("cucumber-glue")
    public SubscriptionRepository subscriptionRepository(){
        return new InMemorySubscriptionRepository();
    }

    @Bean
    @Scope("cucumber-glue")
    public CustomerRateRepository customerRateRepository() { return new InMemoryCustomerRateRepository(); }

    @Bean
    @Scope("cucumber-glue")
    public PronosticRepository pronosticRepository() { return new InMemoryPronosticRepository(); }

    @Bean
    @Scope("cucumber-glue")
    public DisapprovalRepository disapprovalRepository() { return new InMemoryDisapprovalRepository(); }

    @Bean
    @Scope("cucumber-glue")
    public NotificationRepository notificationRepository() { return new InMemoryNotificationRepository(); }

    @Bean
    @Scope("cucumber-glue")
    public PenaltyRepository penaltyRepository() { return new InMemoryPenaltyRepository(); }

}
