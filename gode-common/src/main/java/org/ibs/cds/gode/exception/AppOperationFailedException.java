package org.ibs.cds.gode.exception;

public class AppOperationFailedException extends GodeException {

    public AppOperationFailedException(Error error) {
        super(error);
    }

    public AppOperationFailedException(Error error, String message) {
        super(error, message);
    }

    public AppOperationFailedException(Error error, String message, Throwable cause) {
        super(error, message, cause);
    }

    public AppOperationFailedException(Error error, Throwable cause) {
        super(error, cause);
    }
}
