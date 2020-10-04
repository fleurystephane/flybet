package com.sfl.flybet.casestudy.infrastructure.ports;

import com.sfl.flybet.casestudy.domain.Pronostic;

import java.util.Optional;

public interface PronosticRepository {

    Optional<Pronostic> byId(String pronoId);
}
