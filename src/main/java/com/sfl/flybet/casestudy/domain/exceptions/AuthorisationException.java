package com.sfl.flybet.casestudy.domain.exceptions;

public class AuthorisationException extends Throwable {
    public AuthorisationException(String s) {
        super(s);
    }

    public AuthorisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorisationException(Throwable cause) {
        super(cause);
    }

    public AuthorisationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AuthorisationException() {
    }
}
