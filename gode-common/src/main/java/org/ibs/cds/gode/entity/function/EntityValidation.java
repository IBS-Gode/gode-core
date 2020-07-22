package org.ibs.cds.gode.entity.function;

import org.ibs.cds.gode.entity.type.StateEntity;
import org.ibs.cds.gode.entity.validation.ValidationStatus;

/**
 *
 * @author manugraj
 * @param <E>
 */
public interface EntityValidation<E extends StateEntity<?>> {
    
    ValidationStatus validateEntity(E view);

}