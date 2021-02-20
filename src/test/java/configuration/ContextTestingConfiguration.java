package configuration;

import io.cucumber.java8.En;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        DatabasesConfiguration.class,
        GatewaysConfiguration.class,
        ConfigurationContext.class
})
public class ContextTestingConfiguration implements En {
}
