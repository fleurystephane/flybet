package com.sfl.flybet.domain.pronostic;

import com.sfl.flybet.casestudy.domain.exceptions.AlreadyDisapprovedException;
import com.sfl.flybet.casestudy.domain.exceptions.DisapprovalableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Disapproval;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.ports.incoming.DisapprovalPronostic;
import com.sfl.flybet.domain.pronostic.ports.outgoing.DisapprovalPronosticDatabase;

import java.util.Iterator;
import java.util.Set;

public class DisapprovalPronosticFacade implements DisapprovalPronostic {
    public static final int NB_CUSTOMER_DISAPPROVALS = 3;
    public static final String NO_DISAPPROVAL_REMAINING = "No disapproval remaining.";
    public static final String PRONOSTIC_IS_NOT_IN_DISAPPROVALABLE_STATUS = "Pronostic is not in disapprovalable status";
    public static final String IMPOSSIBLE_TO_DISAPPROVE_OWN_PRONOSTIC = "Impossible to disapprove own pronostic";

    private final ProjectDatabase projectDatabase;
    private final DisapprovalPronosticDatabase disapprovalPronosticDatabase;

    public DisapprovalPronosticFacade(ProjectDatabase projectDatabase, DisapprovalPronosticDatabase disapprovalPronosticDatabase) {
        this.projectDatabase = projectDatabase;
        this.disapprovalPronosticDatabase = disapprovalPronosticDatabase;
    }

    @Override
    public void disapprovePronostic(Customer customer, Pronostic pronostic)
            throws PronosticNotFoundException, DisapprovalableException, AlreadyDisapprovedException {
        if(!projectDatabase.findPronosticById(pronostic.getId()).isPresent())
            throw new PronosticNotFoundException();
        if(pronosticBelongsToCustomer(customer, pronostic))
            throw new DisapprovalableException(IMPOSSIBLE_TO_DISAPPROVE_OWN_PRONOSTIC);
        if(getDisapprovalRaminingCounter(customer) <= 0){
            throw new DisapprovalableException(NO_DISAPPROVAL_REMAINING);
        }
        if(pronostic.isDisapprovalable()) {
            Set<Disapproval> customerDisapps = disapprovalPronosticDatabase.byCustomer(customer.getId());
            if(customerDisapps.stream().filter(disapproval -> disapproval.getPronostic().getId().equals(pronostic.getId())).count() == 1)
                throw new AlreadyDisapprovedException();

            Disapproval disapproval = new Disapproval(pronostic, customer);
            disapprovalPronosticDatabase.add(disapproval);
        }
        else{
            throw new DisapprovalableException(PRONOSTIC_IS_NOT_IN_DISAPPROVALABLE_STATUS);
        }
    }

    @Override
    public int getDisapprovalRaminingCounter(Customer customer) {
        if(customer.getNbClaims() == -1)
            return NB_CUSTOMER_DISAPPROVALS - disapprovalPronosticDatabase.countDisapprovalFor(customer);

        return customer.getNbClaims() - disapprovalPronosticDatabase.countDisapprovalFor(customer);
    }


    private boolean pronosticBelongsToCustomer(Customer customer, Pronostic pronostic) {
        Iterator<Project> projects = projectDatabase.allProjects(customer).iterator();
        while(projects.hasNext()){
            try {
                if(projectDatabase.allPronos(projects.next().getId()).stream().anyMatch(pronostic1 -> pronostic1.equals(pronostic))){
                    return true;
                }
            } catch (ProjectNotFoundException ignored) { }
        }
        return false;
    }
}
