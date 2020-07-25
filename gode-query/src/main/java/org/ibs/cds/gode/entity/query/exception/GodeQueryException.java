package org.ibs.cds.gode.entity.query.exception;

public class GodeQueryException extends RuntimeException {
    public GodeQueryException() {
        super();
    }

    public GodeQueryException(String message) {
        super(message);
    }

    public GodeQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public GodeQueryException(Throwable cause) {
        super(cause);
    }

    protected GodeQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
