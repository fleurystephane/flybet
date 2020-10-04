package unit.projects;

import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.EndDateProjectException;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryCustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryCustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryProjectRepository;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.casestudy.domain.exceptions.SoldeInsuffisantException;
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
import java.time.LocalDate;

@RunWith(HierarchicalContextRunner.class)
public class CreateProjectsTest {
    public static final String GESTION_BANKROL_PROJECT_ID = "A1A1";
    private final ProjectRepository massiProjectRepositories = new InMemoryProjectRepository();
    private final CustomerRepository customerRepository = new InMemoryCustomerRepository();
    private final CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
    private Customer tipsterMassi = new Customer("ABC", "Massi");
    private Customer admin = new Customer("ADM", "Admin");
    private ProjectCustomerPort projectCustomerPort = new ProjectCustomer(massiProjectRepositories, customerAccountRepository);
    private Project gestionDeBankrolMassiProject = new Project(tipsterMassi, "Gestion de bankrol", new Amount(new BigDecimal("200.00"), Devise.CREDIT));
    private CustomerAccount adminAccount = new CustomerAccount("ADM", new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
    private CustomerAccount tipsterMassiAccount = new CustomerAccount("ABC", new Amount(new BigDecimal("2000.00"), Devise.CREDIT));

    @Before
    public void beforeTheClass(){
        customerRepository.add(tipsterMassi);
        customerRepository.add(admin);
        customerAccountRepository.add(adminAccount);
        customerAccountRepository.add(tipsterMassiAccount);
    }

    @Test
    public void shouldCreateNewProject () throws ProjectAlreadyExistsException, SoldeInsuffisantException, EndDateProjectException {
        gestionDeBankrolMassiProject.setId(GESTION_BANKROL_PROJECT_ID);
        projectCustomerPort.createProject(gestionDeBankrolMassiProject);

        Assert.assertThat(massiProjectRepositories.all(GESTION_BANKROL_PROJECT_ID), Matchers.notNullValue());
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void shouldThrowExceptionWhenOneProjectWithSameNameAlreadyExists() throws ProjectAlreadyExistsException, SoldeInsuffisantException, EndDateProjectException {
        CustomerAccount adminAccount = new CustomerAccount("ADM", new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
        CustomerAccount tipsterMassiAccount = new CustomerAccount("ABC", new Amount(new BigDecimal("2000.00"), Devise.CREDIT));
        customerAccountRepository.add(adminAccount);
        customerAccountRepository.add(tipsterMassiAccount);

        massiProjectRepositories.add(gestionDeBankrolMassiProject);
        Project gestionDeBankrolMassiProjectBis = new Project(tipsterMassi,"Gestion de bankrol", new Amount(new BigDecimal("100.00"), Devise.CREDIT));
        gestionDeBankrolMassiProject.setId(GESTION_BANKROL_PROJECT_ID);
        projectCustomerPort.createProject(gestionDeBankrolMassiProjectBis);
    }

    @Test(expected = EndDateProjectException.class)
    public void shouldThrowEndDateExceptionWhenCreatingProjectWithEndDateInThePast() throws SoldeInsuffisantException, ProjectAlreadyExistsException, EndDateProjectException {
        Project noelMassiProject = new Project(tipsterMassi,"Noel", new Amount(new BigDecimal("200.00"), Devise.CREDIT), LocalDate.of(2019, 1, 1));
        projectCustomerPort.createProject(noelMassiProject);
    }

}
