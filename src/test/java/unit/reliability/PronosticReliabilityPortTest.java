package unit.reliability;

import com.sfl.flybet.casestudy.domain.exceptions.AlreadyDisapprovedException;
import com.sfl.flybet.casestudy.domain.exceptions.DisapprovalableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotDecidableException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.casestudy.domain.gateways.InMemoryAuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.AuthenticationCustomerGateway;
import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.DisapprovalPronosticFacade;
import com.sfl.flybet.domain.pronostic.ManageResultPronosticFacade;
import com.sfl.flybet.domain.pronostic.PublishPronosticFacade;
import com.sfl.flybet.domain.pronostic.model.Disapproval;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
import com.sfl.flybet.domain.pronostic.ports.outgoing.DisapprovalPronosticDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PenaltyCustomerDatabase;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PronosticNotificationDatabase;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import unit.projects.InMemoryProjectDatabaseAdapter;
import unit.pronostic.adapter.InMemoryDisapprovalPronosticDatabaseAdapter;
import unit.pronostic.adapter.InMemoryNotificationRepositoryAdapter;
import unit.pronostic.adapter.InMemoryPenaltyCustomerDatabaseAdapter;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class PronosticReliabilityPortTest {


    private static final Long DEF = 12L;
    private static final Long GHI = 34L;
    private static final Long ADM = 0L;
    private static final Long BOBBY_PRJ_ID = 1L;
    private static final Long A1 = 111L;
    private static final Long Z0 = 600L;
    private static final Long T1 = 601L;
    private static final Long T2 = 602L;
    private static final Long T3 = 603L;
    private static final Long T4 = 604L;

    private Customer joeCustomer = new Customer(GHI, "Joe");
    private Customer adminCustomer = new Customer(ADM, "Admin");
    private Customer bobbyCustomer = new Customer(DEF, "Bobby");
    private Pronostic pronoA1 = new Pronostic(A1);
    private Project projectFunOfBobby = new Project(
            "fun", new Amount(BigDecimal.TEN, Devise.EURO), "Objectif...", bobbyCustomer, BOBBY_PRJ_ID);
    AuthenticationCustomerGateway authenticationCustomerGateway = new InMemoryAuthenticationCustomerGateway();
    private DisapprovalPronosticDatabase disapprovaldisapprovalPronosticDatabase = new InMemoryDisapprovalPronosticDatabaseAdapter();
    private ProjectDatabase projectDatabase = new InMemoryProjectDatabaseAdapter();
    private PronosticNotificationDatabase pronosticNotificationDatabase = new InMemoryNotificationRepositoryAdapter();
    private PenaltyCustomerDatabase penaltyCustomerDatabase = new InMemoryPenaltyCustomerDatabaseAdapter();

    private PublishPronosticFacade publishPronosticFacade = new PublishPronosticFacade(projectDatabase);
    private DisapprovalPronosticFacade disapprovalPronosticFacade =
            new DisapprovalPronosticFacade(projectDatabase, disapprovaldisapprovalPronosticDatabase);
    private ManageResultPronosticFacade manageResultPronosticFacade =
            new ManageResultPronosticFacade(projectDatabase, penaltyCustomerDatabase,pronosticNotificationDatabase,
                    authenticationCustomerGateway, disapprovaldisapprovalPronosticDatabase);

    /*
    ----------- Désapprouver un pronostic  -----------
     */
    @Test
    public void shouldDisapprovePronostic() throws ProjectAlreadyExistsException, PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        projectDatabase.add(projectFunOfBobby);
        pronoA1.setMise(new BigDecimal("2.00"));
        pronoA1.setUniteMise("EURO");
        pronoA1.setCote(new BigDecimal("2.35"));
        pronoA1.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoA1);
        joeCustomer.setNbClaims(disapprovalPronosticFacade.NB_CUSTOMER_DISAPPROVALS);
        disapprovalPronosticFacade.disapprovePronostic(joeCustomer, pronoA1);

        assertEquals(disapprovalPronosticFacade.getDisapprovalRaminingCounter(joeCustomer),
                disapprovalPronosticFacade.NB_CUSTOMER_DISAPPROVALS - 1);
    }

    @Test(expected = PronosticNotFoundException.class)
    public void shouldThrowPronosticNotFoundExceptionWhenPronosticNotExists() throws PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        Pronostic pronoZ0 = new Pronostic(Z0);
        disapprovalPronosticFacade.disapprovePronostic(bobbyCustomer, pronoZ0);

        fail("PronosticNotFoundException should be thrown");
    }

    @Test(expected = AlreadyDisapprovedException.class)
    public void shouldThrowAlreadyDisapproveExceptionWhenPronosticAlreadyDisapproveByCustomer() throws PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException, ProjectAlreadyExistsException {
        projectDatabase.add(projectFunOfBobby);
        pronoA1.setMise(new BigDecimal("2.00"));
        pronoA1.setUniteMise("EURO");
        pronoA1.setCote(new BigDecimal("2.35"));
        pronoA1.setStatusProno(StatusProno.WON);

        joeCustomer.setNbClaims(disapprovalPronosticFacade.NB_CUSTOMER_DISAPPROVALS);

        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoA1);
        Disapproval disapprovalA1 = new Disapproval(pronoA1, joeCustomer);
        disapprovaldisapprovalPronosticDatabase.add(disapprovalA1);
        disapprovalPronosticFacade.disapprovePronostic(joeCustomer, pronoA1);

    }

    @Test(expected = DisapprovalableException.class)
    public void shouldNotDisapproveOwnPronostic() throws ProjectAlreadyExistsException, PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        projectDatabase.add(projectFunOfBobby);
        pronoA1.setMise(new BigDecimal("2.00"));
        pronoA1.setUniteMise("EURO");
        pronoA1.setCote(new BigDecimal("2.35"));
        pronoA1.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoA1);
        disapprovalPronosticFacade.disapprovePronostic(bobbyCustomer, pronoA1);

    }

    @Test(expected = DisapprovalableException.class)
    public void shouldThrowDisapprovalableExceptionWhenDisapproveCertifiedProno() throws ProjectAlreadyExistsException, PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        
        projectDatabase.add(projectFunOfBobby);
        pronoA1.setMise(new BigDecimal("2.00"));
        pronoA1.setUniteMise("EURO");
        pronoA1.setCote(new BigDecimal("2.35"));
        pronoA1.setStatusProno(StatusProno.CERTIFIED_WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoA1);

        disapprovalPronosticFacade.disapprovePronostic(joeCustomer, pronoA1);
    }

    @Test
    public void shouldNotDisapprovePronosticWhenNoDisapprovalRemaining() throws ProjectAlreadyExistsException, PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        
        projectDatabase.add(projectFunOfBobby);
        pronoA1.setMise(new BigDecimal("2.00"));
        pronoA1.setUniteMise("EURO");
        pronoA1.setCote(new BigDecimal("2.35"));
        pronoA1.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoA1);

        Pronostic pronoT1 = new Pronostic(T1);
        pronoT1.setMise(new BigDecimal("2.00"));
        pronoT1.setUniteMise("EURO");
        pronoT1.setCote(new BigDecimal("2.35"));
        pronoT1.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoT1);
        Disapproval disapprovalT1 = new Disapproval(pronoT1, joeCustomer);
        disapprovaldisapprovalPronosticDatabase.add(disapprovalT1);

        Pronostic pronoT2 = new Pronostic(T2);
        pronoT2.setMise(new BigDecimal("2.00"));
        pronoT2.setUniteMise("EURO");
        pronoT2.setCote(new BigDecimal("2.35"));
        pronoT2.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoT2);
        Disapproval disapprovalT2 = new Disapproval(pronoT2, joeCustomer);
        disapprovaldisapprovalPronosticDatabase.add(disapprovalT2);

        Pronostic pronoT3 = new Pronostic(T3);
        pronoT3.setMise(new BigDecimal("2.00"));
        pronoT3.setUniteMise("EURO");
        pronoT3.setCote(new BigDecimal("2.35"));
        pronoT3.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoT3);
        Disapproval disapprovalT3 = new Disapproval(pronoT3, joeCustomer);
        disapprovaldisapprovalPronosticDatabase.add(disapprovalT3);

        try {
            disapprovalPronosticFacade.disapprovePronostic(joeCustomer, pronoA1);
        }catch(DisapprovalableException de){
            Assert.assertTrue(disapprovalPronosticFacade.NO_DISAPPROVAL_REMAINING.equals(de.getMessage()));
            return;
        }

        fail();

    }

    @Test
    public void shouldReturn0DisapprovalRemaining() throws ProjectAlreadyExistsException {
        
        projectDatabase.add(projectFunOfBobby);

        joeCustomer.setNbClaims(4);

        Pronostic pronoT1 = new Pronostic(T1);
        pronoT1.setMise(new BigDecimal("1.00"));
        pronoT1.setUniteMise("EURO");
        pronoT1.setCote(new BigDecimal("1.56"));
        pronoT1.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoT1);
        Disapproval disapprovalT1 = new Disapproval(pronoT1, joeCustomer);
        disapprovaldisapprovalPronosticDatabase.add(disapprovalT1);

        Pronostic pronoT2 = new Pronostic(T2);
        pronoT2.setMise(new BigDecimal("2.00"));
        pronoT2.setUniteMise("EURO");
        pronoT2.setCote(new BigDecimal("1.75"));
        pronoT2.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoT2);
        Disapproval disapprovalT2 = new Disapproval(pronoT2, joeCustomer);
        disapprovaldisapprovalPronosticDatabase.add(disapprovalT2);

        Pronostic pronoT3 = new Pronostic(T3);
        pronoT3.setMise(new BigDecimal("2.00"));
        pronoT3.setUniteMise("EURO");
        pronoT3.setCote(new BigDecimal("2.35"));
        pronoT3.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoT3);
        Disapproval disapprovalT3 = new Disapproval(pronoT3, joeCustomer);
        disapprovaldisapprovalPronosticDatabase.add(disapprovalT3);

        Pronostic pronoT4 = new Pronostic(T4);
        pronoT4.setMise(new BigDecimal("2.00"));
        pronoT4.setUniteMise("EURO");
        pronoT4.setCote(new BigDecimal("2.35"));
        pronoT4.setStatusProno(StatusProno.WON);
        projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoT4);
        Disapproval disapprovalT4 = new Disapproval(pronoT4, joeCustomer);
        disapprovaldisapprovalPronosticDatabase.add(disapprovalT4);

        assertEquals(0, disapprovalPronosticFacade.getDisapprovalRaminingCounter(joeCustomer));
    }
    /*
    ----------- Déclarer gagnant un pronostic -----------
     */
    @Test(expected = PronosticNotFoundException.class)
    public void shouldThrowExceptionWhenTryingDeclaringWonNotFoundPronostic()
            throws PronosticNotDecidableException, PronosticNotFoundException {
        manageResultPronosticFacade.declarePronosticWon(Z0);
        fail("PronosticNotFoundException expected...");
    }

    public class AdminActionTest {
        /*PronosticReliabilityPort adminPronosticReliabilityPort = new PronosticReliability(authenticationCustomerGateway,
                projectDatabase, disapprovaldisapprovalPronosticDatabase, pronosticNotificationDatabase, penaltyCustomerDatabase);*/

        /*
    ------------ Sanctionner un tipster  -------------
     */
        @Test
        public void shouldRetrievePenalty() throws AuthorizationException, ProjectAlreadyExistsException, PronosticNotFoundException {
            authenticationCustomerGateway.authenticate(adminCustomer);
            
            projectDatabase.add(projectFunOfBobby);
            pronoA1.setMise(new BigDecimal("2.00"));
            pronoA1.setUniteMise("EURO");
            pronoA1.setCote(new BigDecimal("2.35"));
            pronoA1.setStatusProno(StatusProno.WON);
            projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoA1);
            manageResultPronosticFacade.changePronosticToLostAsAdmin(bobbyCustomer, pronoA1);
            Assert.assertTrue(penaltyCustomerDatabase.getPenaltiesFor(bobbyCustomer).size() == 1);
        }

        @Test (expected = PronosticNotFoundException.class)
        public void shouldThrowAuthorisationExceptionWhenCustomerIsNotOwner() throws AuthorizationException, ProjectAlreadyExistsException, PronosticNotFoundException {
            authenticationCustomerGateway.authenticate(adminCustomer);
            
            projectDatabase.add(projectFunOfBobby);
            pronoA1.setMise(new BigDecimal("2.00"));
            pronoA1.setUniteMise("EURO");
            pronoA1.setCote(new BigDecimal("2.35"));
            pronoA1.setStatusProno(StatusProno.WON);
            projectDatabase.addPronoToProject(BOBBY_PRJ_ID, pronoA1);
            manageResultPronosticFacade.changePronosticToLostAsAdmin(joeCustomer, pronoA1);
        }
    }


}