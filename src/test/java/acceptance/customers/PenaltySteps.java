package acceptance.customers;

import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.customer.ports.outgoing.CustomerDatabase;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Penalty;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PenaltyCustomerDatabase;
import io.cucumber.java8.En;
import org.junit.Assert;

import java.util.Optional;
import java.util.Set;

public class PenaltySteps implements En {

    public PenaltySteps(CustomerDatabase customerDatabase, ProjectDatabase projectDatabase,
                        PenaltyCustomerDatabase penaltyCustomerDatabase) {
        And("^le propriétaire \"([^\"]*)\" du pronostic \"([^\"]*)\" est sanctionné d'un avertissement$", (String pseudoTipster, String pronoId) -> {
            Optional<Customer> tipster = customerDatabase.getCustomerByPseudo(pseudoTipster);
            Set<Penalty> penaltiesFor = penaltyCustomerDatabase.getPenaltiesFor(tipster.get());
            Assert.assertNotNull(penaltiesFor);
            Assert.assertEquals(1L, penaltiesFor.stream().filter(penalty -> penalty.getPronostic().getId().equals(Long.valueOf(pronoId))).count());
        });
        And("^\"([^\"]*)\" a déjà reçu un avertissement pour le pronostic \"([^\"]*)\"$", (String pseudoTipster, String pronoId) -> {
            penaltyCustomerDatabase.addPenalty(customerDatabase.getCustomerByPseudo(pseudoTipster).get(), projectDatabase.findPronosticById(Long.valueOf(pronoId)).get());
        });
        Then("^je vérifie que \"([^\"]*)\" est sous (\\d+) avertissements$", (String pseudoTipster, Integer nbPenalties) -> {
            Optional<Customer> tipster = customerDatabase.getCustomerByPseudo(pseudoTipster);
            Set<Penalty> penaltiesFor = penaltyCustomerDatabase.getPenaltiesFor(tipster.get());
            Assert.assertNotNull(penaltiesFor);
            Assert.assertEquals(2L, penaltiesFor.stream().filter(penalty -> penalty.getOwner().getPseudo().equals(pseudoTipster)).count());
        });
    }
}
