package com.sfl.flybet.domain.pronostic;

import com.sfl.flybet.casestudy.domain.exceptions.PronosticNotFoundException;
import com.sfl.flybet.domain.project.ports.outgoing.ProjectDatabase;
import com.sfl.flybet.domain.pronostic.model.Pronostic;
import com.sfl.flybet.domain.pronostic.ports.incoming.ChangeCotePronostic;

import java.math.BigDecimal;
import java.util.Optional;

public class ChangeCotePronosticFacade implements ChangeCotePronostic {
    private final ProjectDatabase projectDatabase;

    public ChangeCotePronosticFacade(ProjectDatabase projectDatabase) {
        this.projectDatabase = projectDatabase;
    }

    @Override
    public void changeCoteValue(Pronostic pronostic, BigDecimal newCote) throws PronosticNotFoundException {
        Optional<Pronostic> pronosExistant =
                projectDatabase.findPronosticById(pronostic.getId());
        if(pronosExistant.isPresent()){
            if(!pronosExistant.get().isDecided()){
                pronosExistant.get().setCote(newCote);
            }
        }
        else {
            throw new PronosticNotFoundException();
        }
    }
}
