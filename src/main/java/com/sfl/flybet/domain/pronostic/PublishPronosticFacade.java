package com.sfl.flybet.domain.pronostic;

import com.sfl.flybet.casestudy.domain.exceptions.AlreadyPublishedPronosticException;
import com.sfl.flybet.casestudy.domain.exceptions.BankrolInsufficientException;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
import com.sfl.flybet.domain.pronostic.ports.incoming.PublishPronostic;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class PublishPronosticFacade implements PublishPronostic {
    private final ProjectDatabase projectDatabase;

    public PublishPronosticFacade(ProjectDatabase projectDatabase) {
        this.projectDatabase = projectDatabase;
    }

    @Override
    public void publish(Pronostic pronostic, Long projectId) throws AlreadyPublishedPronosticException, BankrolInsufficientException, ProjectNotFoundException {
        validatePublicationArgs(pronostic, projectId);
        Set<Pronostic> pronosExistant =
                projectDatabase.allPronos(projectId);
        if(checkIfPronoAlreadyPublishedInThisProject(pronosExistant)) {
            throw new AlreadyPublishedPronosticException();
        }

        if(checkIfBetGreaterThanRemainingBankrol(pronostic, projectId)) {
            throw new BankrolInsufficientException();
        }

        for(Pronostic p : pronosExistant){
            if(p.equals(pronostic)){
                p.setStatusProno(StatusProno.PUBLISHED);
                projectDatabase.publishDraftPronostic(projectId, p);
                return;
            }
        }

        pronostic.setStatusProno(StatusProno.PUBLISHED);
        projectDatabase.addPronoToProject(projectId, pronostic);
    }

    private void validatePublicationArgs(Pronostic pronostic, Long projectId) {
        if(null == projectId){
            throw new IllegalArgumentException();
        }
        if(!PublishPronosticFacade.PronosticValidator.isPublishable(pronostic)){
            throw new IllegalArgumentException();
        }
    }

    private boolean checkIfPronoAlreadyPublishedInThisProject(Set<Pronostic> pronosExistant) {
        for(Pronostic p : pronosExistant){
            if(p.isPublished()){
                return true;
            }
        }
        return false;
    }

    private boolean checkIfBetGreaterThanRemainingBankrol(Pronostic pronostic, Long projectId) {
        BigDecimal remainingBankrol = BigDecimal.ZERO;
        Optional<Project> optProj = projectDatabase.byId(projectId);
        if(optProj.isPresent()){
            remainingBankrol = remainingBankrol.add(optProj.get().getBankrolInit().getValue());
        }
        else{
            throw new IllegalStateException("Aucun projet n'existe sous l'id "+ projectId);
        }
        try{
        Set<Pronostic> pronosExistant = projectDatabase.allPronos(projectId);
        for(Pronostic p : pronosExistant){
            if(p.isCounted()) {
                if (p.getStatusProno() == StatusProno.WON || p.getStatusProno() == StatusProno.CERTIFIED_WON) {
                    remainingBankrol = remainingBankrol.add((p.getCote().subtract(BigDecimal.ONE)).multiply(p.getMise()));
                } else {
                    remainingBankrol = remainingBankrol.subtract(p.getMise());
                }
            }
        }
        }catch(ProjectNotFoundException pnfe){

        }


        return pronostic.getMise().compareTo(remainingBankrol) > 0;

    }


    private static class PronosticValidator {
        private static final BigDecimal COTE_MINI = new BigDecimal("1.10");

        public static boolean isPublishable(Pronostic pronostic) {
            return pronostic != null && pronostic.getCote().compareTo(COTE_MINI)>=0;
        }
    }
}
