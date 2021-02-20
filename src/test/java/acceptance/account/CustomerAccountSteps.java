package acceptance.account;


import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import configuration.account.AccountContext;
import configuration.account.ScenarioAccountContext;
import io.cucumber.java8.En;
import org.hamcrest.Matchers;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;

public class CustomerAccountSteps implements En {

    public CustomerAccountSteps(AuthenticationCustomerGateway authenticationCustomerGateway,
                                CustomerAccountDatabase customerAccountDatabase,
                                CustomerDatabase customerDatabase,
                                ScenarioAccountContext scenarioAccountContext) {
        And("^le solde de mon compte est de \"([^\"]*)\" crédits$", (String solde) -> {
            authenticationCustomerGateway.currentCustomer().ifPresent(customer -> {
                CustomerAccount expectedCustomerAccount = new CustomerAccount(
                        customer.getId(), new Amount(new BigDecimal(Float.parseFloat(solde)), com.sfl.flybet.domain.common.model.Devise.CREDIT)
                );

                if(shouldInitRepository(customerAccountDatabase)){
                    customerAccountDatabase.addAccount(expectedCustomerAccount);
                    scenarioAccountContext.setContextValue(AccountContext.CUSTOMER_ACCOUNT, expectedCustomerAccount.getBalance().getValue());
                }
                else{
                    assertThat(expectedCustomerAccount.getBalance(), Matchers.comparesEqualTo(
                            customerAccountDatabase.byId(customer.getId()).get().getBalance()));
                }
            });
        });
        And("^le solde du compte de \"([^\"]*)\" est alimenté$", (String pseudo) -> {
            if(pseudo.equals("Admin")){
                assertThat(customerAccountDatabase.byId(customerDatabase.getCustomerByPseudo(pseudo).get().getId()).get().getBalance().getValue(),
                        Matchers.greaterThan(
                                (BigDecimal) scenarioAccountContext.getContextValue(AccountContext.ADMIN_ACCOUNT))
                );

                System.out.println("L'Admin a un solde de : " + customerAccountDatabase.byId(customerDatabase.getCustomerByPseudo("Admin")
                        .get().getId()).get().getBalance().getValue());
            }
            else {
                assertThat(customerAccountDatabase.byId(customerDatabase.getCustomerByPseudo(pseudo).get().getId()).get().getBalance().getValue(),
                        Matchers.greaterThan(
                                (BigDecimal) scenarioAccountContext.getContextValue(AccountContext.CUSTOMER_ACCOUNT))
                );
                System.out.println("Le Tipster "+pseudo+" a un solde de : " + customerAccountDatabase.byId(customerDatabase.getCustomerByPseudo(pseudo)
                        .get().getId()).get().getBalance().getValue());
            }
        });
        And("^le solde du compte de \"([^\"]*)\" est de \"([^\"]*)\" crédits$",
                (String pseudo, String solde) -> {
                    CustomerAccount expectedCustomerAccount = new CustomerAccount(
                            customerDatabase.getCustomerByPseudo(pseudo).get().getId(),
                            new Amount(new BigDecimal(Float.parseFloat(solde)), Devise.CREDIT)
                    );
                    customerAccountDatabase.addAccount(expectedCustomerAccount);
                    if(pseudo.equals("Admin")) {
                        scenarioAccountContext.setContextValue(AccountContext.ADMIN_ACCOUNT, expectedCustomerAccount.getBalance().getValue());
                    }else{
                        scenarioAccountContext.setContextValue(AccountContext.CUSTOMER_ACCOUNT, expectedCustomerAccount.getBalance().getValue());
                    }
        });
        And("^le solde du compte de \"([^\"]*)\" est débité$", (String pseudo) -> {
            System.out.println("Le Tipster "+pseudo+" a un solde de : " + customerAccountDatabase.byId(customerDatabase.getCustomerByPseudo(pseudo)
                    .get().getId()).get().getBalance().getValue());
            assertThat(customerAccountDatabase.byId(customerDatabase.getCustomerByPseudo(pseudo).get().getId()).get().getBalance().getValue(),
                    Matchers.lessThan(
                            (BigDecimal) scenarioAccountContext.getContextValue(AccountContext.CUSTOMER_ACCOUNT))
            );
        });

    }

    private boolean shouldInitRepository(CustomerAccountDatabase customerAccountDatabase) {
        return customerAccountDatabase.all().isEmpty();
    }
}
