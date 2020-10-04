package unit.pronostic;

import com.sfl.flybet.casestudy.domain.*;
import com.sfl.flybet.casestudy.domain.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryCustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryCustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.adapters.InMemoryProjectRepository;
import com.sfl.flybet.casestudy.domain.exceptions.AlreadyPublishedPronosticException;
import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerAccountRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import com.sfl.flybet.casestudy.domain.adapters.PublishProno;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Optional;

@RunWith(HierarchicalContextRunner.class)
public class PublishPronoTest {

    private static final String FUN_PROJECT_ID = "fun-1";
    private static final String GLOBAL_PROJECT_ID = "global-1";
    private final CustomerRepository customerRepository = new InMemoryCustomerRepository();
    private final CustomerAccountRepository customerAccountRepository = new InMemoryCustomerAccountRepository();
    private final ProjectRepository massiProjectRepositories = new InMemoryProjectRepository();
    private Customer tipsterMassi = new Customer("ABC", "Massi");
    private Customer admin = new Customer("ADM", "Admin");
    private Pronostic pronoA1 = new Pronostic("A1");
    private Pronostic pronoA2 = new Pronostic("A2");
    private CustomerAccount adminAccount = new CustomerAccount("ADM", new Amount(new BigDecimal("150000.00"), Devise.CREDIT));
    private CustomerAccount tipsterMassiAccount = new CustomerAccount("ABC", new Amount(new BigDecimal("2000.00"), Devise.CREDIT));
    private Project funProject = new Project(tipsterMassi, "fun", new Amount(new BigDecimal("200.00"), Devise.CREDIT));
    private Project globalProject = new Project(tipsterMassi, "global", new Amount(new BigDecimal("200.00"), Devise.CREDIT));

    @Before
    public void beforeTheClass(){

        customerRepository.add(tipsterMassi);
        customerRepository.add(admin);
        customerAccountRepository.add(adminAccount);
        customerAccountRepository.add(tipsterMassiAccount);
        funProject.setId(FUN_PROJECT_ID);
        globalProject.setId(GLOBAL_PROJECT_ID);
    }

    private BigDecimal getBigDecimalCote(String cote) {
        return new BigDecimal(Float.parseFloat(cote));
    }

    @Test(expected = AlreadyPublishedPronosticException.class)
    public void shouldThrowExceptionIfPronoAlreadyPublished() throws AlreadyPublishedPronosticException, ProjectAlreadyExistsException {
        pronoA1.setStatus(StatusProno.PUBLISHED);
        massiProjectRepositories.add(funProject);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setCote(getBigDecimalCote("1.55"));

        new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).publish(pronoA2, FUN_PROJECT_ID);
    }


    @Test
    public void shouldHavePronoPublished() throws ProjectAlreadyExistsException {
        massiProjectRepositories.add(funProject);
        massiProjectRepositories.add(globalProject);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setCote(getBigDecimalCote("1.90"));

        try {
            new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).publish(pronoA2, GLOBAL_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException e) {
            Assert.fail("Le prono A2 devrait etre publié...");
        }
        Assert.assertTrue("Le prono A2 aurait du etre publie dans le projet \"global\"",
                massiProjectRepositories.all(GLOBAL_PROJECT_ID).contains(pronoA2));
    }

    @Test
    public void shouldPublishPronoWithSavedOtherProno() throws ProjectAlreadyExistsException {
        massiProjectRepositories.add(funProject);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setCote(getBigDecimalCote("2.40"));
        try {
            new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).publish(pronoA2, FUN_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException e) {
            Assert.fail("Le prono2 devrait eêtre publié.... même si un prono enregistré dans projet est présent...");
        }
        Assert.assertTrue("Le prono A2 aurait du etre publie dans le projet \"fun\"",
                massiProjectRepositories.all(FUN_PROJECT_ID).contains(pronoA2));
    }




    @Test
    public void shouldPublishSavedPronoAndVerifyStatusHasChanged() throws ProjectAlreadyExistsException {
        massiProjectRepositories.add(funProject);
        pronoA1.setStatus(StatusProno.DRAFT);
        pronoA1.setCote(getBigDecimalCote("3.90"));
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        try {
            new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).publish(pronoA1, FUN_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException e) {
            Assert.fail("Le prono A1 etant en brouillon, je dois pouvoir le publier...");
        }
        Assert.assertTrue(massiProjectRepositories.all(FUN_PROJECT_ID).iterator().next().isPublished());
        Assert.assertEquals(1, massiProjectRepositories.all(FUN_PROJECT_ID).size());
    }

    @Test
    public void shouldPublishPronoWithSavedAndDecidedPronos() throws ProjectAlreadyExistsException {
        massiProjectRepositories.add(funProject);
        pronoA1.setStatus(StatusProno.DRAFT);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setStatus(StatusProno.WON);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA2);
        Pronostic pronoA3 = new Pronostic("A3");
        pronoA3.setCote(getBigDecimalCote("2.22"));
        try {
            new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).publish(pronoA3, FUN_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException e) {
            Assert.fail("Le prono A3 devrait être publié car en présence d'un brouillon et d'un prono décidé (gagnant)...");
        }
        Optional<Pronostic> pronoFoundPublished = massiProjectRepositories.all(FUN_PROJECT_ID).stream()
                .filter(Pronostic::isPublished).findAny();
        Assert.assertTrue(pronoFoundPublished.isPresent());
        Assert.assertEquals("A3",
                pronoFoundPublished.get().getId());
        Assert.assertTrue(pronoFoundPublished.get().isPublished());
    }

    @Test
    public void shouldPublishSavedPronoWithOthersSavedPronos() throws ProjectAlreadyExistsException {
        massiProjectRepositories.add(funProject);
        pronoA1.setStatus(StatusProno.DRAFT);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        pronoA2.setStatus(StatusProno.DRAFT);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA2);
        Pronostic pronoA3 = new Pronostic("A3");
        pronoA3.setStatus(StatusProno.DRAFT);
        pronoA3.setCote(getBigDecimalCote("1.90"));
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA3);
        try {
            new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).publish(pronoA3, FUN_PROJECT_ID);
        } catch (AlreadyPublishedPronosticException e) {
            Assert.fail("Le prono A3 devrait être publié car en présence d'un brouillon et d'un prono décidé (gagnant)...");
        }
        Optional<Pronostic> pronoFoundPublished = massiProjectRepositories.all(FUN_PROJECT_ID).stream().filter(Pronostic::isPublished).findAny();
        Assert.assertTrue(pronoFoundPublished.isPresent());
        Assert.assertEquals("A3", pronoFoundPublished.get().getId());
        Assert.assertTrue(pronoFoundPublished.get().isPublished());
        Assert.assertEquals(2, massiProjectRepositories.all(FUN_PROJECT_ID).stream().filter(Pronostic::isDraft).count());
    }

    @Test
    public void shouldSavePronoEvenIfPublishedPronoExists() throws ProjectAlreadyExistsException {
        massiProjectRepositories.add(funProject);
        pronoA1.setStatus(StatusProno.PUBLISHED);
        massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);
        new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).save(pronoA2, FUN_PROJECT_ID);

        Assert.assertEquals(1, massiProjectRepositories.all(FUN_PROJECT_ID).stream().filter(Pronostic::isDraft).count());
        Optional<Pronostic> pronoFoundDraft = massiProjectRepositories.all(FUN_PROJECT_ID).stream().filter(Pronostic::isDraft).findAny();
        Assert.assertTrue(pronoFoundDraft.isPresent());
        Assert.assertEquals("A2", pronoFoundDraft.get().getId());
        Assert.assertEquals(1, massiProjectRepositories.all(FUN_PROJECT_ID).stream().filter(Pronostic::isPublished).count());
        Optional<Pronostic> pronoFoundPublished = massiProjectRepositories.all(FUN_PROJECT_ID).stream().filter(Pronostic::isPublished).findAny();
        Assert.assertTrue(pronoFoundPublished.isPresent());
        Assert.assertEquals("A1", pronoFoundPublished.get().getId());
    }



    public class AroundCote {

        @Test(expected = IllegalArgumentException.class)
        public void shouldRejectPronosticWithCoteLessThanCoteMini() {
            pronoA1.setStatus(null);
            pronoA1.setCote(new BigDecimal("1.09"));
            try {
                new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).publish(pronoA1, FUN_PROJECT_ID);
            } catch (AlreadyPublishedPronosticException e) {
                Assert.fail("Une erreur IllegalArgumentException etait attendue car la cote est trop basse...");
            }
        }

        @Test
        public void shouldAcceptPronosticWithCoteEqualsCoteMini() throws ProjectAlreadyExistsException {
            massiProjectRepositories.add(funProject);
            pronoA1.setStatus(null);
            pronoA1.setCote(getBigDecimalCote("1.10"));
            try {
                new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories).publish(pronoA1, FUN_PROJECT_ID);
            } catch (AlreadyPublishedPronosticException e) {
                Assert.fail("Une erreur IllegalArgumentException etait attendue car la cote est trop basse...");
            }

            Optional<Pronostic> pronoPublished = massiProjectRepositories.all(FUN_PROJECT_ID).stream()
                    .filter(Pronostic::isPublished).findAny();

            Assert.assertTrue(pronoPublished.isPresent());
            Assert.assertEquals(getBigDecimalCote("1.10"), pronoPublished.get().getCote());
        }

        @Test
        public void shouldNotAcceptModifCoteWhenPronoIsGagnant() throws PronosticNotFoundException, ProjectAlreadyExistsException {
            massiProjectRepositories.add(funProject);
            pronoA1.setStatus(StatusProno.WON);
            pronoA1.setCote(getBigDecimalCote("1.45"));
            massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);

            new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories)
                    .changeCoteValue(pronoA1, getBigDecimalCote("1.32"));

            Assert.assertEquals(getBigDecimalCote("1.45"),
                    massiProjectRepositories.all(FUN_PROJECT_ID).iterator().next().getCote());

        }
        @Test
        public void shouldAcceptModifCoteWhenPronoIsNotDecided() throws PronosticNotFoundException, ProjectAlreadyExistsException {
            massiProjectRepositories.add(funProject);
            pronoA1.setStatus(StatusProno.PUBLISHED);
            pronoA1.setCote(new BigDecimal("1.45"));
            massiProjectRepositories.addPronoToProject(FUN_PROJECT_ID, pronoA1);

            new PublishProno(java.util.Optional.ofNullable(tipsterMassi).get(), massiProjectRepositories)
                    .changeCoteValue(pronoA1, getBigDecimalCote("1.32"));

            Assert.assertEquals(getBigDecimalCote("1.32"),
                    massiProjectRepositories.all(FUN_PROJECT_ID).iterator().next().getCote());

        }
    }

}
