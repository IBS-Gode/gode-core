package org.ibs.cds.gode.exception;

public class AppValidationFailedException extends AppOperationFailedException {

    public AppValidationFailedException(Error error) {
        super(error);
    }

    public AppValidationFailedException(Error error, String message) {
        super(error, message);
    }

    public AppValidationFailedException(Error error, String message, Throwable cause) {
        super(error, message, cause);
    }

    public AppValidationFailedException(Error error, Throwable cause) {
        super(error, cause);
    }
}
