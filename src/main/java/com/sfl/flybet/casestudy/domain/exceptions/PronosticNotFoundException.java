package com.sfl.flybet.casestudy.domain.exceptions;

public class PronosticNotFoundException extends Exception {
    public PronosticNotFoundException() {
    }

    public PronosticNotFoundException(String message) {
        super(message);
    }

    public PronosticNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PronosticNotFoundException(Throwable cause) {
        super(cause);
    }

    public PronosticNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
