package acceptance.projects.facilities;

import com.sfl.flybet.domain.common.model.Amount;

import java.time.LocalDate;

public class ProjectAttempt {
    private final String projectTitle;
    private final Amount bkAmount;
    private String objectif;
    private LocalDate endProject;

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;

    public ProjectAttempt(Long id, String projectTitle, Amount bkAmount) {
        this.id = id;
        this.projectTitle = projectTitle;
        this.bkAmount = bkAmount;
    }

    public ProjectAttempt(String projectTitle, Amount amount) {
        this.projectTitle = projectTitle;
        this.bkAmount = amount;
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

    public Long getId() {
        return id;
    }
}
