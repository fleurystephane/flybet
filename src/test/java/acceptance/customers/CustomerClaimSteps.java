package acceptance.customers;

import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotDecidableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.DisapprovalPronosticFacade;
import com.sfl.flybet.domain.pronostic.ManageResultPronosticFacade;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
import com.sfl.flybet.domain.pronostic.ports.incoming.DisapprovalPronostic;
import com.sfl.flybet.domain.pronostic.ports.incoming.ManageResultPronostic;
import com.sfl.flybet.domain.pronostic.ports.outgoing.DisapprovalPronosticDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PenaltyCustomerDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PronosticNotificationDatabase;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import org.junit.Assert;

import java.util.Optional;

public class CustomerClaimSteps implements En {
    public CustomerClaimSteps(ProjectDatabase projectDatabase, DisapprovalPronosticDatabase disapprovalPronosticDatabase,
                              AuthenticationCustomerGateway authenticationCustomerGateway,
                              ScenarioPronosticContext scenarioPronosticContext,
                              PronosticNotificationDatabase pronosticNotificationDatabase,
                              PenaltyCustomerDatabase penaltyCustomerDatabase, CustomerDatabase customerDatabase) {
        DisapprovalPronostic disapprovalPronostic =
                new DisapprovalPronosticFacade(projectDatabase, disapprovalPronosticDatabase);
        ManageResultPronostic manageResultPronosticFacade =
                new ManageResultPronosticFacade(projectDatabase, penaltyCustomerDatabase,pronosticNotificationDatabase,
                        authenticationCustomerGateway, disapprovalPronosticDatabase);

        And("^le pronostic \"([^\"]*)\" a été validé gagnant$", (String pronoId) -> {
            Optional<Pronostic> pronostic = projectDatabase.findPronosticById(Long.valueOf(pronoId));
            pronostic.ifPresent(pronostic1 -> pronostic1.setStatusProno(StatusProno.WON));
        });
        Then("^le pronostic \"([^\"]*)\" contient (\\d+) désapprobation$", (String pronoId, Integer nbClaims) -> {
            Assert.assertTrue(disapprovalPronosticDatabase.countDisapprouval(
                    projectDatabase.findPronosticById(Long.valueOf(pronoId)).get())==Integer.toUnsignedLong(nbClaims));
        });
        And("^je ne dispose plus que de (\\d+) revendications$", (Integer nbClaims) -> {
            Assert.assertTrue(disapprovalPronostic.getDisapprovalRaminingCounter(
                    authenticationCustomerGateway.currentCustomer().get()) == nbClaims);
        });
        Then("^une erreur est remontée indiquant que le pronostic est déjà confirmé$", () -> {
            Assert.assertTrue(DisapprovalPronosticFacade.PRONOSTIC_IS_NOT_IN_DISAPPROVALABLE_STATUS.equals(
                    scenarioPronosticContext.getContextValue(PronosContext.NOT_DISAPPROVALABLE)));
        });
        When("^je tente de déclarer gagnant le pronostic \"([^\"]*)\"$", (String pronoId) -> {
            try {
                manageResultPronosticFacade.declarePronosticWon(Long.valueOf(pronoId));
            } catch (PronosticNotFoundException e) {
                Assert.fail("PronosticNotFoundException!!");
            } catch (PronosticNotDecidableException e) {
                scenarioPronosticContext.setContextValue(PronosContext.NOT_DECIDABLE,
                        projectDatabase.findPronosticById(Long.valueOf(pronoId)).get());
            }
        });
        And("^le pronostic \"([^\"]*)\" est gagnant$", (String pronoId) -> {
            Assert.assertTrue(projectDatabase.findPronosticById(Long.valueOf(pronoId)).get().getStatusProno() == StatusProno.WON);
        });

        Then("^une erreur est remontée indiquant que le pronostic doit être publié$", () -> {
            Assert.assertTrue(scenarioPronosticContext.getContextValue(PronosContext.NOT_DECIDABLE).getClass().equals(Pronostic.class));
        });
        When("^je déclare Perdant le pronostic \"([^\"]*)\" de \"([^\"]*)\" déclaré gagnant$", (String pronoId, String pseudoTipster) -> {
            Optional<Pronostic> pronosticOptional = projectDatabase.findPronosticById(Long.valueOf(pronoId));
            if(pronosticOptional.isPresent()){
                if(pronosticOptional.get().isDecidedWon()){
                    try {
                        manageResultPronosticFacade.changePronosticToLostAsAdmin(customerDatabase.getCustomerByPseudo(pseudoTipster).get(),
                                pronosticOptional.get());
                    } catch (AuthorizationException e) {
                        Assert.fail("Pas les autorisations pour changer en Perdant un prono déclaré gagnant");
                    } catch (PronosticNotFoundException e) {
                        scenarioPronosticContext.setContextValue(PronosContext.RETRIEVE_ERROR, e);
                    }
                }
                else{
                    Assert.fail("Pronostic devrait être au status WON...");
                }
            }
            else{
                Assert.fail("Pronostic inexistant!!!");
            }
        });
        Then("^une notification a été envoyée sur le pronostic \"([^\"]*)\"$", (String pronoId) -> {
            Assert.assertTrue(pronosticNotificationDatabase.all(Long.valueOf(pronoId)).size() == 1);
        });
    }
}
