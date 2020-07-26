package org.ibs.cds.gode.entity.query.exception;

import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;

public class GodeQueryResultProcessException extends JsonProcessingException {
    public GodeQueryResultProcessException(Throwable cause) {
        super(cause);
    }
}