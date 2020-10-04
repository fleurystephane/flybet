package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyStartedException;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectNotFoundException;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProjectRepository implements ProjectRepository {
    private Map<Project, Set<Pronostic>> projectMap = new HashMap<>();
    public void addPronoToProject(String projectId, Pronostic prono) {
        all(projectId).add(prono);
    }

    @Override
    public Set<Pronostic> all(String projectId) {
        if(projectExists(projectId)){
            return projectMap.get(
                    byId(projectId).get());
        }
        return null;
    }

    private boolean projectExists(String projectId) {
        return byId(projectId).isPresent();
    }

    @Override
    public void removePronoFromProject(String projectId, Pronostic pronostic) {
        Optional<Project> p = byId(projectId);
        p.ifPresent(project -> projectMap.get(project).remove(pronostic));
    }

    @Override
    public void add(Project project) throws ProjectAlreadyExistsException {
        if(projectMap.containsKey(project)){
            throw new ProjectAlreadyExistsException();
        }
        projectMap.put(project, new LinkedHashSet<>());
    }

    @Override
    public Set<Project> all(Customer owner) {
        return projectMap.keySet().stream().filter(project -> project.getOwner().equals(owner)).collect(Collectors.toSet());
    }

    @Override
    public Optional<Project> byId(String projectId) {
        return projectMap.keySet().stream().filter(project -> project.getId().equals(projectId)).findFirst();
    }

    @Override
    public void updateProjectTitle(Project project, String newTitle) throws ProjectNotFoundException, ProjectAlreadyStartedException {
        if(weCanChangeProject(project)){
            project.setProjectTitle(newTitle);
        }
    }

    @Override
    public void updateProjectObjectif(Project project, String newObjectif) throws ProjectNotFoundException, ProjectAlreadyStartedException {
        if (weCanChangeProject(project)) {
            project.setObjectif(newObjectif);
        }
    }

    @Override
    public void updateProjectBankrol(Project project, Amount newBankrol) throws ProjectNotFoundException, ProjectAlreadyStartedException {
        if (weCanChangeProject(project)) {
            project.setBankrol(newBankrol);
        }
    }

    @Override
    public void updateProjectEndDate(Project project, LocalDate newEndDate) throws ProjectNotFoundException, ProjectAlreadyStartedException {
        if(weCanChangeProject(project)){
            project.setEndProject(newEndDate);
        }
    }

    @Override
    public void removeProject(Project project) throws ProjectNotFoundException, ProjectAlreadyStartedException {
        if(weCanChangeProject(project)) {
            projectMap.remove(project);
        }
    }


    @Override
    public Optional<Pronostic> findPronosticById(String pronoId) {
        Set<Project> projects = projectMap.keySet();
        for (Project project : projects){
            if(projectMap.get(project).stream().anyMatch(pronostic -> pronostic.getId().equals(pronoId))){
                return projectMap.get(project).stream().filter(pronostic -> pronostic.getId().equals(pronoId)).findFirst();
            }
        }
        return Optional.empty();
    }

    @Override
    public void updatePronostic(Pronostic pronostic) {
        Set<Project> projects = projectMap.keySet();
        for (Project project : projects) {
            Optional<Pronostic> p = projectMap.get(project).stream().filter(prono -> prono.getId().equals(pronostic.getId())).findFirst();
            if(p.isPresent()) {
                Set<Pronostic> pronostics = projectMap.get(project);
                pronostics.remove(p);
                pronostics.add(pronostic);
            }

        }
    }

    @Override
    public void publishDraftPronostic(String projectId, Pronostic pronostic) {
        this.removePronoFromProject(projectId, pronostic);
        pronostic.setStatus(StatusProno.PUBLISHED);
        this.addPronoToProject(projectId, pronostic);
    }

    private boolean weCanChangeProject(Project project) throws ProjectAlreadyStartedException, ProjectNotFoundException {
        Optional<Project> projectFromRepo = byId(project.getId());
        if(projectFromRepo.isPresent()) {
            if(projectMap.get(projectFromRepo.get()).size() > 0) {
                throw new ProjectAlreadyStartedException();
            }
            return true;
        }
        else{
            throw new ProjectNotFoundException();
        }
    }

}
