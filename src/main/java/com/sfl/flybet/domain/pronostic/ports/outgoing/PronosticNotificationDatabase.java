package com.sfl.flybet.domain.pronostic.ports.outgoing;


import com.sfl.flybet.domain.pronostic.model.PronosticEvent;

import java.util.Set;

public interface PronosticNotificationDatabase {
    void add(PronosticEvent pronosticEvent);
    Set<PronosticEvent> all(Long aLong);
}
