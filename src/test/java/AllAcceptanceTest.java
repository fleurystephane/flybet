import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
/*@CucumberOptions(
        format = {"json:target/cucumber-report/cucumber.json"}
)*/
@CucumberOptions(
        plugin = {
                "pretty", "html:target/Destination"
        },
        strict = true
/*        tags = {"@fiabilite"
                , "@declarerPerdant"
                , "@avertissement"
                ,"@créationDeProjet"
                ,"@projetModification"
        }*/
)
public class AllAcceptanceTest {
}