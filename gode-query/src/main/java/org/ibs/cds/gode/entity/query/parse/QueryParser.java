package org.ibs.cds.gode.entity.query.parse;

import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.pagination.PageContext;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public interface QueryParser<E,T> {

    Pair<T, PageContext> parse(QueryConfig<E> config, Map<String,Class> fieldMetadata);
    QueryType getType();

    default Pair<T, PageContext> doParse(QueryConfig<E> config){
        Map<String,Class> fieldMetadata = Arrays.stream(config.getType().getDeclaredFields()).collect(Collectors.toMap(s->s.getName(), s->s.getType()));
        return parse(config, fieldMetadata);
    }

    static void validate(Map<String,Class> fieldMetadata, String field){
        if(!fieldMetadata.containsKey(field)){
            throw new GodeQueryException("No such field in entity");
        }
    }
}
