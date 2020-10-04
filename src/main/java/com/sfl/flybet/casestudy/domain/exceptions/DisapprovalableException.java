package com.sfl.flybet.casestudy.domain.exceptions;

public class DisapprovalableException extends Throwable {
    public DisapprovalableException() {
    }

    public DisapprovalableException(String message) {
        super(message);
    }

    public DisapprovalableException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisapprovalableException(Throwable cause) {
        super(cause);
    }

    public DisapprovalableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
