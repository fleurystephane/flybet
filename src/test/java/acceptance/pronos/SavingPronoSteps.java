package acceptance.pronos;

import acceptance.pronos.facilities.PronoAttempt;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.domain.ports.pronostic.PronosticPublicationPort;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import com.sfl.flybet.casestudy.domain.adapters.PublishProno;
import configuration.projects.ProjectContext;
import configuration.projects.ScenarioProjectContext;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class SavingPronoSteps implements En {

    public SavingPronoSteps(ProjectRepository projectRepository,
                            AuthenticationCustomerGateway authenticationCustomerGateway,
                            ScenarioPronosticContext scenarioPronosticContext,
                            ScenarioProjectContext scenarioProjectContext) {



        Then("^je vérifie que l'enregistrement est effectif$", () -> {
            Set<Pronostic> pronostics = projectRepository.all((String) scenarioProjectContext.getContextValue(ProjectContext.PROJECT_NAME));
            Optional<Pronostic> pronoFound = pronostics.stream().filter(pronostic -> pronostic.equals(
                    ((PronoAttempt) scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT)).getPronostic()
            )).findFirst();
            Assert.assertTrue(pronoFound.isPresent());
            Assert.assertTrue(pronoFound.get().isDraft());
        });
        When("^je tente d'enregistrer le pronostic \"([^\"]*)\" et de cote \"([^\"]*)\" dans le projet \"([^\"]*)\"$",
                (String pronoId, String cote, String projectId) -> {
                    Pronostic pr = new Pronostic(pronoId);
                    pr.setCote(new BigDecimal(Float.parseFloat(cote)));
                    PronosticPublicationPort publishProno = new PublishProno(authenticationCustomerGateway.currentCustomer().get(),
                            projectRepository);
                    publishProno.save(pr, projectId);
                    setPronoAttemptInScenarioContext(scenarioPronosticContext, scenarioProjectContext, pronoId, projectId);
                });

    }

    private void setPronoAttemptInScenarioContext(ScenarioPronosticContext scenarioPronosticContext, ScenarioProjectContext scenarioProjectContext,
                                                  String pronoId, String projectId) {
        PronoAttempt pronoAttempt = (PronoAttempt) scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT);
        if(null == pronoAttempt){
            pronoAttempt = new PronoAttempt();
        }
        pronoAttempt.setProjectId(projectId);
        pronoAttempt.setProno(new Pronostic(pronoId));
        scenarioPronosticContext.setContextValue(PronosContext.PRONO_ATTEMPT, pronoAttempt);
        scenarioPronosticContext.setContextValue(PronosContext.PRONO_TITLE, pronoId);
        scenarioProjectContext.setContextValue(ProjectContext.PROJECT_NAME, projectId);
    }
}
