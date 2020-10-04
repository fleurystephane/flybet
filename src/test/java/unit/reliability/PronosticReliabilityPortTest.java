package unit.reliability;

import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.gateways.AuthenticationCustomerGateway;
import com.sfl.flybet.casestudy.domain.repositories.PenaltyRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.*;
import com.sfl.flybet.casestudy.domain.exceptions.*;
import com.sfl.flybet.casestudy.domain.ports.reliability.PronosticReliabilityPort;
import com.sfl.flybet.casestudy.infrastructure.ports.DisapprovalRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.NotificationRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import com.sfl.flybet.casestudy.domain.adapters.PronosticReliability;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class PronosticReliabilityPortTest {
    AuthenticationCustomerGateway authenticationCustomerGateway = new InMemoryAuthenticationCustomerGateway();
    private DisapprovalRepository disapprovalRepository = new InMemoryDisapprovalRepository();
    private ProjectRepository projectRepository = new InMemoryProjectRepository();
    private Customer bobbyCustomer = new Customer("DEF", "Bobby");
    private Customer joeCustomer = new Customer("GHI", "Joe");
    private Customer adminCustomer = new Customer("ADM", "Admin");
    private Project projectFunOfBobby = new Project(bobbyCustomer, "fun");
    private String idOfProjectFunOfBobby = "12345";
    private NotificationRepository notificationRepository = new InMemoryNotificationRepository();
    private PenaltyRepository penaltyRepository = new InMemoryPenaltyRepository();
    private PronosticReliabilityPort pronosticReliabilityPort = new PronosticReliability(authenticationCustomerGateway,
            projectRepository, disapprovalRepository, notificationRepository, penaltyRepository);
    private Pronostic pronoA1 = new Pronostic("A1");

    /*
    ----------- Désapprouver un pronostic  -----------
     */
    @Test
    public void shouldDisapprovePronostic() throws ProjectAlreadyExistsException, PronosticNotFoundException, DisapprovalableException, AlreadyDisapprovedException {
        projectFunOfBobby.setId(idOfProjectFunOfBobby);
        projectRepository.add(projectFunOfBobby);

        pronoA1.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoA1);
        pronosticReliabilityPort.disapprovePronostic(joeCustomer, pronoA1);

        Assert.assertThat(pronosticReliabilityPort.getDisapprovalRaminingCounter(joeCustomer),
                Matchers.equalTo(PronosticReliabilityPort.NB_CUSTOMER_DISAPPROVALS-1));
    }

    @Test(expected = PronosticNotFoundException.class)
    public void shouldThrowPronosticNotFoundExceptionWhenPronosticNotExists() throws PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        Pronostic pronoZ0 = new Pronostic("Z0");
        pronosticReliabilityPort.disapprovePronostic(bobbyCustomer, pronoZ0);

        fail("PronosticNotFoundException should be thrown");
    }

    @Test(expected = AlreadyDisapprovedException.class)
    public void shouldThrowAlreadyDisapproveExceptionWhenPronosticAlreadyDisapproveByCustomer() throws PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException, ProjectAlreadyExistsException {
        projectFunOfBobby.setId(idOfProjectFunOfBobby);
        projectRepository.add(projectFunOfBobby);

        pronoA1.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoA1);
        Disapproval disapprovalA1 = new Disapproval(pronoA1, joeCustomer);
        disapprovalRepository.add(disapprovalA1);
        pronosticReliabilityPort.disapprovePronostic(joeCustomer, pronoA1);

    }

    @Test(expected = DisapprovalableException.class)
    public void shouldNotDisapproveOwnPronostic() throws ProjectAlreadyExistsException, PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        projectFunOfBobby.setId(idOfProjectFunOfBobby);
        projectRepository.add(projectFunOfBobby);

        pronoA1.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoA1);
        pronosticReliabilityPort.disapprovePronostic(bobbyCustomer, pronoA1);

    }

    @Test(expected = DisapprovalableException.class)
    public void shouldThrowDisapprovalableExceptionWhenDisapproveCertifiedProno() throws ProjectAlreadyExistsException, PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        projectFunOfBobby.setId(idOfProjectFunOfBobby);
        projectRepository.add(projectFunOfBobby);

        pronoA1.setStatus(StatusProno.CERTIFIED_WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoA1);

        pronosticReliabilityPort.disapprovePronostic(joeCustomer, pronoA1);
    }

    @Test
    public void shouldNotDisapprovePronosticWhenNoDisapprovalRemaining() throws ProjectAlreadyExistsException, PronosticNotFoundException, AlreadyDisapprovedException, DisapprovalableException {
        projectFunOfBobby.setId(idOfProjectFunOfBobby);
        projectRepository.add(projectFunOfBobby);

        pronoA1.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoA1);

        Pronostic pronoT1 = new Pronostic("T1");
        pronoT1.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoT1);
        Disapproval disapprovalT1 = new Disapproval(pronoT1, joeCustomer);
        disapprovalRepository.add(disapprovalT1);

        Pronostic pronoT2 = new Pronostic("T2");
        pronoT2.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoT2);
        Disapproval disapprovalT2 = new Disapproval(pronoT2, joeCustomer);
        disapprovalRepository.add(disapprovalT2);

        Pronostic pronoT3 = new Pronostic("T3");
        pronoT3.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoT3);
        Disapproval disapprovalT3 = new Disapproval(pronoT3, joeCustomer);
        disapprovalRepository.add(disapprovalT3);

        try {
            pronosticReliabilityPort.disapprovePronostic(joeCustomer, pronoA1);
        }catch(DisapprovalableException de){
            Assert.assertThat(PronosticReliabilityPort.NO_DISAPPROVAL_REMAINING, Matchers.equalTo(de.getMessage()));
            return;
        }

        fail();

    }

    @Test
    public void shouldReturn0DisapprovalRemaining() throws ProjectAlreadyExistsException {
        projectFunOfBobby.setId(idOfProjectFunOfBobby);
        projectRepository.add(projectFunOfBobby);

        Pronostic pronoT1 = new Pronostic("T1");
        pronoT1.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoT1);
        Disapproval disapprovalT1 = new Disapproval(pronoT1, joeCustomer);
        disapprovalRepository.add(disapprovalT1);

        Pronostic pronoT2 = new Pronostic("T2");
        pronoT2.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoT2);
        Disapproval disapprovalT2 = new Disapproval(pronoT2, joeCustomer);
        disapprovalRepository.add(disapprovalT2);

        Pronostic pronoT3 = new Pronostic("T3");
        pronoT3.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoT3);
        Disapproval disapprovalT3 = new Disapproval(pronoT3, joeCustomer);
        disapprovalRepository.add(disapprovalT3);

        Pronostic pronoT4 = new Pronostic("T4");
        pronoT4.setStatus(StatusProno.WON);
        projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoT4);
        Disapproval disapprovalT4 = new Disapproval(pronoT4, joeCustomer);
        disapprovalRepository.add(disapprovalT4);

        Assert.assertThat(pronosticReliabilityPort.getDisapprovalRaminingCounter(joeCustomer), Matchers.equalTo(0));
    }
    /*
    ----------- Déclarer gagnant un pronostic -----------
     */
    @Test(expected = PronosticNotFoundException.class)
    public void shouldThrowExceptionWhenTryingDeclaringWonNotFoundPronostic() throws PronosticNotFoundException, PronosticNotDecidableException {
        pronosticReliabilityPort.declarePronosticWon("Z0");
        fail("PronosticNotFoundException expected...");
    }

    public class AdminActionTest {
        PronosticReliabilityPort adminPronosticReliabilityPort = new PronosticReliability(authenticationCustomerGateway,
                projectRepository, disapprovalRepository, notificationRepository, penaltyRepository);

        /*
    ------------ Sanctionner un tipster  -------------
     */
        @Test
        public void shouldRetrievePenalty() throws AuthorisationException, ProjectAlreadyExistsException, PronosticNotFoundException {
            authenticationCustomerGateway.authenticate(adminCustomer);
            projectFunOfBobby.setId(idOfProjectFunOfBobby);
            projectRepository.add(projectFunOfBobby);
            pronoA1.setStatus(StatusProno.WON);
            projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoA1);
            adminPronosticReliabilityPort.changePronosticToLostAsAdmin(bobbyCustomer, pronoA1);
            Assert.assertThat(penaltyRepository.getPenaltiesFor(bobbyCustomer), Matchers.hasSize(1));
        }

        @Test (expected = PronosticNotFoundException.class)
        public void shouldThrowAuthorisationExceptionWhenCustomerIsNotOwner() throws AuthorisationException, ProjectAlreadyExistsException, PronosticNotFoundException {
            authenticationCustomerGateway.authenticate(adminCustomer);
            projectFunOfBobby.setId(idOfProjectFunOfBobby);
            projectRepository.add(projectFunOfBobby);
            pronoA1.setStatus(StatusProno.WON);
            projectRepository.addPronoToProject(idOfProjectFunOfBobby, pronoA1);
            adminPronosticReliabilityPort.changePronosticToLostAsAdmin(joeCustomer, pronoA1);
        }
    }


}