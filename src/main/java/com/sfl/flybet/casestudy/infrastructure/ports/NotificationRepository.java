package com.sfl.flybet.casestudy.infrastructure.ports;

import com.sfl.flybet.casestudy.domain.PronosticEvent;

import java.util.Set;

public interface NotificationRepository {
    void add(PronosticEvent pronosticEvent);

    Set<PronosticEvent> all();
}
