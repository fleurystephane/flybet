package unit.pronostic;

import com.sfl.flybet.casestudy.domain.exceptions.BankrolInsufficientException;
import com.sfl.flybet.casestudy.domain.exceptions.AlreadyPublishedPronosticException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.common.model.Amount;
import com.sfl.flybet.domain.common.model.Devise;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.customeraccount.model.CustomerAccount;
import com.sfl.flybet.domain.customeraccount.ports.outgoing.CustomerAccountDatabase;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.ChangeCotePronosticFacade;
import com.sfl.flybet.domain.pronostic.PublishPronosticFacade;
import com.sfl.flybet.domain.pronostic.SavePronosticFacade;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.model.StatusProno;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import unit.customer.adapter.InMemoryCustomerAccountDatabaseAdapter;
import unit.customer.adapter.InMemoryCustomerDatabaseAdapter;
import unit.projects.InMemoryProjectDatabaseAdapter;

import java.math.BigDecimal;
import java.util.Optional;

@RunWith(HierarchicalContextRunner.class)
public class PublishPronoTest {

    private static final Long FUN_PROJECT_ID = 1L;
    private static final Long GLOBAL_PROJECT_ID = 2L;
    private static final Long A2 = 22L;
    private static final Long A1 = 11L;
    private static final Long ABC = 100L;
    private static final Long ADM = 0L;
    private static final Long A3 = 33L;
    private final CustomerDatabase customerDatabase = new InMemoryCustomerDatabaseAdapter();
    private final CustomerAccountDatabase customerAccountDatabase = new InMemoryCustomerAccountDatabaseAdapter();
    private final ProjectDatabase massiProjectDatabase = new InMemoryProjectDatabaseAdapter();
    private Customer tipsterMassi = new Customer(ABC, "Massi");
    private Customer admin = new Customer(ADM, "Admin");
    private Pronostic pronoA1 = new Pronostic(A1);
    private Pronostic pronoA2 = new Pronostic(A2);
    private CustomerAccount adminAccount = new CustomerAccount(ADM, new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
    private CustomerAccount tipsterMassiAccount = new CustomerAccount(ABC, new Amount(new BigDecimal("2000.00"), Devise.CREDIT));
    private Project funProject = new Project("fun", new Amount(new BigDecimal("200.00"), Devise.CREDIT),
            "Objectif...", tipsterMassi, FUN_PROJECT_ID);
    private Project globalProject = new Project("global", new Amount(new BigDecimal("200.00"), Devise.CREDIT),
            "Objectif...", tipsterMassi, GLOBAL_PROJECT_ID);


    @Before
    public void beforeTheClass() {

        customerDatabase.add(tipsterMassi);
        customerDatabase.add(admin);
        customerAccountDatabase.addAccount(adminAccount);
        customerAccountDatabase.addAccount(tipsterMassiAccount);
    }

    private BigDecimal getBigDecimalCote(String cote) {
        return new BigDecimal(Float.parseFloat(cote));
    }

    @Test(expected = AlreadyPublishedPronosticException.class)
    public void shouldThrowExceptionIfPronoAlreadyPublished()
            throws AlreadyPublishedPronosticException, BankrolInsufficientException, ProjectAlreadyExistsException, ProjectNotFoundException {
        pronoA1.setMise(new BigDecimal("20.00"));
        pronoA1.setUniteMise("EURO");
        pronoA1.setStatusProno(StatusProno.PUBLISHED);
        massiProjectDatabase.add(funProject);
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setCote(getBigDecimalCote("1.55"));
        pronoA2.setMise(new BigDecimal("20.00"));
        pronoA2.setUniteMise("EURO");

        new PublishPronosticFacade(massiProjectDatabase).publish(pronoA2, FUN_PROJECT_ID);
    }


    @Test
    public void shouldHavePronoPublished() throws ProjectAlreadyExistsException, BankrolInsufficientException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        massiProjectDatabase.add(globalProject);
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setCote(getBigDecimalCote("1.90"));
        pronoA2.setMise(new BigDecimal("20.00"));
        pronoA2.setUniteMise("EURO");
        try {
            new PublishPronosticFacade(massiProjectDatabase).publish(pronoA2, GLOBAL_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException e) {
            Assert.fail("Le prono A2 devrait etre publié...");
        }
        Assert.assertTrue("Le prono A2 aurait du etre publie dans le projet \"global\"",
                massiProjectDatabase.allPronos(GLOBAL_PROJECT_ID).contains(pronoA2));
    }

    @Test
    public void shouldPublishPronoWithSavedOtherProno() throws ProjectAlreadyExistsException, BankrolInsufficientException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setCote(getBigDecimalCote("2.40"));
        pronoA2.setMise(new BigDecimal("20.00"));
        pronoA2.setUniteMise("EURO");
        try {
            new PublishPronosticFacade(massiProjectDatabase).publish(pronoA2, FUN_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException | ProjectNotFoundException e) {
            Assert.fail("Le prono2 devrait eêtre publié.... même si un prono enregistré dans projet est présent...");
        }
        Assert.assertTrue("Le prono A2 aurait du etre publie dans le projet \"fun\"",
                massiProjectDatabase.allPronos(FUN_PROJECT_ID).contains(pronoA2));
    }


    @Test
    public void shouldPublishSavedPronoAndVerifyStatusHasChanged() throws ProjectAlreadyExistsException, BankrolInsufficientException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        pronoA1.setStatusProno(StatusProno.DRAFT);
        pronoA1.setCote(getBigDecimalCote("3.90"));
        pronoA1.setMise(new BigDecimal("20.00"));
        pronoA1.setUniteMise("EURO");
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        try {
            new PublishPronosticFacade(massiProjectDatabase).publish(pronoA1, FUN_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException e) {
            Assert.fail("Le prono A1 etant en brouillon, je dois pouvoir le publier...");
        }
        Assert.assertTrue(massiProjectDatabase.allPronos(FUN_PROJECT_ID).iterator().next().isPublished());
        Assert.assertEquals(1, massiProjectDatabase.allPronos(FUN_PROJECT_ID).size());
    }

    @Test
    public void shouldPublishPronoWithSavedAndDecidedPronos() throws ProjectAlreadyExistsException, BankrolInsufficientException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        pronoA1.setStatusProno(StatusProno.DRAFT);
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setMise(new BigDecimal("20.00"));
        pronoA2.setCote(new BigDecimal("2.10"));
        pronoA2.setUniteMise("EURO");
        pronoA2.setStatusProno(StatusProno.WON);
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA2);
        Pronostic pronoA3 = new Pronostic(A3);
        pronoA3.setCote(getBigDecimalCote("2.22"));
        pronoA3.setMise(new BigDecimal("20.00"));
        pronoA3.setUniteMise("EURO");
        try {
            new PublishPronosticFacade(massiProjectDatabase).publish(pronoA3, FUN_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException | ProjectNotFoundException e) {
            Assert.fail("Le prono A3 devrait être publié car en présence d'un brouillon et d'un prono décidé (gagnant)...");
        }
        Optional<Pronostic> pronoFoundPublished = massiProjectDatabase.allPronos(FUN_PROJECT_ID).stream()
                .filter(Pronostic::isPublished).findAny();
        Assert.assertTrue(pronoFoundPublished.isPresent());
        Assert.assertEquals(A3,
                pronoFoundPublished.get().getId());
        Assert.assertTrue(pronoFoundPublished.get().isPublished());
    }

    @Test
    public void shouldPublishSavedPronoWithOthersSavedPronos() throws ProjectAlreadyExistsException, BankrolInsufficientException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        pronoA1.setStatusProno(StatusProno.DRAFT);
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setStatusProno(StatusProno.DRAFT);
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA2);
        Pronostic pronoA3 = new Pronostic(A3);
        pronoA3.setStatusProno(StatusProno.DRAFT);
        pronoA3.setCote(getBigDecimalCote("1.90"));
        pronoA3.setMise(new BigDecimal("20.00"));
        pronoA3.setUniteMise("EURO");
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA3);
        try {
            new PublishPronosticFacade(massiProjectDatabase).publish(pronoA3, FUN_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException e) {
            Assert.fail("Le prono A3 devrait être publié car en présence d'un brouillon et d'un prono décidé (gagnant)...");
        }
        Optional<Pronostic> pronoFoundPublished = massiProjectDatabase.allPronos(FUN_PROJECT_ID).stream().filter(Pronostic::isPublished).findAny();
        Assert.assertTrue(pronoFoundPublished.isPresent());
        Assert.assertEquals(A3, pronoFoundPublished.get().getId());
        Assert.assertTrue(pronoFoundPublished.get().isPublished());
        Assert.assertEquals(2, massiProjectDatabase.allPronos(FUN_PROJECT_ID).stream().filter(Pronostic::isDraft).count());
    }

    @Test
    public void shouldSavePronoEvenIfPublishedPronoExists() throws ProjectAlreadyExistsException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        pronoA1.setMise(new BigDecimal("20.00"));
        pronoA1.setUniteMise("EURO");
        pronoA1.setStatusProno(StatusProno.PUBLISHED);
        massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        new SavePronosticFacade(massiProjectDatabase).save(pronoA2, FUN_PROJECT_ID);

        Assert.assertEquals(1, massiProjectDatabase.allPronos(FUN_PROJECT_ID).stream().filter(Pronostic::isDraft).count());
        Optional<Pronostic> pronoFoundDraft = massiProjectDatabase.allPronos(FUN_PROJECT_ID).stream().filter(Pronostic::isDraft).findAny();
        Assert.assertTrue(pronoFoundDraft.isPresent());
        Assert.assertEquals(A2, pronoFoundDraft.get().getId());
        Assert.assertEquals(1, massiProjectDatabase.allPronos(FUN_PROJECT_ID).stream().filter(Pronostic::isPublished).count());
        Optional<Pronostic> pronoFoundPublished = massiProjectDatabase.allPronos(FUN_PROJECT_ID).stream().filter(Pronostic::isPublished).findAny();
        Assert.assertTrue(pronoFoundPublished.isPresent());
        Assert.assertEquals(A1, pronoFoundPublished.get().getId());
    }

    @Test(expected = BankrolInsufficientException.class)
    public void shouldRejectPronoWhenBetGreaterThanRemainingBankrol() throws ProjectAlreadyExistsException, AlreadyPublishedPronosticException, BankrolInsufficientException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        pronoA1.setUniteMise("EURO");
        pronoA1.setMise(new BigDecimal("100.00"));
        pronoA1.setCote(new BigDecimal("1.89"));
        pronoA1.setStatusProno(StatusProno.LOST);
        massiProjectDatabase.addPronoToProject(funProject.getId(), pronoA1);
        // Avec une BK initiale de 200 et une mise perdue de 100, il ne reste plus que 100 EUROS
        pronoA2.setMise(new BigDecimal("110.00"));
        pronoA2.setUniteMise("EURO");
        pronoA2.setCote(new BigDecimal("1.60"));
        new PublishPronosticFacade(massiProjectDatabase).publish(pronoA2, funProject.getId());

        Assert.fail("Prono should be rejected!!!");
    }

    @Test(expected = BankrolInsufficientException.class)
    public void shouldRejectPronoWhenBetGreaterThanRemainingBankrolWhenManyPronos() throws ProjectAlreadyExistsException, AlreadyPublishedPronosticException, BankrolInsufficientException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        pronoA1.setUniteMise("EURO");
        pronoA1.setMise(new BigDecimal("100.00"));
        pronoA1.setCote(new BigDecimal("1.89"));
        pronoA1.setStatusProno(StatusProno.LOST);
        Pronostic pronoA3 = new Pronostic(A3);
        pronoA3.setUniteMise("EURO");
        pronoA3.setMise(new BigDecimal("50.00"));
        pronoA3.setCote(new BigDecimal("2.00"));
        pronoA3.setStatusProno(StatusProno.CERTIFIED_WON);
        massiProjectDatabase.addPronoToProject(funProject.getId(), pronoA1);
        massiProjectDatabase.addPronoToProject(funProject.getId(), pronoA3);
        // Avec une BK initiale de 200 et une mise perdue de 100, il ne reste plus que 100 EUROS
        pronoA2.setMise(new BigDecimal("160.00"));
        pronoA2.setUniteMise("EURO");
        pronoA2.setCote(new BigDecimal("1.60"));
        new PublishPronosticFacade(massiProjectDatabase).publish(pronoA2, funProject.getId());

        Assert.fail("Prono should be rejected!!!");
    }

    @Test
    public void shouldAcceptPronoWhenBetEqualsToRemainingBankrol() throws ProjectAlreadyExistsException, AlreadyPublishedPronosticException, BankrolInsufficientException, ProjectNotFoundException {
        massiProjectDatabase.add(funProject);
        pronoA1.setUniteMise("EURO");
        pronoA1.setMise(new BigDecimal("100.00"));
        pronoA1.setCote(new BigDecimal("1.89"));
        pronoA1.setStatusProno(StatusProno.LOST);
        massiProjectDatabase.addPronoToProject(funProject.getId(), pronoA1);
        // Avec une BK initiale de 200 et une mise perdue de 100, il ne reste plus que 100 EUROS
        pronoA2.setMise(new BigDecimal("100.00"));
        pronoA2.setUniteMise("EURO");
        pronoA2.setCote(new BigDecimal("1.60"));
        new PublishPronosticFacade(massiProjectDatabase).publish(pronoA2, funProject.getId());
    }


    public class AroundCote {

        @Test(expected = IllegalArgumentException.class)
        public void shouldRejectPronosticWithCoteLessThanCoteMini() throws BankrolInsufficientException {
            pronoA1.setStatusProno(null);
            pronoA1.setCote(new BigDecimal("1.09"));
            try {
                new PublishPronosticFacade(massiProjectDatabase).publish(pronoA1, FUN_PROJECT_ID);
            } catch (AlreadyPublishedPronosticException | ProjectNotFoundException e) {
                Assert.fail("Une erreur IllegalArgumentException etait attendue car la cote est trop basse...");
            }
        }

        @Test
        public void shouldAcceptPronosticWithCoteEqualsCoteMini() throws ProjectAlreadyExistsException, BankrolInsufficientException {
            massiProjectDatabase.add(funProject);
            pronoA1.setStatusProno(null);
            pronoA1.setCote(getBigDecimalCote("1.10"));
            pronoA1.setMise(new BigDecimal("20.00"));
            pronoA1.setUniteMise("EURO");
            try {
                new PublishPronosticFacade(massiProjectDatabase).publish(pronoA1, FUN_PROJECT_ID);
            } catch (AlreadyPublishedPronosticException | ProjectNotFoundException e) {
                Assert.fail("Une erreur IllegalArgumentException etait attendue car la cote est trop basse...");
            }

            Optional<Pronostic> pronoPublished = null;
            try {
                pronoPublished = massiProjectDatabase.allPronos(FUN_PROJECT_ID).stream()
                        .filter(Pronostic::isPublished).findAny();
            } catch (ProjectNotFoundException e) {
                Assert.fail("Le projer aurait du etre trouvé!!");
            }

            Assert.assertTrue(pronoPublished.isPresent());
            Assert.assertEquals(getBigDecimalCote("1.10"), pronoPublished.get().getCote());
        }

        @Test
        public void shouldNotAcceptModifCoteWhenPronoIsGagnant() throws PronosticNotFoundException, ProjectAlreadyExistsException, ProjectNotFoundException {
            massiProjectDatabase.add(funProject);
            pronoA1.setMise(new BigDecimal("20.00"));
            pronoA1.setUniteMise("EURO");
            pronoA1.setStatusProno(StatusProno.WON);
            pronoA1.setCote(getBigDecimalCote("1.45"));
            massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);

            new ChangeCotePronosticFacade(massiProjectDatabase)
                    .changeCoteValue(pronoA1, getBigDecimalCote("1.32"));

            Assert.assertEquals(getBigDecimalCote("1.45"),
                    massiProjectDatabase.allPronos(FUN_PROJECT_ID).iterator().next().getCote());

        }

        @Test
        public void shouldAcceptModifCoteWhenPronoIsNotDecided() throws PronosticNotFoundException, ProjectAlreadyExistsException, ProjectNotFoundException {
            massiProjectDatabase.add(funProject);
            pronoA1.setMise(new BigDecimal("20.00"));
            pronoA1.setUniteMise("EURO");
            pronoA1.setStatusProno(StatusProno.PUBLISHED);
            pronoA1.setCote(new BigDecimal("1.45"));
            massiProjectDatabase.addPronoToProject(FUN_PROJECT_ID, pronoA1);

            new ChangeCotePronosticFacade(massiProjectDatabase)
                    .changeCoteValue(pronoA1, getBigDecimalCote("1.32"));

            Assert.assertEquals(getBigDecimalCote("1.32"),
                    massiProjectDatabase.allPronos(FUN_PROJECT_ID).iterator().next().getCote());

        }
    }

}
