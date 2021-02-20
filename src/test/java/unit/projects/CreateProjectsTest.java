package unit.projects;


import com.sfl.flybet.casestudy.domain.gateways.InMemoryAuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.project.ProjectFacade;
import com.sfl.flybet.domain.project.exceptions.EndDateProjectException;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.exceptions.SoldeInsuffisantException;
import com.sfl.flybet.domain.project.model.CreateProjectCommand;
import com.sfl.flybet.domain.project.model.ProjectIdentifier;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
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

@RunWith(HierarchicalContextRunner.class)
public class CreateProjectsTest {
    public static final Long GESTION_BANKROL_PROJECT_ID = 1010L;
    private static final Long ABC = 123L;
    private static final Long ADM = 0L;
    private final ProjectDatabase massiProjectDatabase = new InMemoryProjectDatabaseAdapter();
    private final CustomerDatabase customerDatabase = new InMemoryCustomerDatabaseAdapter();
    private final CustomerAccountDatabase customerAccountDatabase = new InMemoryCustomerAccountDatabaseAdapter();
    private Customer tipsterMassi = new Customer(ABC, "Massi");
    private Customer admin = new Customer(ADM, "Admin");
    private AuthenticationCustomerGateway authenticationCustomerGateway = new InMemoryAuthenticationCustomerGateway();
    private SubscriptionDatabase subscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();
    private ProjectFacade projectFacade = new ProjectFacade(massiProjectDatabase, authenticationCustomerGateway,
            customerAccountDatabase, subscriptionDatabase);
    private CreateProjectCommand gestionDeBankrolMassiProject =
            new CreateProjectCommand("Gestion de bankrol", new Amount(new BigDecimal("200.00"), Devise.CREDIT),
                    LocalDate.now(), "Objectif...", tipsterMassi);
    private CustomerAccount adminAccount = new CustomerAccount(ADM, new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
    private CustomerAccount tipsterMassiAccount = new CustomerAccount(ABC, new Amount(new BigDecimal("2000.00"), Devise.CREDIT));

    @Before
    public void beforeTheClass(){
        customerDatabase.add(tipsterMassi);
        customerDatabase.add(admin);
        customerAccountDatabase.addAccount(adminAccount);
        customerAccountDatabase.addAccount(tipsterMassiAccount);
    }

    @Test
    public void shouldCreateNewProject ()
            throws ProjectAlreadyExistsException, SoldeInsuffisantException, EndDateProjectException, AuthorizationException, ProjectNotFoundException {
        authenticationCustomerGateway.authenticate(tipsterMassi);
        ProjectIdentifier projectIdentifier = projectFacade.create(gestionDeBankrolMassiProject);
        Assert.assertNotNull(massiProjectDatabase.allPronos(projectIdentifier.getId()));
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void shouldThrowExceptionWhenOneProjectWithSameNameAlreadyExists() throws ProjectAlreadyExistsException, SoldeInsuffisantException, EndDateProjectException, AuthorizationException {
        authenticationCustomerGateway.authenticate(tipsterMassi);
        CustomerAccount adminAccount = new CustomerAccount(ADM, new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
        CustomerAccount tipsterMassiAccount = new CustomerAccount(ABC, new Amount(new BigDecimal("2000.00"), Devise.CREDIT));
        customerAccountDatabase.addAccount(adminAccount);
        customerAccountDatabase.addAccount(tipsterMassiAccount);

        ProjectIdentifier projectIdentifier = projectFacade.create(gestionDeBankrolMassiProject);
        CreateProjectCommand gestionDeBankrolMassiProjectBis =
                new CreateProjectCommand("Gestion de bankrol", new Amount(new BigDecimal("100.00"), Devise.CREDIT),
                        LocalDate.now(), "Objectif...", tipsterMassi);
        projectFacade.create(gestionDeBankrolMassiProjectBis);
    }

    @Test(expected = EndDateProjectException.class)
    public void shouldThrowEndDateExceptionWhenCreatingProjectWithEndDateInThePast() throws SoldeInsuffisantException, ProjectAlreadyExistsException, EndDateProjectException, AuthorizationException {
        authenticationCustomerGateway.authenticate(tipsterMassi);
        CreateProjectCommand noelMassiProject = new CreateProjectCommand(
                "Noel", new Amount(new BigDecimal("200.00"), Devise.CREDIT),
                LocalDate.of(2019, 1, 1), "Objectif.....", tipsterMassi);
        projectFacade.create(noelMassiProject);
    }

}
