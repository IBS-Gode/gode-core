package org.ibs.cds.gode.entity.query.parse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.search.sort.SortOrder;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.model.*;
import org.ibs.cds.gode.entity.query.operation.QueryOperationTranslator;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.Sortable;

import java.util.*;
import java.util.stream.Collectors;

public class RawSQLQueryParser<E> implements QueryParser<E,String> {
    private static final String TEMPLATE = "SELECT %s FROM %s WHERE %s ORDER BY %s %s LIMIT %d";

    public Pair<String,PageContext> parse(QueryConfig<E> queryConfig, Map<String,Class> fieldMetadata) {
        Select query = queryConfig.getSelect();
        String tableName = queryConfig.getName();
        Pair<String, String> orderByClause = parseOrderByClause(query.getOrder(), fieldMetadata);
        String whereClause = parseWhereClause(query.getWhere(), fieldMetadata);
        String fields = CollectionUtils.isNotEmpty(query.getOnly()) ? query.getOnly().stream().collect(Collectors.joining(",")) : "*" ;
        return parse0(queryConfig, tableName, fields, whereClause,  orderByClause);
    }

    private Pair<String, PageContext> parse0(QueryConfig queryConfig, String tableName, String fields, String where, Pair<String, String> orderByClause){
        PageContext context = PageContext.of(queryConfig.getPageNo(), queryConfig.getPageSize());
        return Pair.of(String.format(TEMPLATE, fields, tableName, where, orderByClause.getLeft(), orderByClause.getRight(), context.getPageNumber() * context.getPageSize()), context);
    }

    private Pair<String, String> parseOrderByClause(Order order, Map<String, Class> fieldMetadata) {
        if (order == null) {
            return Pair.of("createdOn", "DESC");
        }
        String by = order.getBy() == null ?  "createdOn" : order.getBy();
        QueryParser.validate(fieldMetadata, by);
        Sortable.Type in = order.getIn();
        return Pair.of(by, in == null ? "DESC": in.toString());
    }

    private String parseWhereClause(Where where, Map<String, Class> fieldMetadata) {
        String query = translate(where, fieldMetadata);
        String and = parseCompose(where.getAnd(), "AND", fieldMetadata);
        String or = parseCompose(where.getOr(), "OR", fieldMetadata);
        if(and == null && or == null)
            return query;
        else if(and == null)
            return query.concat(" OR ( ").concat(or).concat(" ) ");
        else if(or == null)
            return query.concat(" AND (").concat(and).concat(")");
        else
            return query.concat(" AND (").concat(and).concat(")").concat(" OR (").concat(or).concat(")");
    }

    private String translate(Where where, Map<String, Class> fieldMetadata) {
        String field = where.getField();
        QueryOperation operation = where.getOperation();
        List<Operand> operands = where.getOperands();
        return (String) QueryOperationTranslator.translate(fieldMetadata, getType(), field, operation, operands.toArray(Operand[]::new));
    }

    private String parseCompose(Compose composed, String type, Map<String, Class> fieldMetadata){
        if (composed != null && CollectionUtils.isNotEmpty(composed.getWhere())) {
            return composed.getWhere().stream().map(where -> parseWhereClause(where, fieldMetadata)).collect(Collectors.joining(" "+type+" "));
        }
        return null;
    }

    private SortOrder fromSortType(Sortable.Type type) {
        switch (type) {
            case ASC:
            default:
                return SortOrder.ASC;
            case DESC:
                return SortOrder.DESC;
        }
    }

    @Override
    public QueryType getType() {
            return QueryType.RAW_SQL;
    }
}
