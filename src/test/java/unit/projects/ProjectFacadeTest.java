package unit.projects;

import com.sfl.flybet.casestudy.domain.gateways.InMemoryAuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.project.ProjectFacade;
import com.sfl.flybet.domain.project.exceptions.EndDateProjectException;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.exceptions.SoldeInsuffisantException;
import com.sfl.flybet.domain.project.model.CreateProjectCommand;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.incoming.CreateProject;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.subscription.ports.outgoing.SubscriptionDatabase;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import unit.customer.adapter.InMemoryCustomerAccountDatabaseAdapter;
import unit.customer.adapter.InMemorySubscriptionDatabaseAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;

@RunWith(HierarchicalContextRunner.class)
public class ProjectFacadeTest {
    static final Long GESTION_BANKROL_PROJECT_ID = 1L;

    private Customer adminCustomer = new Customer(0L, "ADMIN");
    private CustomerAccount adminCustomerAccount = new CustomerAccount(0L, new Amount(new BigDecimal("2000000.00"), Devise.EURO));
    private Customer tipsterMassi = new Customer(1L, "Massi");

    private ProjectDatabase projectDatabase = new InMemoryProjectDatabaseAdapter();
    private AuthenticationCustomerGateway authenticationCustomerGateway = new InMemoryAuthenticationCustomerGateway();
    private CustomerAccountDatabase customerAccountDatabase = new InMemoryCustomerAccountDatabaseAdapter();
    private SubscriptionDatabase subscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();
    private CreateProject createProjectFacade = new ProjectFacade(projectDatabase, authenticationCustomerGateway,
            customerAccountDatabase, subscriptionDatabase);

    @Before
    public void beforeTheClass() {
        customerAccountDatabase.addAccount(adminCustomerAccount);
    }

    @Test
    public void shouldCreateNewProject () throws ProjectAlreadyExistsException, SoldeInsuffisantException,
            EndDateProjectException, AuthorizationException {
        authenticationCustomerGateway.authenticate(tipsterMassi);
        CustomerAccount massiAccount = new CustomerAccount(1L, new Amount(new BigDecimal("2000.00"), Devise.EURO));
        customerAccountDatabase.addAccount(massiAccount);

        CreateProjectCommand cpc = new CreateProjectCommand(
                "Gestion de bankrol",
                new Amount(new BigDecimal("200.00"), Devise.CREDIT),
                LocalDate.of(2040,1,1),
                "Objectif...",
                tipsterMassi
        );

        createProjectFacade.create(cpc);

    }
}
