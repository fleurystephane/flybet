package acceptance.projects;

import acceptance.projects.facilities.ProjectAttempt;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.project.ProjectFacade;
import com.sfl.flybet.domain.project.exceptions.*;
import com.sfl.flybet.domain.project.model.CreateProjectCommand;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.model.ProjectIdentifier;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.subscription.ports.outgoing.SubscriptionDatabase;
import configuration.authorization.AuthorizationContext;
import configuration.authorization.ScenarioAuthorizationContext;
import configuration.projects.ProjectContext;
import configuration.projects.ScenarioProjectContext;
import io.cucumber.java8.En;
import org.junit.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectSteps implements En {

    public ProjectSteps(AuthenticationCustomerGateway authenticationCustomerGateway, ProjectDatabase projectDatabase,
                        CustomerAccountDatabase customerAccountDatabase, CustomerDatabase customercustomerDatabase,
                        SubscriptionDatabase subscriptionDatabase,
                        ScenarioProjectContext scenarioProjectContext, ScenarioAuthorizationContext scenarioAuthorizationContext) {
        final ProjectFacade projectFacade = new ProjectFacade(projectDatabase, authenticationCustomerGateway, customerAccountDatabase, subscriptionDatabase);

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        When("^je tente de créer un projet \"([^\"]*)\" avec une bankrol de \"([^\"]*)\" euros$", (String projectTitle, String bankrol) -> {
            Amount bkAmount = new Amount(new BigDecimal(bankrol), Devise.CREDIT);
            try {

                Project project = new Project(projectTitle, bkAmount, "Objectif", authenticationCustomerGateway.currentCustomer().get(), 456L);
                CreateProjectCommand createProjectCommand =
                        new CreateProjectCommand(projectTitle, bkAmount, LocalDate.now().plusDays(30),
                                "Objectif", authenticationCustomerGateway.currentCustomer().get());
                ProjectIdentifier projectIdentifier = projectFacade.create(createProjectCommand);
                ProjectAttempt projectAttempt = new ProjectAttempt(projectIdentifier.getId(), projectTitle, bkAmount);
                scenarioProjectContext.setContextValue(ProjectContext.NEW_PROJECT, projectAttempt);
            } catch (ProjectAlreadyExistsException | SoldeInsuffisantException e) {
                scenarioProjectContext.setContextValue(ProjectContext.CREATE_ERROR, e);
            } catch (EndDateProjectException e) {
                fail(e.getMessage());
            }
        });
        When("^je tente de créer un projet \"([^\"]*)\" avec une bankrol de \"([^\"]*)\" euros et une fin le \"([^\"]*)\"$",
                (String projectTitle, String bankrol, String dateFin) -> {
                    Amount bkAmount = new Amount(new BigDecimal(bankrol), Devise.CREDIT);
                    try {
                        CreateProjectCommand projectCommand = new CreateProjectCommand(projectTitle, bkAmount,
                                LocalDate.parse(dateFin, dtf), "Objectif",
                                authenticationCustomerGateway.currentCustomer().get());
                        ProjectIdentifier identifier = projectFacade.create(projectCommand);
                        ProjectAttempt projectAttempt = new ProjectAttempt(identifier.getId(), projectTitle, bkAmount);
                        scenarioProjectContext.setContextValue(ProjectContext.NEW_PROJECT, projectAttempt);
                    } catch (ProjectAlreadyExistsException | SoldeInsuffisantException | EndDateProjectException e) {
                        scenarioProjectContext.setContextValue(ProjectContext.CREATE_ERROR, e);
                    }
        });
        Then("^la création du projet est effective$", () -> {
            ProjectAttempt creationProjectAttempt = (ProjectAttempt) scenarioProjectContext.getContextValue(ProjectContext.NEW_PROJECT);

            assertNotNull(projectDatabase.byId(creationProjectAttempt.getId()));
        });
        And("^le nombre total de projet de \"([^\"]*)\" est de (\\d+)$", (String pseudo, Integer nbProjects) -> {
            assertEquals(projectDatabase.allProjects(authenticationCustomerGateway.currentCustomer().get()).size(), (int) nbProjects);
        });

        Then("^une erreur est remontée car un projet existe déjà pour ce titre$", () -> {
            assertEquals(scenarioProjectContext.getContextValue(ProjectContext.CREATE_ERROR).getClass(), ProjectAlreadyExistsException.class);
        });
        And("^le projet \"([^\"]*)\" contient (\\d+) pronostic$", (String projectId, Integer nbPronos) -> {
            if(nbPronos == 0){

            }
            assertEquals(projectDatabase.allPronos(Long.valueOf(projectId)).size(), (int) nbPronos);
        });

        Then("^la modification du projet est effective$", () -> {
            ProjectAttempt creationProjectAttempt = (ProjectAttempt) scenarioProjectContext.getContextValue(ProjectContext.UPDATE_PROJECT);
            assertEquals(creationProjectAttempt.getObjectif(), projectDatabase.byId(creationProjectAttempt.getId()).get().getObjectif());
        });
        When("^je tente de modifier l'objectif du projet \"([^\"]*)\" comme étant \"([^\"]*)\"$", (String projectId, String objectif) -> {
            Project project = retrieveProject(projectId, projectDatabase);
            try {
                project.setObjectif(objectif);
                projectFacade.update(project);
                ProjectAttempt projectAttempt = new ProjectAttempt(project.getId(),projectId, project.getBankrolInit());
                projectAttempt.setObjectif(objectif);
                projectAttempt.setEndProject(project.getEndProject());
                scenarioProjectContext.setContextValue(ProjectContext.UPDATE_PROJECT, projectAttempt);
            } catch (ProjectNotFoundException | ProjectAlreadyStartedException e) {
                scenarioProjectContext.setContextValue(ProjectContext.UPDATE_ERROR, e);
            } catch (AuthorizationException authorizationException){
                scenarioAuthorizationContext.setContextValue(AuthorizationContext.NOT_PROJECT_OWNER, authorizationException.getMessage());
            }
        });
        Then("^une erreur est remontée car le projet est déjà débuté$", () -> {
            assertTrue(scenarioProjectContext.getContextValue(ProjectContext.UPDATE_ERROR).getClass().equals(ProjectAlreadyStartedException.class));
        });
        And("^je dispose du projet \"([^\"]*)\" contenant (\\d+) pronostic$", (String projectTitle, Integer nbPronos) -> {
            /*Project globalProject = new Project(authenticationCustomerGateway.currentCustomer().get(),projectTitle);
            try {
                projectRepository.add(globalProject);
                for (int i = 0; i < nbPronos; i++)
                    projectRepository.addPronoToProject(projectTitle, new Pronostic("Prono-" + i));
            } catch (ProjectAlreadyExistsException e) {
                Assert.fail("Le projet ne peut exister déjà !");
            }*/
        });
        When("^je tente de modifier la bankrol du projet \"([^\"]*)\" en spécifiant \"([^\"]*)\" euros$",
                (String projectId, String newBankrol) -> {
                    Project project = retrieveProject(projectId, projectDatabase);
                    try {
                        project.setBankrolInit(new Amount(new BigDecimal(newBankrol), Devise.EURO));
                        projectFacade.update(project);
                        ProjectAttempt projectAttempt = new ProjectAttempt(
                                project.getId(),project.getProjectTitle(),
                                new Amount(new BigDecimal(newBankrol), Devise.EURO));
                        scenarioProjectContext.setContextValue(ProjectContext.UPDATE_PROJECT, projectAttempt);
                    } catch (ProjectAlreadyStartedException | ProjectNotFoundException e) {
                        scenarioProjectContext.setContextValue(ProjectContext.UPDATE_ERROR, e);
                    }

                });
        Then("^la suppresion du projet est effective$", () -> {
            assertNotNull(scenarioProjectContext.getContextValue(ProjectContext.PROJECT_DELETED));
            assertEquals(Optional.empty(), projectDatabase.byId((Long) scenarioProjectContext.getContextValue(ProjectContext.PROJECT_DELETED)));
        });
        Then("^une erreur de suppression de projet est remontée car le projet est débuté$", () -> {
            assertTrue(scenarioProjectContext.getContextValue(ProjectContext.DELETE_ERROR).getClass().equals(ProjectAlreadyStartedException.class));
        });
        When("^je tente de supprimer le projet \"([^\"]*)\"$", (String projectId) -> {
            Optional<Project> project = projectDatabase.byId(Long.valueOf(projectId));
            try {
                ProjectIdentifier pi = projectFacade.remove(project.get());
                scenarioProjectContext.setContextValue(ProjectContext.PROJECT_DELETED, pi.getId());
            } catch (ProjectAlreadyStartedException | ProjectNotFoundException e) {
                scenarioProjectContext.setContextValue(ProjectContext.DELETE_ERROR, e);
            }
        });
        When("^je tente de modifier le titre du projet \"([^\"]*)\" avec \"([^\"]*)\"$", (String projectId, String newTitle) -> {
            Project project = retrieveProject(projectId, projectDatabase);
            try {
                project.setProjectTitle(newTitle);
                projectFacade.update(project);
                ProjectAttempt projectAttempt = new ProjectAttempt(project.getId(), project.getProjectTitle(),
                        project.getBankrolInit());
                scenarioProjectContext.setContextValue(ProjectContext.UPDATE_PROJECT, projectAttempt);
            } catch (ProjectNotFoundException | ProjectAlreadyStartedException e) {
                scenarioProjectContext.setContextValue(ProjectContext.UPDATE_ERROR, e);
            }
        });
        When("^je tente de modifier la date de fin du projet \"([^\"]*)\" en spécifiant le \"([^\"]*)\"$",
                (String projectId, String newEndDate) -> {
                    Project project = retrieveProject(projectId, projectDatabase);
                    try {
                        project.setEndProject(LocalDate.parse(newEndDate, dtf));
                        projectFacade.update(project);
                        ProjectAttempt projectAttempt = new ProjectAttempt(Long.valueOf(projectId),project.getProjectTitle(), project.getBankrolInit());
                        projectAttempt.setEndProject(LocalDate.parse(newEndDate, dtf));
                        scenarioProjectContext.setContextValue(ProjectContext.UPDATE_PROJECT, projectAttempt);
                    } catch (ProjectAlreadyStartedException | ProjectNotFoundException e) {
                        scenarioProjectContext.setContextValue(ProjectContext.UPDATE_ERROR, e);
                    }
                });

        Then("^une erreur est remontée car la date de fin du projet ne peut être passée$", () -> {
            Assert.assertTrue(scenarioProjectContext.getContextValue(ProjectContext.CREATE_ERROR).getClass().equals(EndDateProjectException.class));
        });
        And("^je vérifie que l'objectif du projet \"([^\"]*)\" est \"([^\"]*)\"$", (String projectId, String newObjectif) -> {
            assertTrue(((ProjectAttempt)scenarioProjectContext.getContextValue(ProjectContext.UPDATE_PROJECT)).getObjectif()
                    .equals(newObjectif));
        });
        And("^j'envisage de créer un nouveau projet \"([^\"]*)\" avec une bankrol de \"([^\"]*)\" euros$",
                (String projectTitle, String bankrolInit) -> {
                    ProjectAttempt projectAttempt =
                            new ProjectAttempt(projectTitle,
                                    new Amount(new BigDecimal(bankrolInit), Devise.EURO));
                    scenarioProjectContext.setContextValue(ProjectContext.NEW_PROJECT, projectAttempt);
        });
        When("^je tente de créer ce projet \"([^\"]*)\"$", (String projectTitle) -> {
            ProjectAttempt attempt = (ProjectAttempt) scenarioProjectContext.getContextValue(ProjectContext.NEW_PROJECT);
            CreateProjectCommand command = new CreateProjectCommand(attempt.getProjectTitle(), attempt.getBkAmount(),
                    LocalDate.now().plusMonths(12), "Objectif", authenticationCustomerGateway.currentCustomer().get());
            ProjectIdentifier pi = projectFacade.create(command);
            attempt.setId(pi.getId());
            scenarioProjectContext.setContextValue(ProjectContext.NEW_PROJECT, attempt);
        });
        And("^ce futur projet aura une durée de (\\d+) jours$", (Integer nbDaysDuration) -> {
            ProjectAttempt projectAttempt = (ProjectAttempt) scenarioProjectContext.getContextValue(ProjectContext.NEW_PROJECT);
            projectAttempt.setEndProject(LocalDate.now().plusDays(nbDaysDuration));
            scenarioProjectContext.setContextValue(ProjectContext.NEW_PROJECT, projectAttempt);
        });
        When("^je récupère tous les projets de \"([^\"]*)\"$", (String tipsterName) -> {
            Optional<Customer> tipster = customercustomerDatabase.getCustomerByPseudo(tipsterName);
            if(!tipster.isPresent()){
                Assert.fail("Aucun client sous le nom " + tipsterName);
            }
            try {
                scenarioProjectContext.setContextValue(
                        ProjectContext.LIST_PROJECT,
                        projectFacade.getAllProjectsOf(tipster.get()));
            } catch (AuthorizationException authorizationException) {
                scenarioAuthorizationContext.setContextValue(
                        AuthorizationContext.NO_ACCOUNT_TO_TIPSTER, authorizationException.getMessage());
            }
        });
        Then("^je vérifie que j'obtiens (\\d+) projets$", (Integer nbProjects) -> {
            Set<Project> projects = (Set<Project>) scenarioProjectContext.getContextValue(ProjectContext.LIST_PROJECT);
            Assert.assertNotNull(projects);
            Assert.assertTrue(projects.size() == nbProjects);
        });
        Then("^je vérifie qu'une erreur d'autorisation pas abonné au tipster est remontée$", () -> {
            Assert.assertNotNull(scenarioAuthorizationContext.getContextValue(AuthorizationContext.NO_ACCOUNT_TO_TIPSTER));
        });
        Then("^je vérifie qu'une erreur d'autorisation pas propriétaire du projet est remontée$", () -> {
            Assert.assertNotNull(scenarioAuthorizationContext.getContextValue(AuthorizationContext.NOT_PROJECT_OWNER));
        });

    }

    private Project retrieveProject(String projectId, ProjectDatabase database) {
        Optional<Project> project = database.byId(Long.valueOf(projectId));
        if(!project.isPresent()){
            throw new IllegalStateException("Le projet devrait être présent!!");
        }
        return project.get();
    }
}
