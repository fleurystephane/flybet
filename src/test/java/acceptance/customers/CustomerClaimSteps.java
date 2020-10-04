package acceptance.customers;

import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.StatusProno;
import com.sfl.flybet.casestudy.domain.exceptions.*;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.domain.ports.reliability.PronosticReliabilityPort;
import com.sfl.flybet.casestudy.domain.repositories.PenaltyRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.DisapprovalRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.NotificationRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import com.sfl.flybet.casestudy.domain.adapters.PronosticReliability;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.Optional;

public class CustomerClaimSteps implements En {
    public CustomerClaimSteps(ProjectRepository projectRepository, DisapprovalRepository disapprovalRepository,
                              AuthenticationCustomerGateway authenticationCustomerGateway,
                              ScenarioPronosticContext scenarioPronosticContext,
                              NotificationRepository notificationRepository,
                              PenaltyRepository penaltyRepository, CustomerRepository customerRepository) {
        PronosticReliabilityPort pronosticReliabilityPort =
                new PronosticReliability(authenticationCustomerGateway, projectRepository,
                        disapprovalRepository, notificationRepository, penaltyRepository);

        And("^le pronostic \"([^\"]*)\" a été validé gagnant$", (String pronoId) -> {
            Optional<Pronostic> pronostic = projectRepository.findPronosticById(pronoId);
            pronostic.ifPresent(pronostic1 -> pronostic1.setStatus(StatusProno.WON));
        });
        Then("^le pronostic \"([^\"]*)\" contient (\\d+) désapprobation$", (String pronoId, Integer nbClaims) -> {
            Assert.assertThat(disapprovalRepository.countDisapprouval(projectRepository.findPronosticById(pronoId).get()), Matchers.equalTo(Integer.toUnsignedLong(nbClaims)));
        });
        And("^je ne dispose plus que de (\\d+) revendications$", (Integer nbClaims) -> {
            Assert.assertThat(pronosticReliabilityPort.getDisapprovalRaminingCounter(authenticationCustomerGateway.currentCustomer().get()), Matchers.equalTo(nbClaims));
        });
        Then("^une erreur est remontée indiquant que le pronostic est déjà confirmé$", () -> {
            Assert.assertThat(PronosticReliabilityPort.PRONOSTIC_IS_NOT_IN_DISAPPROVALABLE_STATUS,
                    Matchers.equalTo(scenarioPronosticContext.getContextValue(PronosContext.NOT_DISAPPROVALABLE)));
        });
        When("^je tente de déclarer gagnant le pronostic \"([^\"]*)\"$", (String pronoId) -> {
            try {
                pronosticReliabilityPort.declarePronosticWon(pronoId);
            } catch (PronosticNotFoundException e) {
                Assert.fail("PronosticNotFoundException!!");
            } catch (PronosticNotDecidableException e) {
                scenarioPronosticContext.setContextValue(PronosContext.NOT_DECIDABLE, projectRepository.findPronosticById(pronoId).get());
            }
        });
        And("^le pronostic \"([^\"]*)\" est gagnant$", (String pronoId) -> {
            Assert.assertThat(projectRepository.findPronosticById(pronoId).get().getStatusProno(), Matchers.equalTo(StatusProno.WON));
        });
        Then("^une notification a été envoyée$", () -> {
            Assert.assertThat(notificationRepository.all(), Matchers.hasSize(1));
        });
        Then("^une erreur est remontée indiquant que le pronostic doit être publié$", () -> {
            Assert.assertThat(scenarioPronosticContext.getContextValue(PronosContext.NOT_DECIDABLE), Matchers.instanceOf(Pronostic.class));
        });
        When("^je déclare Perdant le pronostic \"([^\"]*)\" de \"([^\"]*)\" déclaré gagnant$", (String pronoId, String pseudoTipster) -> {
            Optional<Pronostic> pronosticOptional = projectRepository.findPronosticById(pronoId);
            if(pronosticOptional.isPresent()){
                if(pronosticOptional.get().isDecidedWon()){
                    try {
                        pronosticReliabilityPort.changePronosticToLostAsAdmin(customerRepository.byPseudo(pseudoTipster).get(), pronosticOptional.get());
                    } catch (AuthorisationException e) {
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
    }
}
