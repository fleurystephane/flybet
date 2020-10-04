package com.sfl.flybet.casestudy.domain.exceptions;

public class AlreadyDisapprovedException extends Exception {
    public AlreadyDisapprovedException() {
    }

    public AlreadyDisapprovedException(String message) {
        super(message);
    }

    public AlreadyDisapprovedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyDisapprovedException(Throwable cause) {
        super(cause);
    }

    public AlreadyDisapprovedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
