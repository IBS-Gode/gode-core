package org.ibs.cds.gode.app.function;

import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.exception.KnownException;

public abstract class AppFunctionBody<Request, Manager, Response>  {

    public abstract Response process(Request request, Manager manager);
    public abstract ValidationStatus validate(Request request);
    
    public Response run (Request request, Manager manager) {
        ValidationStatus status = validate(request);
        if(!status.getStatus().isSuccess()){
            throw KnownException.APP_VALIDATIONS_FAILED.provide(status.getErrors());
        }
       return process(request, manager);
    }

}
