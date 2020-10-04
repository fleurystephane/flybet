package acceptance.reliability;

import com.sfl.flybet.casestudy.domain.Disapproval;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.adapters.PronosticReliability;
import com.sfl.flybet.casestudy.domain.exceptions.AlreadyDisapprovedException;
import com.sfl.flybet.casestudy.domain.exceptions.DisapprovalableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.domain.ports.reliability.PronosticReliabilityPort;
import com.sfl.flybet.casestudy.domain.repositories.PenaltyRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.DisapprovalRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.NotificationRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.Optional;

public class DisapprovalSteps implements En {
    public DisapprovalSteps(ProjectRepository projectRepository, DisapprovalRepository disapprovalRepository,
                            AuthenticationCustomerGateway authenticationCustomerGateway,
                            ScenarioPronosticContext scenarioPronosticContext,
                            NotificationRepository notificationRepository,
                            CustomerRepository customerRepository,
                            PenaltyRepository penaltyRepository) {

        PronosticReliabilityPort pronosticReliabilityPort = new PronosticReliability(authenticationCustomerGateway, projectRepository,
                disapprovalRepository, notificationRepository, penaltyRepository);

        And("^j'ai posé une désapprobation sur le pronostic \"([^\"]*)\"$", (String pronoId) -> {
            Optional<Pronostic> pronostic = projectRepository.findPronosticById(pronoId);
            if(!(pronostic.isPresent())){
                scenarioPronosticContext.setContextValue(PronosContext.RETRIEVE_ERROR, new PronosticNotFoundException());
                return;
            }
            if(pronostic.get().isDisapprovalable()) {
                Disapproval disapproval = new Disapproval(
                        pronostic.get(), authenticationCustomerGateway.currentCustomer().get());
                disapprovalRepository.add(disapproval);
            }
            else{
                scenarioPronosticContext.setContextValue(PronosContext.NOT_DISAPPROVALABLE, pronostic.get());
            }

        });

        And("^\"([^\"]*)\" a posé une désapprobation sur le pronostic \"([^\"]*)\"$", (String pseudo, String pronoId) -> {
            Optional<Pronostic> pronostic = projectRepository.findPronosticById(pronoId);
            if(!(pronostic.isPresent())){
                scenarioPronosticContext.setContextValue(PronosContext.RETRIEVE_ERROR, new PronosticNotFoundException());
                return;
            }
            if(pronostic.get().isDisapprovalable()) {
                Disapproval disapproval = new Disapproval(
                        pronostic.get(), customerRepository.byPseudo(pseudo).get());
                disapprovalRepository.add(disapproval);
            }
        });

        And("^il y a (\\d+) désapprobations sur le pronostic \"([^\"]*)\"$", (Integer nbDisapprovals, String pronoId) -> {
            Optional<Pronostic> pronosticOptional = projectRepository.findPronosticById(pronoId);
            if(pronosticOptional.isPresent() && pronosticOptional.get().isDisapprovalable()){
                Disapproval disapproval1 = new Disapproval(
                        pronosticOptional.get(), null);
                Disapproval disapproval2 = new Disapproval(
                        pronosticOptional.get(), null);
                Disapproval disapproval3 = new Disapproval(
                        pronosticOptional.get(), null);
                disapprovalRepository.add(disapproval1);
                disapprovalRepository.add(disapproval2);
                disapprovalRepository.add(disapproval3);
            }
        });

        When("^je tente de désapprouver le pronostic \"([^\"]*)\"$", (String pronoId) -> {
            Optional<Pronostic> pronostic = null;

            pronostic = projectRepository.findPronosticById(pronoId);
            if(pronostic.isPresent()) {
                try {
                    pronosticReliabilityPort.disapprovePronostic(authenticationCustomerGateway.currentCustomer().get(),
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
            Assert.assertThat(PronosticReliabilityPort.IMPOSSIBLE_TO_DISAPPROVE_OWN_PRONOSTIC,
                    Matchers.equalTo(scenarioPronosticContext.getContextValue(PronosContext.NOT_DISAPPROVALABLE)));
        });

        Then("^une erreur est remontée indiquant que j'ai déjà désapprouvé ce pronostic$", () -> {
            Assert.assertThat(scenarioPronosticContext.getContextValue(PronosContext.ALREADY_DISAPPROVED),
                    Matchers.instanceOf(Pronostic.class));
        });


        Then("^toutes les désapprobations sur le pronostic \"([^\"]*)\" sont restituées$", (String pronoId) -> {
            Optional<Pronostic> pronostic = projectRepository.findPronosticById(pronoId);
            Assert.assertThat(disapprovalRepository.countDisapprouval(pronostic.get()), Matchers.equalTo(0L));
        });
        Then("^une erreur est remontée car le pronostic n'est pas désapprouvable$", () -> {
            Assert.assertThat(PronosticReliabilityPort.PRONOSTIC_IS_NOT_IN_DISAPPROVALABLE_STATUS,
                    Matchers.equalTo(scenarioPronosticContext.getContextValue(PronosContext.NOT_DISAPPROVALABLE)));
        });
        Then("^une erreur est remontée car je ne suis plus autorisé à désapprouver un pronostic$", () -> {
            Assert.assertThat(PronosticReliabilityPort.NO_DISAPPROVAL_REMAINING,
                    Matchers.equalTo(scenarioPronosticContext.getContextValue(PronosContext.NOT_DISAPPROVALABLE)));
        });
    }
}
