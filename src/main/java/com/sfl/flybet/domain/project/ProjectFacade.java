package com.sfl.flybet.domain.project;


import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.project.exceptions.*;
import com.sfl.flybet.domain.project.model.CreateProjectCommand;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.model.ProjectIdentifier;
import com.sfl.flybet.domain.project.ports.incoming.CreateProject;
import com.sfl.flybet.domain.project.ports.incoming.ReadProject;
import com.sfl.flybet.domain.project.ports.incoming.RemoveProject;
import com.sfl.flybet.domain.project.ports.incoming.UpdateProject;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
import com.sfl.flybet.domain.subscription.ports.outgoing.SubscriptionDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.logging.Logger;

public class ProjectFacade implements CreateProject, UpdateProject, RemoveProject, ReadProject {

    private final static Logger LOGGER = Logger.getLogger( ProjectFacade.class.getName() );

    private final ProjectDatabase database;
    private final CustomerAccountDatabase accountDatabase;
    private final SubscriptionDatabase subscriptionDatabase;
    private final AuthenticationCustomerGateway authenticationCustomerGateway;

    public ProjectFacade(ProjectDatabase database, AuthenticationCustomerGateway authenticationCustomerGateway,
                         CustomerAccountDatabase accountDB, SubscriptionDatabase subscriptionDatabase) {
        this.database = database;
        this.authenticationCustomerGateway = authenticationCustomerGateway;
        this.accountDatabase = accountDB;
        this.subscriptionDatabase = subscriptionDatabase;
    }

    @Override
    public ProjectIdentifier create(CreateProjectCommand command)
            throws ProjectAlreadyExistsException, SoldeInsuffisantException, EndDateProjectException, AuthorizationException {
        if(!authenticationCustomerGateway.currentCustomer().isPresent()){
            throw new IllegalStateException("No current user!");
        }
        if(authenticationCustomerGateway.currentCustomer().get() != command.getOwner()){
            throw new AuthorizationException("Project can only be created by current user");
        }
        CustomerAccount customerAccount = getCustomerAccount(command.getOwner());

        if(customerAccount.hasBalanceLessThan(getRateForOneProject())){
            throw new SoldeInsuffisantException();
        }
        if(command.getEndProject() != null && command.getEndProject().isBefore(LocalDate.now())){
            throw new EndDateProjectException("End date of project must be in the futur.");
        }
        if(database.allProjects(command.getOwner()).stream().anyMatch(project -> project.getProjectTitle().equals(command.getProjectTitle())))
            throw new ProjectAlreadyExistsException("2 projets ne peuvent avoir le même titre.");

        Project project = new Project();
        project.setProjectTitle(command.getProjectTitle());
        project.setBankrolInit(command.getBankrolInit());
        project.setEndProject(command.getEndProject());
        project.setObjectif(command.getObjectif());
        project.setOwner(command.getOwner());

        ProjectIdentifier pi = database.save(project);
        customerAccount.payForProjectCreation(getRateForOneProject());
        accountDatabase.addSystemAmount(getRateForOneProject());

        LOGGER.info("Nouveau projet créé!");

        return pi;
    }

    @Override
    public ProjectIdentifier update(Project project) throws ProjectAlreadyStartedException, ProjectNotFoundException, AuthorizationException {
        if(!authenticationCustomerGateway.currentCustomer().isPresent()){
            throw new IllegalStateException("No current user!");
        }
        if(project.getOwner() != authenticationCustomerGateway.currentCustomer().get())
            throw new AuthorizationException("Must be owner to change a project");
        throwExceptionForProjectAlreadyStarted(project.getId());
        database.updateProject(project);
        return new ProjectIdentifier(project.getId());
    }

    private void throwExceptionForProjectAlreadyStarted(Long projectId) throws ProjectNotFoundException, ProjectAlreadyStartedException {
        Set<Pronostic> pronostics = null;
        if((pronostics = database.allPronos(projectId)) != null){
            for (Pronostic prono : pronostics) {
                if(prono.getStatusProno().equals(StatusProno.PUBLISHED)){
                    throw new ProjectAlreadyStartedException("Au moins un prono a déjà été publié");
                }
            }
        }
    }

    private CustomerAccount getCustomerAccount(Customer customer) {
        return accountDatabase.getAccountOf(customer).get();
    }

    private Amount getRateForOneProject() {
        return new Amount(new BigDecimal("1.00"), Devise.CREDIT);
    }

    @Override
    public ProjectIdentifier remove(Project project) throws ProjectAlreadyStartedException, ProjectNotFoundException, AuthorizationException {
        if(!authenticationCustomerGateway.currentCustomer().isPresent()){
            throw new IllegalStateException("No current user!");
        }
        if(project.getOwner() != authenticationCustomerGateway.currentCustomer().get())
            throw new AuthorizationException("Must be owner to change a project");

        throwExceptionForProjectAlreadyStarted(project.getId());

        database.removeProject(project);
        return new ProjectIdentifier(project.getId());
    }

    @Override
    public Set<Project> getAllProjectsOf(Customer owner) throws AuthorizationException {
        if(!authenticationCustomerGateway.currentCustomer().isPresent()){
            throw new IllegalStateException("No current user!");
        }
        if(!subscriptionDatabase.isSubscribed(authenticationCustomerGateway.currentCustomer().get(), owner))
            throw new AuthorizationException("Must have subscription to get projects of "+ owner.getPseudo());

        return database.allProjects(owner);
    }
}
