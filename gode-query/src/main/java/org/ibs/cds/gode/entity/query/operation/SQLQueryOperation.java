package org.ibs.cds.gode.entity.query.operation;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.ibs.cds.gode.entity.query.QueryStore;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.Operand;
import org.ibs.cds.gode.entity.query.model.QueryOperation;

/**
 *
 * @author manugraj
 */
public enum SQLQueryOperation implements StoreQueryOperation<String> {

//    stringEquals(1, "$column = \'%s\'", QueryOperation.eq, String.class),
//    stringNotEquals(1, "$column =  \'%s\'", QueryOperation.eq, String.class),
//    like(1, "$column like %s", QueryOperation.eq, Number.class),
//    equals(1,"$column = %s", QueryOperation.eq, Number.class),
//    notEquals(1, "$column != %s", QueryOperation.eq, Number.class),
//    gt(1, "$column > %s", QueryOperation.eq, Number.class),
//    lt(1, "$column < %s", QueryOperation.eq, Number.class),
//    gte(1, "$column >= %s", QueryOperation.eq, Number.class),
//    lte(1, "$column <= %s", QueryOperation.eq, Number.class),
//    btw(2, "$column <= %s AND $column >= %s", QueryOperation.eq, Number.class)
    
    ;
    
    private int argCount;
    private final String format;
    private final @Getter QueryOperation operation;
    private final Class type;

    SQLQueryOperation(int argCount, String format, QueryOperation operation, Class type) {
        this.argCount = argCount;
        this.format = format;
        this.operation = operation;
        this.type = type;
    }

    @Override
    public String getOperation(String column, Operand... args) {
        if(args == null || args.length < this.argCount) throw new GodeQueryException("Not enough arguments are available for the query");
        return String.format(StringUtils.replace(this.format, "$column",  column), args);
    }

    @Override
    public QueryStore store() {
        return QueryStore.JPA;
    }

   
}
