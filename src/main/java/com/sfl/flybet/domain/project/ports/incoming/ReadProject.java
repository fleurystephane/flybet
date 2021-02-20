package com.sfl.flybet.domain.project.ports.incoming;

import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.customer.model.Customer;
import com.sfl.flybet.domain.project.model.Project;

import java.util.Set;

public interface ReadProject {
    Set<Project> getAllProjectsOf(Customer owner) throws AuthorizationException;
}
