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
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyStartedException;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
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
import java.util.Optional;

@RunWith(HierarchicalContextRunner.class)
public class UpdateProjectsTest {
    public static final Long FUN_PROJECT_ID = 200L;
    private static final String GLOBAL_PROJECT_ID = "Global-1";
    private static final Long ABC = 123L;
    private static final Long ADM = 0L;
    private static final Long AAA = 1L;
    private static final BigDecimal MISE_10E = BigDecimal.TEN;
    private static final BigDecimal COTE_2E50 = new BigDecimal("2.50");


    private final ProjectDatabase projectDatabase = new InMemoryProjectDatabaseAdapter();
    private final CustomerDatabase customerDatabase = new InMemoryCustomerDatabaseAdapter();
    private final CustomerAccountDatabase customerAccountDatabase = new InMemoryCustomerAccountDatabaseAdapter();
    private Customer tipsterMassi = new Customer(ABC, "Massi");
    private Customer admin = new Customer(ADM, "Admin");
    private AuthenticationCustomerGateway authenticationCustomerGateway = new InMemoryAuthenticationCustomerGateway();
    private SubscriptionDatabase subscriptionDatabase = new InMemorySubscriptionDatabaseAdapter();
    private ProjectFacade projectFacade = new ProjectFacade(projectDatabase, authenticationCustomerGateway,
            customerAccountDatabase, subscriptionDatabase);
    private Project gestionDeBankrolMassiProject = new Project(
            "Gestion de bankrol", new Amount(new BigDecimal("200.00"), Devise.CREDIT),
            "Objectif...",tipsterMassi, 1L);
    private Project funProject = new Project(
            "fun", new Amount(new BigDecimal("200.00"), Devise.CREDIT),
            "Objectif...", tipsterMassi, FUN_PROJECT_ID);
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
    public void shouldChangeObjectifOfProject() throws ProjectAlreadyExistsException, ProjectAlreadyStartedException, ProjectNotFoundException, AuthorizationException {
        funProject.setObjectif("Nothing special...");
        projectDatabase.add(funProject);
        authenticationCustomerGateway.authenticate(tipsterMassi);
        funProject.setObjectif("Atteindre 300 Euros en 3 semaines");
        projectFacade.update(funProject);
        Optional<Project> p = projectDatabase.byId(FUN_PROJECT_ID);
        Assert.assertTrue(p.isPresent());
        Assert.assertEquals("Atteindre 300 Euros en 3 semaines", p.get().getObjectif());
    }

    @Test(expected=ProjectAlreadyStartedException.class)
    public void shouldNotChangeObjectifOfProjectContainingProno() throws ProjectAlreadyExistsException, ProjectAlreadyStartedException, ProjectNotFoundException, AuthorizationException {
        funProject.setObjectif("Nothing special...");
        authenticationCustomerGateway.authenticate(tipsterMassi);
        projectDatabase.add(funProject);
        Pronostic pronostic = new Pronostic(AAA, StatusProno.PUBLISHED, COTE_2E50, MISE_10E, "EURO");
        projectDatabase.addPronoToProject(FUN_PROJECT_ID, pronostic);
        funProject.setObjectif("Atteindre 300 Euros en 3 semaines");
        projectFacade.update(funProject);
        Assert.fail("Exception aurait du etre levée!!");
    }

    public class DeleteProjectTest {
        @Test
        public void shouldDeleteProject() throws ProjectAlreadyExistsException, ProjectAlreadyStartedException, ProjectNotFoundException, AuthorizationException {
            authenticationCustomerGateway.authenticate(tipsterMassi);
            projectDatabase.add(funProject);
            projectFacade.remove(funProject);
            Assert.assertEquals(projectDatabase.byId(FUN_PROJECT_ID), Optional.empty());
        }

        @Test(expected = ProjectAlreadyStartedException.class)
        public void shouldNotDeleteProject() throws ProjectAlreadyExistsException, ProjectAlreadyStartedException, ProjectNotFoundException, AuthorizationException {
            authenticationCustomerGateway.authenticate(tipsterMassi);
            projectDatabase.add(funProject);
            Pronostic pronostic = new Pronostic(AAA, StatusProno.PUBLISHED, COTE_2E50, MISE_10E, "EURO");
            projectDatabase.addPronoToProject(FUN_PROJECT_ID, pronostic);
            projectFacade.remove(funProject);
            Assert.fail("Exception aurait du etre levée!!");
        }
    }
}
