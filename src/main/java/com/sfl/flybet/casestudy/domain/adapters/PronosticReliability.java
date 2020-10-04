package com.sfl.flybet.casestudy.domain.adapters;

import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.*;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.domain.ports.reliability.PronosticReliabilityPort;
import com.sfl.flybet.casestudy.domain.repositories.PenaltyRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.DisapprovalRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.NotificationRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;

import java.util.Iterator;
import java.util.Optional;

/**
 * La fiabilité des pronostics...
 */
public class PronosticReliability implements PronosticReliabilityPort {


    private AuthenticationCustomerGateway authenticationCustomerGateway;
    private ProjectRepository projectRepository;
    private DisapprovalRepository disapprovalRepository;

    private NotificationRepository notificationRepository;
    private PenaltyRepository penaltyRepository;


    public PronosticReliability(AuthenticationCustomerGateway authenticationCustomerGateway,
                                ProjectRepository projectRepository, DisapprovalRepository disapprovalRepository,
                                NotificationRepository notificationRepository, PenaltyRepository penaltyRepository) {
        this.authenticationCustomerGateway = authenticationCustomerGateway;

        this.projectRepository = projectRepository;
        this.disapprovalRepository = disapprovalRepository;
        this.notificationRepository = notificationRepository;
        this.penaltyRepository = penaltyRepository;
    }

    @Override
    public void disapprovePronostic(Customer customer, Pronostic pronostic) throws PronosticNotFoundException, DisapprovalableException, AlreadyDisapprovedException {
        if(!projectRepository.findPronosticById(pronostic.getId()).isPresent())
            throw new PronosticNotFoundException();
        if(pronosticBelongsToCustomer(customer, pronostic))
            throw new DisapprovalableException(IMPOSSIBLE_TO_DISAPPROVE_OWN_PRONOSTIC);
        if(getDisapprovalRaminingCounter(customer) <= 0){
            throw new DisapprovalableException(NO_DISAPPROVAL_REMAINING);
        }
        if(pronostic.isDisapprovalable()) {
            if(disapprovalRepository.byCustomer(customer.getId()).stream().anyMatch(disapproval ->
                    disapproval.getPronostic().getId().equals(pronostic.getId()))){
                throw new AlreadyDisapprovedException();
            }
            Disapproval disapproval = new Disapproval(pronostic, customer);
            disapprovalRepository.add(disapproval);
        }
        else{
            throw new DisapprovalableException(PRONOSTIC_IS_NOT_IN_DISAPPROVALABLE_STATUS);
        }
    }


    @Override
    public int getDisapprovalRaminingCounter(Customer customer) {
        return Math.max(0, NB_CUSTOMER_DISAPPROVALS - disapprovalRepository.countDisapprovalFor(customer));
    }

    @Override
    public void declarePronosticWon(String pronoId) throws PronosticNotFoundException, PronosticNotDecidableException {
        Optional<Pronostic> prono = projectRepository.findPronosticById(pronoId);
        if(!prono.isPresent())
            throw new PronosticNotFoundException();
        if(prono.get().isPublished()){
            prono.get().setStatus(StatusProno.WON);
            notificationRepository.add(new PronosticEvent());
        }
        else{
            throw new PronosticNotDecidableException();
        }
    }

    @Override
    public void changePronosticToLostAsAdmin(Customer customer, Pronostic pronostic) throws AuthorisationException, PronosticNotFoundException {
        if(!checkCurrentCustomerIsAdmin())
            throw new AuthorisationException("You must be Admin for changing pronostic status");
        if(!pronosticBelongsToCustomer(customer, pronostic))
            throw new PronosticNotFoundException();

        pronostic.setStatus(StatusProno.CERTIFIED_LOST);
        projectRepository.updatePronostic(pronostic);
        disapprovalRepository.removeAllFor(pronostic);
        penaliseTipster(customer, pronostic);

    }

    private boolean pronosticBelongsToCustomer(Customer customer, Pronostic pronostic) {
        Iterator<Project> projects = projectRepository.all(customer).iterator();
        while(projects.hasNext()){
            if(projectRepository.all(projects.next().getId()).stream().anyMatch(pronostic1 -> pronostic1.equals(pronostic))){
                return true;
            }
        }
        return false;
    }

    private void penaliseTipster(Customer tipster, Pronostic pronostic) {
        penaltyRepository.addPenalty(tipster, pronostic);
        notificationRepository.add(new PronosticEvent());
    }


    private boolean checkCurrentCustomerIsAdmin() {
        return authenticationCustomerGateway.isAdmin(authenticationCustomerGateway.currentCustomer().get());
    }

}
