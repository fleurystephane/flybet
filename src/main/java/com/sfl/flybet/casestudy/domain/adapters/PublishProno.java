package com.sfl.flybet.casestudy.domain.adapters;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.StatusProno;
import com.sfl.flybet.casestudy.domain.exceptions.AlreadyPublishedPronosticException;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.casestudy.domain.ports.pronostic.PronosticPublicationPort;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class PublishProno implements PronosticPublicationPort {
    private final Customer currentCustomer;
    private final ProjectRepository projectRepository;

    public PublishProno(Customer currentCustomer, ProjectRepository projectRepository) {
        this.currentCustomer = currentCustomer;
        this.projectRepository = projectRepository;
    }

    @Override
    public void publish(Pronostic pronostic, String projectId) throws AlreadyPublishedPronosticException {
        validatePublicationArgs(pronostic, projectId);
        Set<Pronostic> pronosExistant =
                projectRepository.all(projectId);

        if(null != pronosExistant){
            checkIfPronoAlreadyPublishedInThisProject(pronosExistant);

            for(Pronostic p : pronosExistant){
                if(p.equals(pronostic)){
                    projectRepository.publishDraftPronostic(projectId, p);
                    return;
                }
            }
        }
        pronostic.setStatus(StatusProno.PUBLISHED);
        projectRepository.addPronoToProject(projectId, pronostic);

    }

    private void checkIfPronoAlreadyPublishedInThisProject(Set<Pronostic> pronosExistant) throws AlreadyPublishedPronosticException {
        for(Pronostic p : pronosExistant){
            if(p.isPublished()){
                throw new AlreadyPublishedPronosticException();
            }
        }
    }

    private void validatePublicationArgs(Pronostic pronostic, String projectId) {
        if(null == projectId || projectId.isEmpty()){
            throw new IllegalArgumentException();
        }
        if(!PronosticValidator.isPublishable(pronostic)){
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void save(Pronostic pronostic, String projectId) {
        projectRepository.addPronoToProject(projectId, pronostic);
    }

    @Override
    public void changeCoteValue(Pronostic pronostic, BigDecimal newCote) throws PronosticNotFoundException {
        Optional<Pronostic> pronosExistant =
                projectRepository.findPronosticById(pronostic.getId());
        if(pronosExistant.isPresent()){
            if(!pronosExistant.get().isDecided()){
                pronosExistant.get().setCote(newCote);
            }
        }
        else {
            throw new PronosticNotFoundException();
        }
    }

    private static class PronosticValidator {
        private static final BigDecimal COTE_MINI = new BigDecimal("1.10");

        public static boolean isPublishable(Pronostic pronostic) {
            return pronostic != null && pronostic.getCote().compareTo(COTE_MINI)>=0;
        }
    }
}
