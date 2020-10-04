package acceptance.customers;

import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRateRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerSteps implements En {
    public CustomerSteps(CustomerRepository customerRepository, AuthenticationCustomerGateway authenticationCustomerGateway,
                         CustomerRateRepository customerRateRepository,
                         ProjectRepository projectRepository) {

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        Given("^des clients existent:$", (DataTable dataTable) -> {
            List<Map<String, String>> dataMaps = dataTable.asMaps(String.class, String.class);
            dataMaps.forEach(dataMap -> {
                Customer customer = new Customer(dataMap.get("id"), dataMap.get("pseudo"));
                customerRepository.add(customer);
                assertTrue(customerRepository.all().contains(customer));
            });

        });

        Given("^je suis un client authentifié en tant que \"([^\"]*)\"$", (String pseudo) -> {
            Optional<Customer> optionalCustomer =
                    customerRepository.all().stream().filter(t -> t.getPseudo().equals(pseudo)).findFirst();
            optionalCustomer.ifPresent(authenticationCustomerGateway::authenticate);
            assertTrue(authenticationCustomerGateway.currentCustomer().isPresent());
        });
        Given("^des tarifs existent:$", (DataTable dataTable) -> {
            List<Map<String, String>> dataMaps = dataTable.asMaps(String.class, String.class);
            dataMaps.forEach(dataMap -> {
                        CustomerRate rate = new CustomerRate(
                                customerRepository.byId(dataMap.get("id")).get(),
                                new Amount(new BigDecimal(dataMap.get("rate")), Devise.CREDIT), Integer.parseInt(dataMap.get("duration"))
                        );
                        customerRateRepository.add(rate);
            }
            );
        });
        Given("^des pronostics existent:$", (DataTable dataTable) -> {
            List<Map<String, String>> dataMaps = dataTable.asMaps(String.class, String.class);
            dataMaps.forEach(dataMap -> {
                Pronostic pronostic = new Pronostic(dataMap.get("pronoId"));
                pronostic.setCote(new BigDecimal(dataMap.get("cote")));
                pronostic.setStatus(StatusProno.valueOf(dataMap.get("status")));
                projectRepository.addPronoToProject(dataMap.get("project"), pronostic);
            });
        });
        Given("^des projets existent$", (DataTable dataTable) -> {
            List<Map<String, String>> dataMaps = dataTable.asMaps(String.class, String.class);
            dataMaps.forEach(dataMap -> {
                try {
                    Project project = new Project(customerRepository.byId(dataMap.get("customerId")).get(),
                            dataMap.get("title"), new Amount(new BigDecimal(dataMap.get("bankrol")), Devise.EURO));
                    project.setId(dataMap.get("projectId"));
                    if(dataMap.get("endDate") != null && !dataMap.get("endDate").isEmpty())
                        project.setEndProject(LocalDate.parse(dataMap.get("endDate"), dtf));
                    project.setObjectif(dataMap.get("objectif"));
                    projectRepository.add(project);
                } catch (ProjectAlreadyExistsException e) {
                    fail("Erreur lors de la déclaration des projets...");
                }
            });
        });

    }
}
