package unit.projects;

import com.sfl.flybet.casestudy.domain.gateways.InMemoryAuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.project.ProjectFacade;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.subscription.model.Subscription;
import com.sfl.flybet.domain.subscription.ports.outgoing.SubscriptionDatabase;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import unit.customer.adapter.InMemoryCustomerAccountDatabaseAdapter;
import unit.customer.adapter.InMemoryCustomerDatabaseAdapter;
import unit.customer.adapter.InMemorySubscriptionDatabaseAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@RunWith(HierarchicalContextRunner.class)
public class RetrieveProjectsTest {
    private static final Long ABC = 123L;
    private static final Long AAA = 456L;
    private Customer tipsterMassi = new Customer(ABC, "Massi");
    private Customer joe = new Customer(AAA, "Joe");
    private final CustomerDatabase customerDatabase = new InMemoryCustomerDatabaseAdapter();
    private final CustomerAccountDatabase accountDatabase = new InMemoryCustomerAccountDatabaseAdapter();
    private final ProjectDatabase projectDatabase = new InMemoryProjectDatabaseAdapter();

    private ProjectFacade projectFacade;
    private AuthenticationCustomerGateway authenticationGateway = new InMemoryAuthenticationCustomerGateway();
    private SubscriptionDatabase subscriptionsubscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();

    @Before
    public void beforeTheClass() {
        customerDatabase.add(tipsterMassi);
    }

    @Test
    public void shouldRetrieveAllProjecsOfMyTipsterSubscription() throws AuthorizationException, ProjectAlreadyExistsException {
        authenticationGateway.authenticate(joe);
        subscriptionsubscriptionDatabase.add(new Subscription(joe,tipsterMassi, LocalDate.now(), 12));
        projectFacade = new ProjectFacade(projectDatabase, authenticationGateway, accountDatabase,
                subscriptionsubscriptionDatabase);
        Project gestionDeBankrolMassiProject = new Project(
                "Gestion de bankrol", new Amount(new BigDecimal("200.00"), Devise.CREDIT),
                "Objectif...",tipsterMassi, 1L);
        Project massiSmicProject = new Project(
                "Massi SMIC", new Amount(new BigDecimal("200.00"), Devise.CREDIT),
                "Objectif...",tipsterMassi, 2L);
        projectDatabase.add(gestionDeBankrolMassiProject);
        projectDatabase.add(massiSmicProject);
        Set<Project> projects = projectFacade.getAllProjectsOf(tipsterMassi);
        Assert.assertTrue(projects.size() == 2);
    }
}
