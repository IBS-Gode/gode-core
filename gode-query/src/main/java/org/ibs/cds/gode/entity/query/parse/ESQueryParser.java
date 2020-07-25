package org.ibs.cds.gode.entity.query.parse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.ibs.cds.gode.entity.query.QueryStore;
import org.ibs.cds.gode.entity.query.model.*;
import org.ibs.cds.gode.entity.query.operation.QueryOperationTranslator;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.Sortable;

import java.util.ArrayList;
import java.util.List;

public class ESQueryParser implements QueryParser<SearchRequest> {

    public Pair<SearchRequest,PageContext> parse(QueryConfig queryConfig) {
        Select query = queryConfig.getSelect();
        String indexName = queryConfig.getName();
        List<QueryBuilder> builders = new ArrayList();
        Pair<String, SortOrder> orderByClause = parseOrderByClause(query.getOrder());
        parseWhereClause(query.getWhere(), builders);
        Pair<SearchSourceBuilder, PageContext> sourceBuilder = searchBuilder(builders, queryConfig, orderByClause);
        return Pair.of(new SearchRequest(indexName).source(sourceBuilder.getKey()),sourceBuilder.getValue());
    }

    private Pair<SearchSourceBuilder,PageContext> searchBuilder(List<QueryBuilder> builders, QueryConfig query, Pair<String, SortOrder> orderByClause) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<String> only = query.getSelect().getOnly();
        builders.stream().forEach(k -> boolQueryBuilder.filter(k));
        PageContext pageContext = PageContext.of(query.getPageNo(), query.getPageSize());
        pageContext.addSortOrder(Sortable.by(query.getSelect().getOrder().getIn(), query.getSelect().getOrder().getBy()));
        int from = (pageContext.getPageNumber() -1) * pageContext.getPageSize();
        if (CollectionUtils.isNotEmpty(only)) {
            return Pair.of(new SearchSourceBuilder()
                    .query(boolQueryBuilder)
                    .fetchSource(only.toArray(String[]::new), null)
                    .from(from)
                    .size(pageContext.getPageSize())
                    .trackTotalHits(true)
                    .sort(orderByClause.getLeft(), orderByClause.getRight()), pageContext);
        }
        return Pair.of(new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .from(from)
                .size(pageContext.getPageSize())
                .trackTotalHits(true)
                .sort(orderByClause.getLeft(), orderByClause.getRight()), pageContext);

    }

    private Pair<String, SortOrder> parseOrderByClause(Order order) {
        if (order == null) {
            return Pair.of("createdOn", SortOrder.DESC);
        }
        String by = order.getBy();
        return Pair.of(by == null ? "createdOn" : by, fromSortType(order.getIn()));
    }

    private void parseWhereClause(Where where, List<QueryBuilder> builders) {
        if(where == null) {
            builders.add(QueryBuilders.matchAllQuery());
            return;
        }
        String field = where.getField();
        QueryOperation operation = where.getOperation();
        List<Operand> operands = where.getOperands();
        builders.add((QueryBuilder) QueryOperationTranslator.translate(getType(), field, operation, operands.toArray(Operand[]::new)));
        parseCompose(where.getAnd(), builders);
        parseCompose(where.getOr(), builders);
    }

    private void parseCompose(Compose composed, List<QueryBuilder> builders){
        if (composed != null && CollectionUtils.isNotEmpty(composed.getWhere())) {
            composed.getWhere().stream().forEach(w -> parseWhereClause(w, builders));
        }
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
    public QueryStore getType() {
        return QueryStore.ELASTICSEARCH;
    }
}
