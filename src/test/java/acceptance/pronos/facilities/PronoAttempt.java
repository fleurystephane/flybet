package acceptance.pronos.facilities;


import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.pronostic.model.Pronostic;

public class PronoAttempt {
    private Pronostic pronostic;
    private String projectName;
    private Customer tipster;
    private String projectId;

    public void setCustomer(Customer tipster) {
        this.tipster = tipster;
    }

    public void setProno(Pronostic pronostic) {
        this.pronostic = pronostic;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Customer getCustomer() {
        return tipster;
    }


    public Pronostic getPronostic() {
        return pronostic;
    }

    @Override
    public String toString() {
        return "PronoAttempt{" +
                "pronostic=" + pronostic +
                ", projectId='" + projectId + '\'' +
                '}';
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
