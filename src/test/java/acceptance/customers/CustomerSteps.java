package acceptance.customers;

import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.customerrate.model.CustomerRate;
import com.sfl.flybet.domain.customerrate.ports.outgoing.CustomerRateDatabase;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
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
    public CustomerSteps(CustomerDatabase customerDatabase, AuthenticationCustomerGateway authenticationCustomerGateway,
                         CustomerRateDatabase customerRateDatabase,
                         ProjectDatabase projectDatabase) {

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        Given("^des clients existent:$", (DataTable dataTable) -> {
            List<Map<String, String>> dataMaps = dataTable.asMaps(String.class, String.class);
            dataMaps.forEach(dataMap -> {
                Customer customer = new Customer(Long.valueOf(dataMap.get("id")), dataMap.get("pseudo"));
                if(dataMap.get("nbClaims") != null) {
                    customer.setNbClaims(Integer.parseInt(dataMap.get("nbClaims")));
                }
                customerDatabase.add(customer);
                assertTrue(customerDatabase.all().contains(customer));
            });

        });

        Given("^je suis un client authentifié en tant que \"([^\"]*)\"$", (String pseudo) -> {
            Optional<Customer> optionalCustomer =
                    customerDatabase.all().stream().filter(t -> t.getPseudo().equals(pseudo)).findFirst();
            optionalCustomer.ifPresent(authenticationCustomerGateway::authenticate);
            assertTrue(authenticationCustomerGateway.currentCustomer().isPresent());
        });
        Given("^des tarifs existent:$", (DataTable dataTable) -> {
            List<Map<String, String>> dataMaps = dataTable.asMaps(String.class, String.class);
            dataMaps.forEach(dataMap -> {
                        CustomerRate rate = new CustomerRate(
                                customerDatabase.getCustomerById(Long.valueOf(dataMap.get("id"))).get(),
                                new Amount(new BigDecimal(dataMap.get("rate")), Devise.CREDIT), Integer.parseInt(dataMap.get("duration"))
                        );
                        customerRateDatabase.add(rate);
            }
            );
        });
        Given("^des pronostics existent:$", (DataTable dataTable) -> {
            List<Map<String, String>> dataMaps = dataTable.asMaps(String.class, String.class);
            dataMaps.forEach(dataMap -> {
                Pronostic pronostic = new Pronostic(Long.valueOf(dataMap.get("pronoId")));
                pronostic.setCote(new BigDecimal(dataMap.get("cote")));
                if(null != dataMap.get("mise") && !dataMap.get("mise").isEmpty()) {
                    System.out.println("MISE = {"+ dataMap.get("mise")+"}");
                    pronostic.setMise(new BigDecimal(dataMap.get("mise")));
                    pronostic.setUniteMise(dataMap.get("uniteMise"));
                }
                pronostic.setStatusProno(StatusProno.valueOf(dataMap.get("status")));

                projectDatabase.addPronoToProject(Long.valueOf(dataMap.get("project")), pronostic);
            });
        });
        Given("^des projets existent$", (DataTable dataTable) -> {
            List<Map<String, String>> dataMaps = dataTable.asMaps(String.class, String.class);
            dataMaps.forEach(dataMap -> {
                try {
                    Project project = new Project(
                            dataMap.get("title"),
                            new Amount(new BigDecimal(dataMap.get("bankrolInit")), Devise.EURO),
                            dataMap.get("objectif"),
                            customerDatabase.getCustomerById(Long.valueOf(dataMap.get("customerId"))).get(),
                            Long.valueOf(dataMap.get("projectId")));
                    if(dataMap.get("endDate") != null && !dataMap.get("endDate").isEmpty())
                        project.setEndProject(LocalDate.parse(dataMap.get("endDate"), dtf));
                    projectDatabase.add(project);
                } catch (ProjectAlreadyExistsException e) {
                    fail("Erreur lors de la déclaration des projets...");
                }
            });
        });

    }
}
