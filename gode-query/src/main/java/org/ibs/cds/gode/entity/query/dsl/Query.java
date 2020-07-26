package org.ibs.cds.gode.entity.query.dsl;

import java.util.List;

public interface Query {

    static WhereClause select(String... fields){
        return new WhereClause(List.of(fields));
    }

}
