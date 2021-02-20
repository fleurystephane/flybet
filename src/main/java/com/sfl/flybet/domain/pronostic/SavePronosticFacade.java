package com.sfl.flybet.domain.pronostic;

import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.ports.incoming.SavePronostic;

public class SavePronosticFacade implements SavePronostic {
    private final ProjectDatabase projectDatabase;

    public SavePronosticFacade(ProjectDatabase projectDatabase) {
        this.projectDatabase = projectDatabase;
    }

    @Override
    public void save(Pronostic pronostic, Long projectId) {
        projectDatabase.addPronoToProject(projectId, pronostic);
    }
}
