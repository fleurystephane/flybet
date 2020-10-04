package com.sfl.flybet.casestudy.domain.exceptions;

public class EndDateProjectException extends Exception {
    public EndDateProjectException() {
    }

    public EndDateProjectException(String message) {
        super(message);
    }

    public EndDateProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public EndDateProjectException(Throwable cause) {
        super(cause);
    }

    public EndDateProjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
