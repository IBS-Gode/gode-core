package org.ibs.cds.gode.entity.query.operation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.elasticsearch.index.query.QueryBuilder;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.Operand;
import org.ibs.cds.gode.entity.query.model.QueryOperation;
import org.ibs.cds.gode.entity.query.parse.QueryParser;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author manugraj
 */
public class QueryOperationTranslator {

    private static final Map<QueryOperation, ESQueryOperation> esOperations = Arrays
            .stream(ESQueryOperation.values())
            .collect(Collectors.toMap(s -> s.getOperation(), s -> s));

    private static final Table<QueryOperation, Class<?>, SQLQueryOperation> sqlOperations;

    private static final Map<QueryOperation, JPAQueryOperation> jpaOperations = Arrays
            .stream(JPAQueryOperation.values())
            .collect(Collectors.toMap(s -> s.getOperation(), s -> s));

    static {
        sqlOperations = HashBasedTable.create();
        Arrays
                .stream(SQLQueryOperation.values())
                .forEach(k -> k.getType().stream().forEach(type -> sqlOperations.put(k.getOperation(), type, k)));
    }


    public static QueryBuilder es(String column, QueryOperation op, Operand... args) {
        if (!esOperations.containsKey(op))
            throw new GodeQueryException("No store operations avaiable for the operation:" + op);
        return esOperations.get(op).getOperation(column, args);
    }

    public static String mysql(Map<String, Class> fieldMetadata, String column, QueryOperation op, Operand... args) {
        Class classType = fieldMetadata.get(column);
        if (!sqlOperations.contains(op, classType))
            throw new GodeQueryException("No store operations avaiable for the operation:" + op);
        return sqlOperations.get(op, classType).getOperation(column, args);
    }

    public static Function<Root<?>, Function<CriteriaBuilder, Predicate>> jpa(String column, QueryOperation op, Operand... args) {
        if (!jpaOperations.containsKey(op))
            throw new GodeQueryException("No store operations avaiable for the operation:" + op);
        return jpaOperations.get(op).getOperation(column, args);
    }

    public static Object translate(Map<String, Class> fieldMetadata, QueryType type, String column, QueryOperation op, Operand... args) {
        QueryParser.validate(fieldMetadata, column);
        switch (type) {
            case ELASTICSEARCH:
                return es(column, op, args);
            case JPA:
                return jpa(column, op, args);
            case MYSQL:
                return mysql(fieldMetadata, column, op, args);
            default:
                throw new GodeQueryException("No store mapping found for dynamic query");
        }
    }
}
