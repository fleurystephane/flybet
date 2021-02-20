package acceptance.pronos;

import acceptance.pronos.facilities.PronoAttempt;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.PublishPronosticFacade;
import com.sfl.flybet.domain.pronostic.SavePronosticFacade;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.ports.incoming.PublishPronostic;
import com.sfl.flybet.domain.pronostic.ports.incoming.SavePronostic;
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

    public SavingPronoSteps(ProjectDatabase projectDatabase,
                            AuthenticationCustomerGateway authenticationCustomerGateway,
                            ScenarioPronosticContext scenarioPronosticContext,
                            ScenarioProjectContext scenarioProjectContext) {



        Then("^je vérifie que l'enregistrement est effectif$", () -> {
            Set<Pronostic> pronostics = projectDatabase.allPronos((Long) scenarioProjectContext.getContextValue(ProjectContext.PROJECT_ID));
            Optional<Pronostic> pronoFound = pronostics.stream().filter(pronostic -> pronostic.getId().equals(
                    ((PronoAttempt) scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT)).getPronostic().getId()
            )).findFirst();
            Assert.assertTrue(pronoFound.isPresent());
            Assert.assertTrue(pronoFound.get().isDraft());
        });
        When("^je tente d'enregistrer le pronostic \"([^\"]*)\" et de cote \"([^\"]*)\" dans le projet \"([^\"]*)\"$",
                (String pronoId, String cote, String projectId) -> {
                    Pronostic pr = new Pronostic(Long.valueOf(pronoId));
                    pr.setCote(new BigDecimal(Float.parseFloat(cote)));
                    SavePronostic savePronostic = new SavePronosticFacade(projectDatabase);
                    /*PronosticPublicationPort publishProno = new PublishProno(authenticationCustomerGateway.currentCustomer().get(),
                            projectDatabase);*/
                    savePronostic.save(pr, Long.valueOf(projectId));
                    setPronoAttemptInScenarioContext(scenarioPronosticContext, scenarioProjectContext, pronoId, projectId);
                });
        When("^je tente d'enregistrer le pronostic \"([^\"]*)\" dans le projet \"([^\"]*)\"$", (String pronoId, String projectId) -> {
            Pronostic prono = (Pronostic) scenarioPronosticContext.getContextValue(PronosContext.NEW_PRONO);
            SavePronostic savePronostic = new SavePronosticFacade(projectDatabase);
            savePronostic.save(prono, Long.valueOf(projectId));
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
        pronoAttempt.setProno(new Pronostic(Long.valueOf(pronoId)));
        scenarioPronosticContext.setContextValue(PronosContext.PRONO_ATTEMPT, pronoAttempt);
        scenarioPronosticContext.setContextValue(PronosContext.PRONO_TITLE, pronoId);
        scenarioProjectContext.setContextValue(ProjectContext.PROJECT_ID, Long.valueOf(projectId));
    }
}
