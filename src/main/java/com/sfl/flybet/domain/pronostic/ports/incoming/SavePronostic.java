package com.sfl.flybet.domain.pronostic.ports.incoming;

import com.sfl.flybet.domain.pronostic.model.Pronostic;

public interface SavePronostic {
    void save(Pronostic pronostic, Long projectId);
}
