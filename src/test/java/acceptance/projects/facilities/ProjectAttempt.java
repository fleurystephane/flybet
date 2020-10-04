package acceptance.projects.facilities;

import com.sfl.flybet.casestudy.domain.Amount;

import java.time.LocalDate;

public class ProjectAttempt {
    private final String projectTitle;
    private final Amount bkAmount;
    private String objectif;
    private LocalDate endProject;
    private String id;

    public ProjectAttempt(String id, String projectTitle, Amount bkAmount) {
        this.id = id;
        this.projectTitle = projectTitle;
        this.bkAmount = bkAmount;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public LocalDate getEndProject() {
        return endProject;
    }

    public void setEndProject(LocalDate endProject) {
        this.endProject = endProject;
    }

    public String getObjectif() {
        return objectif;
    }

    public Amount getBkAmount() {
        return bkAmount;
    }

    public String getId() {
        return id;
    }
}
