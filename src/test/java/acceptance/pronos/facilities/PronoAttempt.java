package acceptance.pronos.facilities;

import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.domain.Tipster;

public class PronoAttempt {
    private Pronostic pronostic;
    private String projectName;
    private Tipster customer;
    private String pronoTitle;
    private String projectId;

    public void setCustomer(Tipster tipster) {
        this.customer = tipster;
    }

    public void setProno(Pronostic pronostic) {
        this.pronostic = pronostic;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Tipster getCustomer() {
        return customer;
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
