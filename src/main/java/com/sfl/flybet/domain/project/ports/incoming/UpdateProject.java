package com.sfl.flybet.domain.project.ports.incoming;

import com.sfl.flybet.domain.authentication.exceptions.AuthorizationException;
import com.sfl.flybet.domain.project.exceptions.ProjectAlreadyStartedException;
import com.sfl.flybet.domain.project.exceptions.ProjectNotFoundException;
import com.sfl.flybet.domain.project.model.Project;
import com.sfl.flybet.domain.project.model.ProjectIdentifier;

public interface UpdateProject {
    ProjectIdentifier update(Project project) throws ProjectAlreadyStartedException, ProjectNotFoundException, AuthorizationException;
}
