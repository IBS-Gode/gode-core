package org.ibs.cds.gode.entity.query.parse;

import com.github.vincentrussell.query.mongodb.sql.converter.MongoDBQueryHolder;
import com.github.vincentrussell.query.mongodb.sql.converter.ParseException;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.pagination.PageContext;

import java.util.Map;

public class MongoQueryParser<E> implements QueryParser<E, MongoDBQueryHolder> {

    @Override
    public Pair<MongoDBQueryHolder, PageContext> parse(QueryConfig<E> config, Map<String, Class> fieldMetadata) {
        Pair<String, PageContext> sqlQuery = new RawSQLQueryParser().parse(config, fieldMetadata);
        try {
            QueryConverter queryConverter = new QueryConverter.Builder().sqlString(sqlQuery.getKey()).build();
            return Pair.of(queryConverter.getMongoQuery(), sqlQuery.getRight());
        } catch (ParseException e) {
            throw new GodeQueryException(e);
        }
    }

    @Override
    public QueryType getType() {
        return QueryType.MONGODB;
    }
}
