package configuration;

import configuration.account.ScenarioAccountContext;
import configuration.projects.ScenarioProjectContext;
import configuration.pronos.ScenarioPronosticContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ConfigurationContext {
    @Bean
    @Scope("cucumber-glue")
    public ScenarioPronosticContext scenarioPronosticContext() {
        return new ScenarioPronosticContext();
    }
    @Bean
    @Scope("cucumber-glue")
    public ScenarioProjectContext scenarioProjectContext() {
        return new ScenarioProjectContext();
    }
    @Bean
    @Scope("cucumber-glue")
    public ScenarioAccountContext scenarioAccountContext() {
        return new ScenarioAccountContext();
    }
}
