package com.sfl.flybet.casestudy.domain.ports.project;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Project;
import com.sfl.flybet.casestudy.domain.exceptions.*;

import java.time.LocalDate;

public interface ProjectCustomerPort {
    void createProject(Project project) throws ProjectAlreadyExistsException, SoldeInsuffisantException, EndDateProjectException;

    void changeObjectifFor(Project project, String newObjectif) throws ProjectNotFoundException, ProjectAlreadyStartedException;

    void changeBankrol(Project project, Amount newBankrol) throws ProjectAlreadyStartedException, ProjectNotFoundException;

    void changeEndDate(Project project, LocalDate newEndDate) throws ProjectAlreadyStartedException, ProjectNotFoundException;

    void deleteProject(Project project) throws ProjectAlreadyStartedException, ProjectNotFoundException;

    void changeTitle(Project project, String newTitle) throws ProjectNotFoundException, ProjectAlreadyStartedException;
}
