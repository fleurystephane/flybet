package acceptance.reliability;

import com.sfl.flybet.casestudy.domain.exceptions.AlreadyDisapprovedException;
import com.sfl.flybet.casestudy.domain.exceptions.DisapprovalableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.DisapprovalPronosticFacade;
import com.sfl.flybet.domain.pronostic.model.Disapproval;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.ports.incoming.DisapprovalPronostic;
import com.sfl.flybet.domain.pronostic.ports.outgoing.DisapprovalPronosticDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PenaltyCustomerDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PronosticNotificationDatabase;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import org.junit.Assert;

import java.util.Optional;

public class DisapprovalSteps implements En {
    public DisapprovalSteps(ProjectDatabase projectDatabase, DisapprovalPronosticDatabase disapprovalPronosticDatabase,
                            AuthenticationCustomerGateway authenticationCustomerGateway,
                            ScenarioPronosticContext scenarioPronosticContext,
                            PronosticNotificationDatabase pronosticNotificationDatabase,
                            CustomerDatabase customerDatabase,
                            PenaltyCustomerDatabase penaltyCustomerDatabase) {

        DisapprovalPronostic disapprovalPronosticFacade = new DisapprovalPronosticFacade(projectDatabase, disapprovalPronosticDatabase);

        And("^j'ai posé une désapprobation sur le pronostic \"([^\"]*)\"$", (String pronoId) -> {
            Optional<Pronostic> pronostic = projectDatabase.findPronosticById(Long.valueOf(pronoId));
            if(!(pronostic.isPresent())){
                scenarioPronosticContext.setContextValue(PronosContext.RETRIEVE_ERROR, new PronosticNotFoundException());
                return;
            }
            if(pronostic.get().isDisapprovalable()) {
                Disapproval disapproval = new Disapproval(
                        pronostic.get(), authenticationCustomerGateway.currentCustomer().get());
                disapprovalPronosticDatabase.add(disapproval);
            }
            else{
                scenarioPronosticContext.setContextValue(PronosContext.NOT_DISAPPROVALABLE, pronostic.get());
            }

        });

        And("^\"([^\"]*)\" a posé une désapprobation sur le pronostic \"([^\"]*)\"$", (String pseudo, String pronoId) -> {
            Optional<Pronostic> pronostic = projectDatabase.findPronosticById(Long.valueOf(pronoId));
            if(!(pronostic.isPresent())){
                scenarioPronosticContext.setContextValue(PronosContext.RETRIEVE_ERROR, new PronosticNotFoundException());
                return;
            }
            if(pronostic.get().isDisapprovalable()) {
                Disapproval disapproval = new Disapproval(
                        pronostic.get(), customerDatabase.getCustomerByPseudo(pseudo).get());
                disapprovalPronosticDatabase.add(disapproval);
            }
        });

        And("^il y a (\\d+) désapprobations sur le pronostic \"([^\"]*)\"$", (Integer nbDisapprovals, String pronoId) -> {
            Optional<Pronostic> pronosticOptional = projectDatabase.findPronosticById(Long.valueOf(pronoId));
            if(pronosticOptional.isPresent() && pronosticOptional.get().isDisapprovalable()){
                Disapproval disapproval1 = new Disapproval(
                        pronosticOptional.get(), null);
                Disapproval disapproval2 = new Disapproval(
                        pronosticOptional.get(), null);
                Disapproval disapproval3 = new Disapproval(
                        pronosticOptional.get(), null);
                disapprovalPronosticDatabase.add(disapproval1);
                disapprovalPronosticDatabase.add(disapproval2);
                disapprovalPronosticDatabase.add(disapproval3);
            }
        });

        When("^je tente de désapprouver le pronostic \"([^\"]*)\"$", (String pronoId) -> {
            Optional<Pronostic> pronostic = null;

            pronostic = projectDatabase.findPronosticById(Long.valueOf(pronoId));
            if(pronostic.isPresent()) {
                try {
                    disapprovalPronosticFacade.disapprovePronostic(authenticationCustomerGateway.currentCustomer().get(),
                            pronostic.get());
                } catch (PronosticNotFoundException e) {
                    scenarioPronosticContext.setContextValue(PronosContext.RETRIEVE_ERROR, e);
                } catch (DisapprovalableException e) {
                    scenarioPronosticContext.setContextValue(PronosContext.NOT_DISAPPROVALABLE, e.getMessage());
                } catch (AlreadyDisapprovedException e) {
                    scenarioPronosticContext.setContextValue(PronosContext.ALREADY_DISAPPROVED, pronostic.get());
                }
            }
            else{
                scenarioPronosticContext.setContextValue(PronosContext.RETRIEVE_ERROR, new PronosticNotFoundException());
            }

        });

        Then("^une erreur est remontée car on ne peut pas désapprouver son propre pronostic$", () -> {
            Assert.assertTrue(DisapprovalPronosticFacade.IMPOSSIBLE_TO_DISAPPROVE_OWN_PRONOSTIC.equals(
                    scenarioPronosticContext.getContextValue(PronosContext.NOT_DISAPPROVALABLE)));
        });

        Then("^une erreur est remontée indiquant que j'ai déjà désapprouvé ce pronostic$", () -> {
            Assert.assertTrue(scenarioPronosticContext.getContextValue(PronosContext.ALREADY_DISAPPROVED)
                    .getClass().equals(Pronostic.class));
        });


        Then("^toutes les désapprobations sur le pronostic \"([^\"]*)\" sont restituées$", (String pronoId) -> {
            Optional<Pronostic> pronostic = projectDatabase.findPronosticById(Long.valueOf(pronoId));
            Assert.assertTrue(disapprovalPronosticDatabase.countDisapprouval(pronostic.get()) == 0L);
        });
        Then("^une erreur est remontée car le pronostic n'est pas désapprouvable$", () -> {
            Assert.assertTrue(DisapprovalPronosticFacade.PRONOSTIC_IS_NOT_IN_DISAPPROVALABLE_STATUS.equals(
                    scenarioPronosticContext.getContextValue(PronosContext.NOT_DISAPPROVALABLE)
                    ));
        });
        Then("^une erreur est remontée car je ne suis plus autorisé à désapprouver un pronostic$", () -> {
            Assert.assertTrue(DisapprovalPronosticFacade.NO_DISAPPROVAL_REMAINING.equals(
                    scenarioPronosticContext.getContextValue(PronosContext.NOT_DISAPPROVALABLE)
                    ));
        });
    }
}
