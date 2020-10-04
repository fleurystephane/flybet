package acceptance.customers;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Penalty;
import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.adapters.PronosticReliability;
import com.sfl.flybet.casestudy.domain.ports.reliability.PenaltyCustomerPort;
import com.sfl.flybet.casestudy.domain.repositories.PenaltyRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.CustomerRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.DisapprovalRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.NotificationRepository;
import com.sfl.flybet.casestudy.infrastructure.ports.ProjectRepository;
import io.cucumber.java8.En;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.Optional;
import java.util.Set;

public class PenaltySteps implements En {

    public PenaltySteps(CustomerRepository customerRepository, ProjectRepository projectRepository,
                        PenaltyRepository penaltyRepository) {
        And("^le propriétaire \"([^\"]*)\" du pronostic \"([^\"]*)\" est sanctionné d'un avertissement$", (String pseudoTipster, String pronoId) -> {
            Optional<Customer> tipster = customerRepository.byPseudo(pseudoTipster);
            Set<Penalty> penaltiesFor = penaltyRepository.getPenaltiesFor(tipster.get());
            Assert.assertThat(penaltiesFor, Matchers.notNullValue());
            Assert.assertThat(penaltiesFor.stream().filter(penalty -> penalty.getPronostic().getId().equals(pronoId)).count(), Matchers.equalTo(1L));
        });
        And("^\"([^\"]*)\" a déjà reçu un avertissement pour le pronostic \"([^\"]*)\"$", (String pseudoTipster, String pronoId) -> {
            penaltyRepository.addPenalty(customerRepository.byPseudo(pseudoTipster).get(), projectRepository.findPronosticById(pronoId).get());
        });
        Then("^je vérifie que \"([^\"]*)\" est sous (\\d+) avertissements$", (String pseudoTipster, Integer nbPenalties) -> {
            Optional<Customer> tipster = customerRepository.byPseudo(pseudoTipster);
            Set<Penalty> penaltiesFor = penaltyRepository.getPenaltiesFor(tipster.get());
            Assert.assertThat(penaltiesFor, Matchers.notNullValue());
            Assert.assertThat(penaltiesFor.stream().filter(penalty -> penalty.getOwner().getPseudo().equals(pseudoTipster)).count(), Matchers.equalTo(2L));
        });
    }
}
