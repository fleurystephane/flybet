package acceptance.account;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.CustomerAccount;
import com.sfl.flybet.casestudy.domain.Devise;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;
import configuration.account.AccountContext;
import configuration.account.ScenarioAccountContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import org.hamcrest.Matchers;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;

public class CustomerAccountSteps implements En {

    public CustomerAccountSteps(AuthenticationCustomerGateway authenticationCustomerGateway,
                                CustomerAccountRepository customerAccountRepository,
                                CustomerRepository customerRepository,
                                ScenarioPronosticContext scenarioPronosticContext,
                                ScenarioAccountContext scenarioAccountContext) {
        And("^le solde de mon compte est de \"([^\"]*)\" crédits$", (String solde) -> {
            authenticationCustomerGateway.currentCustomer().ifPresent(customer -> {
                CustomerAccount expectedCustomerAccount = new CustomerAccount(
                        customer.getId(), new Amount(new BigDecimal(Float.parseFloat(solde)), Devise.CREDIT)
                );

                if(shouldInitRepository(customerAccountRepository)){
                    customerAccountRepository.add(expectedCustomerAccount);
                    scenarioAccountContext.setContextValue(AccountContext.CUSTOMER_ACCOUNT, expectedCustomerAccount.getBalance().getValue());
                }
                else{
                    assertThat(expectedCustomerAccount.getBalance(), Matchers.comparesEqualTo(
                            customerAccountRepository.byId(customer.getId()).get().getBalance()));
                }
            });
        });
        And("^le solde du compte de \"([^\"]*)\" est alimenté$", (String pseudo) -> {
            if(pseudo.equals("Admin")){
                assertThat(customerAccountRepository.byId(customerRepository.byPseudo(pseudo).get().getId()).get().getBalance().getValue(),
                        Matchers.greaterThan(
                                (BigDecimal) scenarioAccountContext.getContextValue(AccountContext.ADMIN_ACCOUNT))
                );

                System.out.println("L'Admin a un solde de : " + customerAccountRepository.byId(customerRepository.byPseudo("Admin")
                        .get().getId()).get().getBalance().getValue());
            }
            else {
                assertThat(customerAccountRepository.byId(customerRepository.byPseudo(pseudo).get().getId()).get().getBalance().getValue(),
                        Matchers.greaterThan(
                                (BigDecimal) scenarioAccountContext.getContextValue(AccountContext.CUSTOMER_ACCOUNT))
                );
                System.out.println("Le Tipster "+pseudo+" a un solde de : " + customerAccountRepository.byId(customerRepository.byPseudo(pseudo)
                        .get().getId()).get().getBalance().getValue());
            }
        });
        And("^le solde du compte de \"([^\"]*)\" est de \"([^\"]*)\" crédits$",
                (String pseudo, String solde) -> {
                    CustomerAccount expectedCustomerAccount = new CustomerAccount(
                            customerRepository.byPseudo(pseudo).get().getId(),
                            new Amount(new BigDecimal(Float.parseFloat(solde)), Devise.CREDIT)
                    );
                    customerAccountRepository.add(expectedCustomerAccount);
                    if(pseudo.equals("Admin")) {
                        scenarioAccountContext.setContextValue(AccountContext.ADMIN_ACCOUNT, expectedCustomerAccount.getBalance().getValue());
                    }else{
                        scenarioAccountContext.setContextValue(AccountContext.CUSTOMER_ACCOUNT, expectedCustomerAccount.getBalance().getValue());
                    }
        });
        And("^le solde du compte de \"([^\"]*)\" est débité$", (String pseudo) -> {
            System.out.println("Le Tipster "+pseudo+" a un solde de : " + customerAccountRepository.byId(customerRepository.byPseudo(pseudo)
                    .get().getId()).get().getBalance().getValue());
            assertThat(customerAccountRepository.byId(customerRepository.byPseudo(pseudo).get().getId()).get().getBalance().getValue(),
                    Matchers.lessThan(
                            (BigDecimal) scenarioAccountContext.getContextValue(AccountContext.CUSTOMER_ACCOUNT))
            );
        });


    }

    private boolean shouldInitRepository(CustomerAccountRepository customerAccountRepository) {
        return customerAccountRepository.all().isEmpty();
    }
}
