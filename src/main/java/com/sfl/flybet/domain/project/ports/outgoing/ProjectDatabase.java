package com.sfl.flybet.domain.project.ports.outgoing;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.model.ProjectIdentifier;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

import java.util.Optional;
import java.util.Set;

public interface ProjectDatabase {
    ProjectIdentifier save(Project project);
    void updateProject(Project project) throws ProjectNotFoundException;
    void removeProject(Project project);

    void addPronoToProject(Long projectId, Pronostic prono);
    Set<Pronostic> allPronos(Long projectId) throws ProjectNotFoundException;
    void removePronoFromProject(Long projectId, Pronostic pronostic);
    void add(Project project) throws ProjectAlreadyExistsException;
    Set<Project> allProjects(Customer owner);
    Optional<Project> byId(Long projectId);
    Optional<Pronostic> findPronosticById(Long pronoId);

    void updatePronostic(Pronostic pronostic);

    void publishDraftPronostic(Long projectId, Pronostic pronostic);
}
