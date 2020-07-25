package org.ibs.cds.gode.entity.query.operation;

import org.ibs.cds.gode.entity.query.QueryStore;
import org.ibs.cds.gode.entity.query.model.Operand;

/**
 *
 * @author manugraj
 */
public interface StoreQueryOperation<T> {
    
     T getOperation(String column, Operand... args);
     QueryStore store();
}
