package com.sfl.flybet.casestudy.infrastructure.adapters;

import com.sfl.flybet.casestudy.domain.Pronostic;
import com.sfl.flybet.casestudy.infrastructure.ports.PronosticRepository;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class InMemoryPronosticRepository implements PronosticRepository {
    Set<Pronostic> pronostics = new LinkedHashSet<>();

    @Override
    public Optional<Pronostic> byId(String pronoId) {
        return pronostics.stream().filter(pronostic -> pronostic.getId().equals(pronoId)).findFirst();
    }
}
