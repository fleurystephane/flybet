package acceptance.pronos;

import acceptance.pronos.facilities.PronoAttempt;
import acceptance.pronos.facilities.PublishError;
import com.sfl.flybet.casestudy.domain.Project;
import com.sfl.flybet.casestudy.domain.exceptions.AlreadyPublishedPronosticException;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.StatusProno;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.domain.ports.pronostic.PronosticPublicationPort;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import com.sfl.flybet.casestudy.domain.adapters.PublishProno;
import configuration.projects.ProjectContext;
import configuration.projects.ScenarioProjectContext;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class PublishingPronoSteps implements En {


    public PublishingPronoSteps(ProjectRepository projectRepository,
                                AuthenticationCustomerGateway authenticationCustomerGateway,
                                ScenarioPronosticContext scenarioPronosticContext,
                                ScenarioProjectContext scenarioProjectContext) {


        And("^j'ai publié le pronostic \"([^\"]*)\" dans le projet \"([^\"]*)\"$", (String pronoId, String projectName) -> {
            Pronostic prono = new Pronostic(pronoId);
            prono.setStatus(null);
            try {
                projectRepository.add(new Project(authenticationCustomerGateway.currentCustomer().get(),projectName));
            } catch (ProjectAlreadyExistsException e) {
                Assert.fail("Le projet ne peut deja exister !");
            }
            projectRepository.addPronoToProject(projectName, prono);

            scenarioPronosticContext.setContextValue(PronosContext.PRONO_TITLE, pronoId);
            scenarioProjectContext.setContextValue(ProjectContext.PROJECT_NAME, projectName);

        });

        And("^j'ai créé un nouveau pronostic \"([^\"]*)\" de cote \"([^\"]*)\"$", (String pronoId, String cote) -> {
            Pronostic pronostic = new Pronostic(pronoId);
            pronostic.setCote(BigDecimal.valueOf(Float.parseFloat(cote)));
            scenarioPronosticContext.setContextValue(PronosContext.NEW_PRONO, pronostic);
        });

        When("^je tente de publier le pronostic \"([^\"]*)\" dans le projet \"([^\"]*)\"$",
                (String pronoId, String projectId) -> {

            /*
            Soit le pronostic à publier est déjà dans le background
            Soit il est nouveau et donc dans le context
             */
            try {
                Assert.assertFalse(publishErrorIsConfirm(scenarioPronosticContext));
                Optional<Pronostic> optPronostic = projectRepository.findPronosticById(pronoId);
                PronosticPublicationPort publishProno = new PublishProno(authenticationCustomerGateway.currentCustomer().get(),
                        projectRepository);
                if(optPronostic.isPresent()){
                    publishProno.publish(optPronostic.get(), projectId);
                }
                else{
                    Pronostic prono = (Pronostic)scenarioPronosticContext.getContextValue(PronosContext.NEW_PRONO);
                    publishProno.publish(prono, projectId);
                }

                setPronoAttemptPublishedInScenarioContext(scenarioPronosticContext, scenarioProjectContext, pronoId, projectId);

            } catch (AlreadyPublishedPronosticException | IllegalArgumentException e) {
                setConfirmPublishError(scenarioPronosticContext);
            }
                });
        Then("^je vérifie qu'une erreur est remontée$", () -> {
            Assert.assertTrue("Une erreur est attendue ici....", publishErrorIsConfirm(scenarioPronosticContext));
        });
        Then("^je vérifie que la publication est effective$", () -> {
            Set<Pronostic> pronostics = projectRepository.all(
                    (String) scenarioProjectContext.getContextValue(ProjectContext.PROJECT_NAME));
            Optional<Pronostic> p = pronostics.stream().filter(
                    prono -> prono.getId().equals(
                            ((PronoAttempt)scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT)).getPronostic().getId())).findFirst();
            Assert.assertTrue(p.isPresent());
            Assert.assertTrue(p.get().isPublished());


        });


        And("^j'ai enregistré le pronostic \"([^\"]*)\" de cote \"([^\"]*)\" dans le projet \"([^\"]*)\"$",
                (String pronoId, String cote, String projectId) -> {
            Pronostic prono = new Pronostic(pronoId);
            prono.setCote(BigDecimal.valueOf(Float.parseFloat(cote)));

            projectRepository.addPronoToProject(projectId, prono);

            scenarioProjectContext.setContextValue(ProjectContext.PROJECT_NAME, projectId);
            scenarioPronosticContext.setContextValue(PronosContext.PRONO_TITLE, pronoId);
        });

        When("^je saisis une nouvelle cote de \"([^\"]*)\" sur le pronostic \"([^\"]*)\"$", (String cote, String pronoId) -> {

            Optional<Pronostic> optPronostic = optPronostic = projectRepository.findPronosticById(pronoId);

            Assert.assertNotNull(optPronostic);
            try {
                PronosticPublicationPort publishProno = new PublishProno(authenticationCustomerGateway.currentCustomer().get(),
                        projectRepository);
                publishProno.changeCoteValue(optPronostic.get(),
                        BigDecimal.valueOf(Float.parseFloat(cote)));
            } catch (PronosticNotFoundException e) {
                e.printStackTrace();
            }

            PronoAttempt attempt = new PronoAttempt();
            Pronostic pronostic = new Pronostic(pronoId);
            pronostic.setCote(BigDecimal.valueOf(Float.parseFloat(cote)));
            attempt.setProno(pronostic);
            scenarioPronosticContext.setContextValue(PronosContext.PRONO_ATTEMPT, attempt);
        });
        Then("^je vérifie que le changement de cote est effectif$", () -> {

            PronoAttempt attempt = (PronoAttempt)scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT);

            Assert.assertEquals(projectRepository.findPronosticById(attempt.getPronostic().getId()).get().getCote(),
                        attempt.getPronostic().getCote());
        });
        Then("^je vérifie que le changement de cote n'est pas effectif$", () -> {
            PronoAttempt attempt = (PronoAttempt)scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT);
            Assert.assertThat(attempt.getPronostic().getCote(),
                    Matchers.not(Matchers.equalTo(projectRepository.findPronosticById(attempt.getPronostic().getId()).get().getCote())));

        });

        Then("^une erreur est remontée car le pronostic n'existe pas$", () -> {
            Assert.assertThat(scenarioPronosticContext.getContextValue(PronosContext.RETRIEVE_ERROR),
                    Matchers.instanceOf(PronosticNotFoundException.class));
        });


    }

    private void setConfirmPublishError(ScenarioPronosticContext scenarioPronosticContext) {
        PublishError publishError = (PublishError) scenarioPronosticContext.getContextValue(PronosContext.PUBLISH_ERROR);
        if (null == publishError) {
            publishError = new PublishError();
        }
        publishError.confirmError();
        scenarioPronosticContext.setContextValue(PronosContext.PUBLISH_ERROR, publishError);
    }

    private boolean publishErrorIsConfirm(ScenarioPronosticContext scenarioPronosticContext) {
        try {
            return ((PublishError) scenarioPronosticContext.getContextValue(PronosContext.PUBLISH_ERROR)).isConfirm();
        }catch(NullPointerException npe){
            return false;
        }
    }

    private void setPronoAttemptPublishedInScenarioContext(ScenarioPronosticContext scenarioPronosticContext,
                                                           ScenarioProjectContext scenarioProjectContext,
                                                           String pronoId, String projectName) {
        PronoAttempt pronoAttempt = (PronoAttempt) scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT);
        if(null == pronoAttempt){
            pronoAttempt = new PronoAttempt();
        }
        pronoAttempt.setProjectName(projectName);
        Pronostic prono = new Pronostic(pronoId);
        prono.setStatus(StatusProno.PUBLISHED);
        pronoAttempt.setProno(prono);
        scenarioPronosticContext.setContextValue(PronosContext.PRONO_ATTEMPT, pronoAttempt);
        scenarioPronosticContext.setContextValue(PronosContext.PRONO_TITLE, pronoId);
        scenarioProjectContext.setContextValue(ProjectContext.PROJECT_NAME, projectName);
    }

}
