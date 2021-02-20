package com.sfl.flybet.domain.project.ports.incoming;

import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.project.exceptions.EndDateProjectException;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyExistsException;
import com.sfl.flybet.domain.project.exceptions.SoldeInsuffisantException;
import com.sfl.flybet.domain.project.model.CreateProjectCommand;
import com.sfl.flybet.domain.project.model.ProjectIdentifier;

public interface CreateProject {
    ProjectIdentifier create(CreateProjectCommand command)
            throws ProjectAlreadyExistsException, SoldeInsuffisantException, EndDateProjectException, AuthorizationException;
}
