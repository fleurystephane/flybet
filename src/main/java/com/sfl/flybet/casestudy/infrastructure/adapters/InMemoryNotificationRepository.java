package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.PronosticEvent;
import com.sfl.flybet.casestudy.infrastructure.ports.NotificationRepository;

import java.util.LinkedHashSet;
import java.util.Set;

public class InMemoryNotificationRepository implements NotificationRepository {
    Set<PronosticEvent> events = new LinkedHashSet<>();
    @Override
    public void add(PronosticEvent pronosticEvent) {
        events.add(pronosticEvent);
    }

    @Override
    public Set<PronosticEvent> all() {
        return events;
    }
}
