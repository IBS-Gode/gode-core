package org.ibs.cds.gode.exception;

/**
 *
 * @author manugraj
 */
public class InvalidConfigurationException extends GodeRuntimeException{

    public InvalidConfigurationException(Error error) {
        super(error);
    }

    public InvalidConfigurationException(Error error, String message) {
        super(error, message);
    }

}
