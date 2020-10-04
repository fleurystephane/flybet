package unit.projects;

import com.github.npathai.hamcrestopt.OptionalMatchers;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryCustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryCustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryProjectRepository;
import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyStartedException;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectNotFoundException;
import com.sfl.flybet.casestudy.domain.ports.project.ProjectCustomerPort;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import com.sfl.flybet.casestudy.domain.adapters.ProjectCustomer;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Optional;

@RunWith(HierarchicalContextRunner.class)
public class UpdateProjectsTest {
    public static final String FUN_PROJECT_ID = "FUN_PROJECT_ID";
    private final ProjectRepository massiProjectRepositories = new InMemoryProjectRepository();
    private final CustomerRepository customerRepository = new InMemoryCustomerRepository();
    private final CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
    private Customer tipsterMassi = new Customer("ABC", "Massi");
    private Customer admin = new Customer("ADM", "Admin");
    private ProjectCustomerPort projectCustomerPort = new ProjectCustomer(massiProjectRepositories, customerAccountRepository);
    private Project gestionDeBankrolMassiProject = new Project(tipsterMassi,"Gestion de bankrol", new Amount(new BigDecimal("200.00"), Devise.CREDIT));
    private Project funProject = new Project(tipsterMassi, "fun", new Amount(new BigDecimal("200.00"), Devise.CREDIT));
    private CustomerAccount adminAccount = new CustomerAccount("ADM", new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
    private CustomerAccount tipsterMassiAccount = new CustomerAccount("ABC", new Amount(new BigDecimal("2000.00"), Devise.CREDIT));

    @Before
    public void beforeTheClass(){
        customerRepository.add(tipsterMassi);
        customerRepository.add(admin);
        customerAccountRepository.add(adminAccount);
        customerAccountRepository.add(tipsterMassiAccount);
        funProject.setId(FUN_PROJECT_ID);
    }


    @Test
    public void shouldChangeObjectifOfProject() throws ProjectAlreadyStartedException, ProjectNotFoundException, ProjectAlreadyExistsException {
        funProject.setObjectif("Nothing special...");
        massiProjectRepositories.add(funProject);
        projectCustomerPort.changeObjectifFor(funProject, "Atteindre 300 Euros en 3 semaines");
        Optional<Project> p = massiProjectRepositories.byId(FUN_PROJECT_ID);
        Assert.assertThat(p.get().getObjectif(), Matchers.equalTo("Atteindre 300 Euros en 3 semaines"));
    }

    @Test(expected=ProjectAlreadyStartedException.class)
    public void shouldNotChangeObjectifOfProjectContainingProno() throws ProjectAlreadyStartedException, ProjectNotFoundException, ProjectAlreadyExistsException {
        funProject.setObjectif("Nothing special...");
        massiProjectRepositories.add(funProject);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, new Pronostic("AAA"));
        projectCustomerPort.changeObjectifFor(funProject, "Atteindre 300 Euros en 3 semaines");
        Assert.fail("Exception aurait du etre levée!!");
    }

    public class DeleteProjectTest {
        @Test
        public void shouldDeleteProject() throws ProjectAlreadyExistsException, ProjectAlreadyStartedException, ProjectNotFoundException {
            massiProjectRepositories.add(funProject);
            projectCustomerPort.deleteProject(funProject);
            Assert.assertThat(massiProjectRepositories.byId(FUN_PROJECT_ID), OptionalMatchers.isEmpty());
        }

        @Test(expected = ProjectAlreadyStartedException.class)
        public void shouldNotDeleteProject() throws ProjectAlreadyExistsException, ProjectAlreadyStartedException, ProjectNotFoundException {
            massiProjectRepositories.add(funProject);
            massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, new Pronostic("AAA"));
            projectCustomerPort.deleteProject(funProject);
            Assert.fail("Exception aurait du etre levée!!");
        }
    }
}
