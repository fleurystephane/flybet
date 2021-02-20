package com.sfl.flybet.domain.authentication.exceptions;

public class AuthorizationException extends Throwable {
    public AuthorizationException(String s) {
        super(s);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }

    public AuthorizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AuthorizationException() {
    }
}
