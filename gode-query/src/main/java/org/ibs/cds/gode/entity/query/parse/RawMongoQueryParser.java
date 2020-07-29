package org.ibs.cds.gode.entity.query.parse;

import com.github.vincentrussell.query.mongodb.sql.converter.ParseException;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.util.PrimitivePageUtils;
import org.springframework.data.mongodb.core.query.BasicQuery;

import java.util.List;
import java.util.Map;

public class RawMongoQueryParser<E> implements QueryParser<E, BasicQuery> {

    @Override
    public Pair<BasicQuery, PageContext> parse(QueryConfig<E> config, Map<String, Class> fieldMetadata) {
        Pair<String, PageContext> sqlQuery = new RawSQLQueryParser().parse(config, fieldMetadata);
        try {
            QueryConverter queryConverter = new QueryConverter.Builder().sqlString(sqlQuery.getKey()).build();
            BasicQuery query = new BasicQuery(queryConverter.getMongoQuery().getQuery().toJson());
            PageContext context = sqlQuery.getRight();
            query.with(PrimitivePageUtils.toBaseRequest(context));
            List<String> only = config.getSelect().getOnly();
            parseWhatClause(query, only);
            return Pair.of(query, context);
        } catch (ParseException e) {
            throw new GodeQueryException(e);
        }
    }

    private void parseWhatClause(BasicQuery query, List<String> only) {
        if(CollectionUtils.isNotEmpty(only)){
            only.forEach(query.fields()::include);
        }
    }

    @Override
    public QueryType getType() {
        return QueryType.RAW_MONGODB;
    }
}
