package com.sfl.flybet.casestudy.domain.adapters;

import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.*;
import com.sfl.flybet.casestudy.domain.ports.project.ProjectCustomerPort;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectCustomer implements ProjectCustomerPort {
    private ProjectRepository projectRepository;
    private CustomerAccountRepository accountRepository;

    public ProjectCustomer(ProjectRepository projectRepository, CustomerAccountRepository accountRepository) {
        this.projectRepository = projectRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void createProject(Project project) throws ProjectAlreadyExistsException, SoldeInsuffisantException, EndDateProjectException {
        CustomerAccount customerAccount = getCustomerAccount(project.getOwner());

        if(customerAccount.hasBalanceLessThan(getRateForOneProject())){
            throw new SoldeInsuffisantException();
        }
        if(project.getEndProject() != null && project.getEndProject().isBefore(LocalDate.now())){
            throw new EndDateProjectException("End date of project must be in the futur.");
        }
        addProjectToRepository(project);
        customerAccount.payForProjectCreation(getRateForOneProject());
        accountRepository.addSystemAmount(getRateForOneProject());
    }

    private void addProjectToRepository(Project project) throws ProjectAlreadyExistsException {
        projectRepository.add(project);
    }

    private Amount getRateForOneProject() {
        return new Amount(new BigDecimal("1.00"), Devise.CREDIT);
    }

    private CustomerAccount getCustomerAccount(Customer customer) {
        return accountRepository.getAccountOf(customer);
    }

    @Override
    public void changeObjectifFor(Project project, String newObjectif) throws ProjectNotFoundException, ProjectAlreadyStartedException {
        projectRepository.updateProjectObjectif(project, newObjectif);
    }

    @Override
    public void changeBankrol(Project project, Amount newBankrol) throws ProjectAlreadyStartedException, ProjectNotFoundException {
        projectRepository.updateProjectBankrol(project, newBankrol);
    }
    @Override
    public void changeEndDate(Project project, LocalDate newEndDate) throws ProjectAlreadyStartedException, ProjectNotFoundException {
        projectRepository.updateProjectEndDate(project, newEndDate);
    }
    @Override
    public void changeTitle(Project project, String newTitle) throws ProjectNotFoundException, ProjectAlreadyStartedException {
        projectRepository.updateProjectTitle(project, newTitle);
    }

    @Override
    public void deleteProject(Project project) throws ProjectAlreadyStartedException, ProjectNotFoundException {
        projectRepository.removeProject(project);
    }


}
