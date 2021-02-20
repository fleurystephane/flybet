package com.sfl.flybet.domain.pronostic;

import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotDecidableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.PronosticEvent;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
import com.sfl.flybet.domain.pronostic.ports.incoming.ManageResultPronostic;
import com.sfl.flybet.domain.pronostic.ports.outgoing.DisapprovalPronosticDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PenaltyCustomerDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PronosticNotificationDatabase;

import java.util.Iterator;
import java.util.Optional;

public class ManageResultPronosticFacade implements ManageResultPronostic {
    private final ProjectDatabase projectDatabase;
    private final PenaltyCustomerDatabase penaltyCustomerDatabase;
    private final PronosticNotificationDatabase pronosticNotificationDatabase;
    private final AuthenticationCustomerGateway authenticationCustomerGateway;
    private final DisapprovalPronosticDatabase disapprovalPronosticDatabase;

    public ManageResultPronosticFacade(ProjectDatabase projectDatabase,
                                       PenaltyCustomerDatabase penaltyCustomerDatabase,
                                       PronosticNotificationDatabase pronosticNotificationDatabase,
                                       AuthenticationCustomerGateway authenticationCustomerGateway,
                                       DisapprovalPronosticDatabase disapprovalPronosticDatabase) {
        this.projectDatabase = projectDatabase;
        this.penaltyCustomerDatabase = penaltyCustomerDatabase;
        this.pronosticNotificationDatabase = pronosticNotificationDatabase;
        this.authenticationCustomerGateway = authenticationCustomerGateway;
        this.disapprovalPronosticDatabase = disapprovalPronosticDatabase;
    }

    @Override
    public void declarePronosticWon(Long pronoId) throws PronosticNotFoundException, PronosticNotDecidableException {
        Optional<Pronostic> prono = projectDatabase.findPronosticById(pronoId);
        if(!prono.isPresent())
            throw new PronosticNotFoundException();
        if(prono.get().isPublished()){
            prono.get().setStatusProno(StatusProno.WON);
            PronosticEvent pe = new PronosticEvent(prono.get());
            pronosticNotificationDatabase.add(pe);
        }
        else{
            throw new PronosticNotDecidableException();
        }
    }

    @Override
    public void changePronosticToLostAsAdmin(Customer customer, Pronostic pronostic) throws AuthorizationException, PronosticNotFoundException {
        if(!checkCurrentCustomerIsAdmin())
            throw new AuthorizationException("You must be Admin for changing pronostic status");
        if(!pronosticBelongsToCustomer(customer, pronostic))
            throw new PronosticNotFoundException();

        pronostic.setStatusProno(StatusProno.CERTIFIED_LOST);
        projectDatabase.updatePronostic(pronostic);
        disapprovalPronosticDatabase.removeAllFor(pronostic);
        penaliseTipster(customer, pronostic);
    }

    private void penaliseTipster(Customer tipster, Pronostic pronostic) {
        penaltyCustomerDatabase.addPenalty(tipster, pronostic);
        PronosticEvent pe = new PronosticEvent(pronostic);
        pronosticNotificationDatabase.add(pe);
    }

    private boolean checkCurrentCustomerIsAdmin() {
        return authenticationCustomerGateway.isAdmin(authenticationCustomerGateway.currentCustomer().get());
    }

    private boolean pronosticBelongsToCustomer(Customer customer, Pronostic pronostic) {
        Iterator<Project> projects = projectDatabase.allProjects(customer).iterator();
        while(projects.hasNext()){
            try {
                if(projectDatabase.allPronos(projects.next().getId()).stream().anyMatch(pronostic1 -> pronostic1.equals(pronostic))){
                    return true;
                }
            } catch (ProjectNotFoundException e) {}
        }
        return false;
    }
}
