package unit.pronostic.adapter;

import com.sfl.flybet.domain.pronostic.model.PronosticEvent;
import com.sfl.flybet.domain.pronostic.ports.outgoing.PronosticNotificationDatabase;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryNotificationRepositoryAdapter implements PronosticNotificationDatabase {
    ConcurrentHashMap<Long, Set<PronosticEvent>> events = new ConcurrentHashMap<>();

    @Override
    public void add(PronosticEvent pronosticEvent) {
        Set<PronosticEvent> pronosEvents = events.get(pronosticEvent.getPronostic().getId());
        if(null == pronosEvents){
            pronosEvents = new HashSet<>();
        }
        pronosEvents.add(pronosticEvent);
        events.put(pronosticEvent.getPronostic().getId(), pronosEvents);
    }

    @Override
    public Set<PronosticEvent> all(Long pronoId) {
        return events.get(pronoId);
    }
}
