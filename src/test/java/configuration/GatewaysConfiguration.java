package configuration;

import com.sfl.flybet.casestudy.domain.gateways.InMemoryAuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class GatewaysConfiguration {
    @Bean
    @Scope("cucumber-glue")
    public AuthenticationCustomerGateway authenticationCustomerGateway() {
        return new InMemoryAuthenticationCustomerGateway();
    }
}
