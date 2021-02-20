package com.sfl.flybet.domain.project.exceptions;

public class ProjectAlreadyStartedException extends Throwable {
    public ProjectAlreadyStartedException() {
    }

    public ProjectAlreadyStartedException(String message) {
        super(message);
    }

    public ProjectAlreadyStartedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectAlreadyStartedException(Throwable cause) {
        super(cause);
    }

    public ProjectAlreadyStartedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}