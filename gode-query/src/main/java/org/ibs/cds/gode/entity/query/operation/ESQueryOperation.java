package org.ibs.cds.gode.entity.query.operation;

import lombok.Getter;
import org.elasticsearch.index.query.QueryBuilder;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.Operand;
import org.ibs.cds.gode.entity.query.model.QueryOperation;

import java.util.Arrays;
import java.util.function.BiFunction;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 *
 * @author manugraj
 */
public enum ESQueryOperation implements StoreQueryOperation<QueryBuilder> {

    equals(1, (column, args) -> termQuery(column, args[0].getValue()), QueryOperation.eq, Object.class),
    notEquals(1, (column, args) -> boolQuery().mustNot(matchQuery(column, args[0].getValue())), QueryOperation.neq, Object.class),
    like(1, (column, args) -> matchPhraseQuery(column, args[0].getValue()), QueryOperation.like, String.class),
    gt(1, (column, args) -> rangeQuery(column).gt(args[0].getValue()), QueryOperation.gt, Number.class),
    lt(1, (column, args) -> rangeQuery(column).lt(args[0].getValue()), QueryOperation.lt, Number.class),
    in(-1,(column, args) -> termsQuery(column, Arrays.stream(args).map(Operand::getValue).toArray()),QueryOperation.in ,Object.class),
    gte(1, (column, args) -> rangeQuery(column).gte(args[0].getValue()), QueryOperation.gte, Number.class),
    lte(1, (column, args) -> rangeQuery(column).lte(args[0].getValue()), QueryOperation.lte, Number.class),
    btw(2, (column, args) -> rangeQuery(column).gte(args[0].getValue()).lte(args[1].getValue()), QueryOperation.between, Number.class);

    private int argCount;
    private final BiFunction<String, Operand[], QueryBuilder> builder;
    @Getter
    private final QueryOperation operation;
    private final Class type;

    ESQueryOperation(int argCount, BiFunction<String, Operand[], QueryBuilder> builder, QueryOperation operation, Class type) {
        this.argCount = argCount;
        this.builder = builder;
        this.operation = operation;
        this.type = type;
    }

    @Override
    public QueryBuilder getOperation(String column, Operand... args) {
        if (args == null || args.length < this.argCount) {
            throw new GodeQueryException("Not enough arguments are available for the query");
        }
        return this.builder.apply(column, args);
    }

    @Override
    public QueryType store() {
        return QueryType.ELASTICSEARCH;
    }

}
