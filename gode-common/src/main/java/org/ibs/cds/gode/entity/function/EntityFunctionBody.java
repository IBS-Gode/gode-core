package org.ibs.cds.gode.entity.function;

import org.ibs.cds.gode.entity.generic.DataMap;
import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.exception.KnownException;

/**
 *
 * @author manugraj
 * @param <V>
 */
public interface EntityFunctionBody<V extends EntityView<?>> extends EntityViewValidation<V>{

    DataMap process(V view);
    
    @Override
    ValidationStatus validate(V view);
    
    
    default DataMap run(V view){
         ValidationStatus status = validate(view);
        if(!status.getStatus().isSuccess()){
            throw KnownException.ENTITY_VALIDATIONS_FAILED.provide(status.getErrors());
        }
       return process(view);
    }
}
