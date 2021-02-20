package unit.projects;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.model.ProjectIdentifier;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class InMemoryProjectDatabaseAdapter implements ProjectDatabase {
    ConcurrentHashMap<Long, Project> projects = new ConcurrentHashMap<>();
    ConcurrentHashMap<Long, Set<Pronostic>> projectMap = new ConcurrentHashMap<>();

    @Override
    public ProjectIdentifier save(Project project) {
        Long newId;
        if(projects.isEmpty()){
            newId = 0L;
        }
        else{
            newId = projects.keySet().stream().mapToLong(k -> k).max().getAsLong()+1;
        }
        project.setId(newId);
        projects.put(newId, project);
        projectMap.put(newId, new HashSet<>());
        return new ProjectIdentifier(newId);
    }

    @Override
    public void updateProject(Project project) throws ProjectNotFoundException {
        if(projects.get(project.getId()) == null){
            throw new ProjectNotFoundException();
        }
        projects.put(project.getId(), project);
    }

    @Override
    public void removeProject(Project project) {
        projects.remove(project.getId());
    }

    @Override
    public void addPronoToProject(Long projectId, Pronostic prono) {
        projectMap.get(projectId).add(prono);
    }

    @Override
    public Set<Pronostic> allPronos(Long projectId) throws ProjectNotFoundException {
        if(null == projectMap.get(projectId)){
            throw new ProjectNotFoundException("Aucun projet trouvé");
        }
        return projectMap.get(projectId);
    }

    @Override
    public void removePronoFromProject(Long projectId, Pronostic pronostic) {
        projectMap.get(projectId).remove(pronostic.getId());
    }

    @Override
    public void add(Project project) throws ProjectAlreadyExistsException {
        projects.put(project.getId(), project);
        if(null == projectMap.get(project.getId()))
           projectMap.put(project.getId(), new HashSet<>());
    }


    @Override
    public Set<Project> allProjects(Customer owner) {
        Set<Project> res = new HashSet<>();
        projects.entrySet().stream().filter(projet -> projet.getValue().getOwner().getId().equals(owner.getId())).map(Entry::getValue).forEach(
                res::add
        );
        return res;
    }

    @Override
    public Optional<Project> byId(Long projectId) {
        return Optional.ofNullable(projects.get(projectId));
    }

    @Override
    public Optional<Pronostic> findPronosticById(Long pronoId) {
        for(Map.Entry<Long, Set<Pronostic>> entry : projectMap.entrySet()){
            for(Pronostic p : entry.getValue()){
                if(p.getId().equals(pronoId)){
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }


    @Override
    public void updatePronostic(Pronostic pronostic) {

    }

    @Override
    public void publishDraftPronostic(Long projectId, Pronostic pronostic) {

    }
}
