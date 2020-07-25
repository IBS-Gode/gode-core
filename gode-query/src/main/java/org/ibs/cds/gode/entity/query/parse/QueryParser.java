package org.ibs.cds.gode.entity.query.parse;

import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.query.QueryStore;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.pagination.PageContext;

public interface QueryParser<T> {

    Pair<T, PageContext> parse(QueryConfig<?> config);
    QueryStore getType();

    static QueryParser of(QueryStore store){
        switch (store){
            case ELASTICSEARCH:
                return new ESQueryParser();
            default:
                return null;
        }
    }
}
