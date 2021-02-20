package acceptance.customers;

import com.sfl.flybet.domain.customer.CustomerFacade;
import com.sfl.flybet.domain.customer.ports.incoming.RetrieveCustomer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import configuration.customer.CustomerContext;
import configuration.customer.ScenarioCustomerContext;
import io.cucumber.java8.En;
import org.junit.Assert;

public class RechercheTipsterSteps implements En {

    public RechercheTipsterSteps(CustomerDatabase customerDatabase, ScenarioCustomerContext scenarioCustomerContext) {
        /* La facade : la classe utilisée par l'application pour piloter le DOMAINE */
        RetrieveCustomer retrieveCustomer = new CustomerFacade(customerDatabase);


        When("^je recherche un tipster avec le pseudo \"([^\"]*)\"$", (String pseudo) -> {
            retrieveCustomer.retrieveCustomerByPseudo(pseudo).ifPresent(customer -> {
                scenarioCustomerContext.setContextValue(CustomerContext.CUSTOMER_FOUND, customer);
            });
        });
        Then("^je vérifie que j'obtiens le tipster d'id (\\d+)$", (Integer tipsterId) -> {
            Assert.assertNotNull("Je devrais obtenir le tipster d'id "+tipsterId, scenarioCustomerContext.getContextValue(CustomerContext.CUSTOMER_FOUND));
            Assert.assertSame("Le CUSTOMER_FOUND dans le contexte n'a pas l'id attendu "+tipsterId, scenarioCustomerContext.getContextValue(CustomerContext.CUSTOMER_FOUND).getId(), tipsterId.longValue());
        });
        Then("^je vérifie que je n'obtiens aucun tipster$", () -> {
            Assert.assertNull(scenarioCustomerContext.getContextValue(CustomerContext.CUSTOMER_FOUND));
        });

    }
}
