package acceptance.projects;

import acceptance.projects.facilities.ProjectAttempt;
import com.github.npathai.hamcrestopt.OptionalMatchers;
import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Devise;
import com.sfl.flybet.casestudy.domain.Project;
import com.sfl.flybet.casestudy.domain.exceptions.*;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.domain.ports.project.ProjectCustomerPort;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import com.sfl.flybet.casestudy.domain.adapters.ProjectCustomer;
import configuration.projects.ProjectContext;
import configuration.projects.ScenarioProjectContext;
import io.cucumber.java8.En;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ProjectSteps implements En {

    public ProjectSteps(AuthenticationCustomerGateway authenticationCustomerGateway, ProjectRepository projectRepository,
                        CustomerAccountRepository customerAccountRepository, ScenarioProjectContext scenarioProjectContext) {
        final ProjectCustomerPort projectCustomerPort = new ProjectCustomer(projectRepository, customerAccountRepository);
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        When("^je tente de créer un projet \"([^\"]*)\" avec une bankrol de \"([^\"]*)\" euros$", (String projectTitle, String bankrol) -> {
            Amount bkAmount = new Amount(new BigDecimal(bankrol), Devise.CREDIT);
            try {

                Project project = new Project(authenticationCustomerGateway.currentCustomer().get(), projectTitle, bkAmount);
                project.setId("456");
                projectCustomerPort.createProject(project);
                ProjectAttempt projectAttempt = new ProjectAttempt(project.getId(), projectTitle, bkAmount);
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
                        Project project = new Project(authenticationCustomerGateway.currentCustomer().get(),
                                projectTitle, bkAmount, LocalDate.parse(dateFin, dtf));
                        project.setId("123");
                        projectCustomerPort.createProject(project);
                        ProjectAttempt projectAttempt = new ProjectAttempt(project.getId(),projectTitle, bkAmount);
                        scenarioProjectContext.setContextValue(ProjectContext.NEW_PROJECT, projectAttempt);
                    } catch (ProjectAlreadyExistsException | SoldeInsuffisantException | EndDateProjectException e) {
                        scenarioProjectContext.setContextValue(ProjectContext.CREATE_ERROR, e);
                    }
        });
        Then("^la création du projet est effective$", () -> {
            ProjectAttempt creationProjectAttempt = (ProjectAttempt) scenarioProjectContext.getContextValue(ProjectContext.NEW_PROJECT);

            assertThat(projectRepository.all(creationProjectAttempt.getId()), Matchers.notNullValue());
        });
        And("^le nombre total de projet de \"([^\"]*)\" est de (\\d+)$", (String pseudo, Integer nbProjects) -> {
            assertThat(projectRepository.all(authenticationCustomerGateway.currentCustomer().get()), Matchers.hasSize(nbProjects));
        });

        Then("^une erreur est remontée car un projet existe déjà pour ce titre$", () -> {
            assertThat(scenarioProjectContext.getContextValue(ProjectContext.CREATE_ERROR), Matchers.instanceOf(ProjectAlreadyExistsException.class));
        });
        And("^le projet \"([^\"]*)\" contient (\\d+) pronostic$", (String projectId, Integer nbPronos) -> {
            assertThat(projectRepository.all(projectId), Matchers.hasSize(nbPronos));
        });

        Then("^la modification du projet est effective$", () -> {
            ProjectAttempt creationProjectAttempt = (ProjectAttempt) scenarioProjectContext.getContextValue(ProjectContext.UPDATE_PROJECT);
            assertThat(creationProjectAttempt.getObjectif(), Matchers.equalToIgnoringCase(
                    projectRepository.byId(creationProjectAttempt.getId()).get().getObjectif()
            ));
        });
        When("^je tente de modifier l'objectif du projet \"([^\"]*)\" comme étant \"([^\"]*)\"$", (String projectId, String objectif) -> {
            Optional<Project> project = projectRepository.byId(projectId);
            try {
                projectCustomerPort.changeObjectifFor(project.get(), objectif);
                ProjectAttempt projectAttempt = new ProjectAttempt(project.get().getId(),projectId, project.get().getBankrol());
                projectAttempt.setObjectif(objectif);
                projectAttempt.setEndProject(project.get().getEndProject());
                scenarioProjectContext.setContextValue(ProjectContext.UPDATE_PROJECT, projectAttempt);
            } catch (ProjectNotFoundException | ProjectAlreadyStartedException e) {
                scenarioProjectContext.setContextValue(ProjectContext.UPDATE_ERROR, e);
            }
        });
        Then("^une erreur est remontée car le projet est déjà débuté$", () -> {
            assertThat(scenarioProjectContext.getContextValue(ProjectContext.UPDATE_ERROR), Matchers.instanceOf(ProjectAlreadyStartedException.class));
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
                    Optional<Project> project = projectRepository.byId(projectId);
                    try {
                        projectCustomerPort.changeBankrol(project.get(), new Amount(new BigDecimal(newBankrol), Devise.EURO));
                        ProjectAttempt projectAttempt = new ProjectAttempt(project.get().getId(),project.get().getProjectTitle(), new Amount(new BigDecimal(newBankrol), Devise.EURO));
                        scenarioProjectContext.setContextValue(ProjectContext.UPDATE_PROJECT, projectAttempt);
                    } catch (ProjectAlreadyStartedException | ProjectNotFoundException e) {
                        scenarioProjectContext.setContextValue(ProjectContext.UPDATE_ERROR, e);
                    }

                });
        Then("^la suppresion du projet est effective$", () -> {
            assertThat(scenarioProjectContext.getContextValue(ProjectContext.PROJECT_DELETED), Matchers.notNullValue());
            assertThat(projectRepository.byId((String) scenarioProjectContext.getContextValue(ProjectContext.PROJECT_DELETED)),
                    OptionalMatchers.isEmpty());
        });
        Then("^une erreur de suppression de projet est remontée car le projet est débuté$", () -> {
            assertThat(scenarioProjectContext.getContextValue(ProjectContext.DELETE_ERROR),
                    Matchers.instanceOf(ProjectAlreadyStartedException.class));
        });
        When("^je tente de supprimer le projet \"([^\"]*)\"$", (String projectId) -> {
            Optional<Project> project = projectRepository.byId(projectId);
            try {
                projectCustomerPort.deleteProject(project.get());
                scenarioProjectContext.setContextValue(ProjectContext.PROJECT_DELETED, project.get().getId());
            } catch (ProjectAlreadyStartedException | ProjectNotFoundException e) {
                scenarioProjectContext.setContextValue(ProjectContext.DELETE_ERROR, e);
            }
        });
        When("^je tente de modifier le titre du projet \"([^\"]*)\" avec \"([^\"]*)\"$", (String projectId, String newTitle) -> {
                Optional<Project> project = projectRepository.byId(projectId);
            try {
                projectCustomerPort.changeTitle(project.get(), newTitle);
                ProjectAttempt projectAttempt = new ProjectAttempt(project.get().getId(), project.get().getProjectTitle(),
                        project.get().getBankrol());
                scenarioProjectContext.setContextValue(ProjectContext.UPDATE_PROJECT, projectAttempt);
            } catch (ProjectNotFoundException | ProjectAlreadyStartedException e) {
                scenarioProjectContext.setContextValue(ProjectContext.UPDATE_ERROR, e);
            }
        });
        When("^je tente de modifier la date de fin du projet \"([^\"]*)\" en spécifiant le \"([^\"]*)\"$",
                (String projectId, String newEndDate) -> {
                    Optional<Project> project = projectRepository.byId(projectId);
                    try {
                        projectCustomerPort.changeEndDate(project.get(), LocalDate.parse(newEndDate, dtf));
                        ProjectAttempt projectAttempt = new ProjectAttempt(projectId,project.get().getProjectTitle(), project.get().getBankrol());
                        projectAttempt.setEndProject(LocalDate.parse(newEndDate, dtf));
                        scenarioProjectContext.setContextValue(ProjectContext.UPDATE_PROJECT, projectAttempt);
                    } catch (ProjectAlreadyStartedException | ProjectNotFoundException e) {
                        scenarioProjectContext.setContextValue(ProjectContext.UPDATE_ERROR, e);
                    }
                });
        Then("^une erreur est remontée car la date de fin du projet ne peut être passée$", () -> {
            Assert.assertThat(scenarioProjectContext.getContextValue(ProjectContext.CREATE_ERROR), Matchers.instanceOf(EndDateProjectException.class));
        });
        And("^je vérifie que l'objectif du projet \"([^\"]*)\" est \"([^\"]*)\"$", (String projectId, String newObjectif) -> {
            assertThat(((ProjectAttempt)scenarioProjectContext.getContextValue(ProjectContext.UPDATE_PROJECT)).getObjectif(),
                    Matchers.equalToIgnoringCase(newObjectif));
        });

    }
}
