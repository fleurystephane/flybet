package acceptance.pronos;

import acceptance.pronos.facilities.PronoAttempt;
import acceptance.pronos.facilities.PublishError;
import com.sfl.flybet.casestudy.domain.exceptions.AlreadyPublishedPronosticException;
import com.sfl.flybet.casestudy.domain.exceptions.BankrolInsufficientException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.ChangeCotePronosticFacade;
import com.sfl.flybet.domain.pronostic.PublishPronosticFacade;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
import com.sfl.flybet.domain.pronostic.ports.incoming.ChangeCotePronostic;
import com.sfl.flybet.domain.pronostic.ports.incoming.PublishPronostic;
import configuration.projects.ProjectContext;
import configuration.projects.ScenarioProjectContext;
import configuration.pronos.PronosContext;
import configuration.pronos.ScenarioPronosticContext;
import io.cucumber.java8.En;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class PublishingPronoSteps implements En {


    public PublishingPronoSteps(ProjectDatabase projectDatabase,
                                AuthenticationCustomerGateway authenticationCustomerGateway,
                                ScenarioPronosticContext scenarioPronosticContext,
                                ScenarioProjectContext scenarioProjectContext) {


        And("^j'ai publié le pronostic \"([^\"]*)\" dans le projet d'id \"([^\"]*)\" et de nom \"([^\"]*)\"$",
                (String pronoId, String projectId, String projectName) -> {
            Pronostic prono = new Pronostic(Long.valueOf(pronoId));
            prono.setStatusProno(null);
            try {
                Project p = new Project("Title...", new Amount(new BigDecimal("500.00"), Devise.EURO),
                        "Objectif...", authenticationCustomerGateway.currentCustomer().get(), Long.valueOf(projectId));
                projectDatabase.add(p);
            } catch (ProjectAlreadyExistsException e) {
                Assert.fail("Le projet ne peut deja exister !");
            }
            projectDatabase.addPronoToProject(Long.valueOf(projectId), prono);

            scenarioPronosticContext.setContextValue(PronosContext.PRONO_TITLE, pronoId);
            scenarioProjectContext.setContextValue(ProjectContext.PROJECT_NAME, projectName);

        });
        And("^j'ai créé un nouveau pronostic \"([^\"]*)\" de cote \"([^\"]*)\" de mise \"([^\"]*)\" \"([^\"]*)\"$",
                (String pronoId, String cote, String mise, String uniteMise) -> {

            Pronostic pronostic = new Pronostic(Long.valueOf(pronoId));
            pronostic.setCote(BigDecimal.valueOf(Float.parseFloat(cote)));
            pronostic.setUniteMise(uniteMise);
            pronostic.setMise(new BigDecimal(mise));
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
                Optional<Pronostic> optPronostic = projectDatabase.findPronosticById(Long.valueOf(pronoId));
                PublishPronostic publishPronostic = new PublishPronosticFacade(projectDatabase);
                Pronostic pronoToPublish = null;
                if(optPronostic.isPresent()){
                    pronoToPublish = optPronostic.get();
                }
                else{
                    pronoToPublish = (Pronostic)scenarioPronosticContext.getContextValue(PronosContext.NEW_PRONO);
                }
                publishPronostic.publish(pronoToPublish, Long.valueOf(projectId));
                setPronoAttemptPublishedInScenarioContext(scenarioPronosticContext, scenarioProjectContext, Long.valueOf(pronoId), projectId);

            } catch (AlreadyPublishedPronosticException | BankrolInsufficientException | IllegalArgumentException e) {
                setConfirmPublishError(scenarioPronosticContext);
            }
                });
        Then("^je vérifie qu'une erreur est remontée$", () -> {
            Assert.assertTrue("Une erreur est attendue ici....", publishErrorIsConfirm(scenarioPronosticContext));
        });
        Then("^je vérifie que la publication est effective$", () -> {
            Set<Pronostic> pronostics = projectDatabase.allPronos(
                    (Long) scenarioProjectContext.getContextValue(ProjectContext.PROJECT_ID));
            Optional<Pronostic> p = pronostics.stream().filter(
                    prono -> prono.getId().equals(
                            ((PronoAttempt)scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT)).getPronostic().getId())).findFirst();
            Assert.assertTrue(p.isPresent());
            Assert.assertTrue(p.get().isPublished());


        });


        And("^j'ai enregistré le pronostic \"([^\"]*)\" de cote \"([^\"]*)\" et de mise \"([^\"]*)\" \"([^\"]*)\" dans le projet \"([^\"]*)\"$",
                (String pronoId, String cote, String mise, String uniteDevise, String projectId) -> {
                    Pronostic prono = new Pronostic(Long.valueOf(pronoId));
                    prono.setCote(BigDecimal.valueOf(Float.parseFloat(cote)));
                    prono.setMise(new BigDecimal(mise));
                    prono.setUniteMise(uniteDevise);
                    prono.setStatusProno(StatusProno.DRAFT);

                    projectDatabase.addPronoToProject(Long.valueOf(projectId), prono);

                    scenarioProjectContext.setContextValue(ProjectContext.PROJECT_ID, Long.valueOf(projectId));
                    scenarioPronosticContext.setContextValue(PronosContext.PRONO_TITLE, pronoId);
                });

        When("^je saisis une nouvelle cote de \"([^\"]*)\" sur le pronostic \"([^\"]*)\"$", (String cote, String pronoId) -> {

            Optional<Pronostic> optPronostic = projectDatabase.findPronosticById(Long.valueOf(pronoId));

            Assert.assertNotNull(optPronostic);
            try {
                ChangeCotePronostic changeCotePronostic = new ChangeCotePronosticFacade(projectDatabase);
                changeCotePronostic.changeCoteValue(optPronostic.get(), BigDecimal.valueOf(Float.parseFloat(cote)));
            } catch (PronosticNotFoundException e) {
                e.printStackTrace();
            }

            PronoAttempt attempt = new PronoAttempt();
            Pronostic pronostic = new Pronostic(Long.valueOf(pronoId));
            pronostic.setCote(optPronostic.get().getCote());
            attempt.setProno(pronostic);
            scenarioPronosticContext.setContextValue(PronosContext.PRONO_ATTEMPT, attempt);
        });
        Then("^je vérifie que le changement de cote est effectif$", () -> {

            PronoAttempt attempt = (PronoAttempt)scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT);

            Assert.assertEquals(projectDatabase.findPronosticById(attempt.getPronostic().getId()).get().getCote(),
                        attempt.getPronostic().getCote());
        });
        Then("^je vérifie que le changement de cote n'est pas effectif$", () -> {
            PronoAttempt attempt = (PronoAttempt)scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT);
            System.out.println("attempt : "+attempt.getPronostic().getCote());
            System.out.println("repository : "+projectDatabase.findPronosticById(attempt.getPronostic().getId()).get().getCote());
            Assert.assertEquals(0, attempt.getPronostic().getCote().compareTo(
                    projectDatabase.findPronosticById(attempt.getPronostic().getId()).get().getCote()
            ));

        });

        Then("^une erreur est remontée car le pronostic n'existe pas$", () -> {
            Assert.assertTrue(scenarioPronosticContext.getContextValue(PronosContext.RETRIEVE_ERROR).getClass()
                    .equals(PronosticNotFoundException.class));
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
                                                           Long pronoId, String projectName) {
        PronoAttempt pronoAttempt = (PronoAttempt) scenarioPronosticContext.getContextValue(PronosContext.PRONO_ATTEMPT);
        if(null == pronoAttempt){
            pronoAttempt = new PronoAttempt();
        }
        pronoAttempt.setProjectName(projectName);
        Pronostic prono = new Pronostic(pronoId);
        prono.setMise(BigDecimal.ZERO);
        prono.setStatusProno(StatusProno.PUBLISHED);
        pronoAttempt.setProno(prono);
        scenarioPronosticContext.setContextValue(PronosContext.PRONO_ATTEMPT, pronoAttempt);
        scenarioPronosticContext.setContextValue(PronosContext.PRONO_TITLE, pronoId);
        scenarioProjectContext.setContextValue(ProjectContext.PROJECT_NAME, projectName);
    }

}
