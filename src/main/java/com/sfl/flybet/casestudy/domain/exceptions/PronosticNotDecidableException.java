package com.sfl.flybet.casestudy.domain.exceptions;

public class PronosticNotDecidableException extends Throwable {
    public PronosticNotDecidableException() {
    }

    public PronosticNotDecidableException(String message) {
        super(message);
    }

    public PronosticNotDecidableException(String message, Throwable cause) {
        super(message, cause);
    }

    public PronosticNotDecidableException(Throwable cause) {
        super(cause);
    }

    public PronosticNotDecidableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
