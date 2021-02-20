package com.sfl.flybet.domain.project.exceptions;

public class SoldeInsuffisantException extends Exception {
    public SoldeInsuffisantException() {
    }

    public SoldeInsuffisantException(String message) {
        super(message);
    }

    public SoldeInsuffisantException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoldeInsuffisantException(Throwable cause) {
        super(cause);
    }

    public SoldeInsuffisantException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
