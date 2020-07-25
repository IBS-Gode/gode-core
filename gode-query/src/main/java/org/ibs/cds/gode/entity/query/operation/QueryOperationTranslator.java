package org.ibs.cds.gode.entity.query.operation;

import org.elasticsearch.index.query.QueryBuilder;
import org.ibs.cds.gode.entity.query.QueryStore;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.Operand;
import org.ibs.cds.gode.entity.query.model.QueryOperation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author manugraj
 */
public class QueryOperationTranslator {

    private static final Map<QueryOperation, ESQueryOperation> esOperations = Arrays
            .stream(ESQueryOperation.values())
            .collect(Collectors.toMap(s -> s.getOperation(), s -> s));


    public static QueryBuilder es(String column, QueryOperation op, Operand... args) {
        if (!esOperations.containsKey(op))
            throw new GodeQueryException("No store operations avaiable for the operation:" + op);
        return esOperations.get(op).getOperation(column, args);
    }

    public static Object translate(QueryStore type, String column, QueryOperation op, Operand... args){
        switch (type){
            case ELASTICSEARCH:
                return es(column, op, args);
            default:
                throw new GodeQueryException("No store mapping found for dynamic query");
        }
    }
}
