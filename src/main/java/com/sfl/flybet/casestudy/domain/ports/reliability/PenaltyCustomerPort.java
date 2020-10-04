package com.sfl.flybet.casestudy.domain.ports.reliability;

import com.sfl.flybet.casestudy.domain.Customer;
import com.sfl.flybet.casestudy.domain.Pronostic;

public interface PenaltyCustomerPort {
    void penaliseTipster(Customer tipster, Pronostic pronostic);
}
