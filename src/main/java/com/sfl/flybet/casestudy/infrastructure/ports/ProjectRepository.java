package com.sfl.flybet.casestudy.infrastructure.ports;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Project;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyStartedException;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectNotFoundException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface ProjectRepository {
    void addPronoToProject(String projectId, Pronostic prono);

    Set<Pronostic> all(String projectId);

    void removePronoFromProject(String projectId, Pronostic pronostic);

    void add(Project project) throws ProjectAlreadyExistsException;

    Set<Project> all(Customer owner);

    Optional<Project> byId(String projectId);

    void updateProjectTitle(Project project, String newTitle) throws ProjectAlreadyStartedException, ProjectNotFoundException;

    void updateProjectObjectif(Project project, String newObjectif) throws ProjectNotFoundException, ProjectAlreadyStartedException;

    void updateProjectBankrol(Project project, Amount newBankrol) throws ProjectNotFoundException, ProjectAlreadyStartedException;

    void removeProject(Project project) throws ProjectNotFoundException, ProjectAlreadyStartedException;

    void updateProjectEndDate(Project project, LocalDate newEndDate) throws ProjectNotFoundException, ProjectAlreadyStartedException;

    Optional<Pronostic> findPronosticById(String pronoId);

    void updatePronostic(Pronostic pronostic);

    void publishDraftPronostic(String projectId, Pronostic pronostic);

}
