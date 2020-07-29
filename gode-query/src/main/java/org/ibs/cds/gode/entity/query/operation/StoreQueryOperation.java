package org.ibs.cds.gode.entity.query.operation;

import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.Operand;

/**
 *
 * @author manugraj
 */
public interface StoreQueryOperation<T> {
    
     T getOperation(String column, Operand... args);
     QueryType store();
     int getArgCount();

     default T operation(String column, Operand... args){
          if (args == null || args.length < getArgCount()) {
               throw new GodeQueryException("Not enough arguments are available for the query");
          }
         return getOperation(column, args);
     }

}
