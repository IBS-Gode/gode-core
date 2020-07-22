package org.ibs.cds.gode.entity.function;

import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.entity.view.EntityView;

/**
 *
 * @author manugraj
 * @param <V>
 */
public interface EntityViewValidation<V extends EntityView<?>> {
    
    ValidationStatus validate(V view);

}
