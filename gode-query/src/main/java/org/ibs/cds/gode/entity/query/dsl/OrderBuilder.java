package org.ibs.cds.gode.entity.query.dsl;

import org.ibs.cds.gode.pagination.Sortable;

public class OrderBuilder {

    private WhereClause predicate;
    private String field;

    public OrderBuilder(WhereClause predicate, String field){
        this.predicate = predicate;
        this.field = field;
    }

    protected OrderBuilder(WhereClause predicate){
        this.predicate = predicate;
        this.field = "createdOn";
    }

    public QueryConfigBuilder asc(){
        return new QueryConfigBuilder(predicate.getReturnFields(), predicate.getWhere(), this.field, Sortable.Type.ASC);
    };

    public QueryConfigBuilder desc(){
        return new QueryConfigBuilder(predicate.getReturnFields(), predicate.getWhere(), this.field, Sortable.Type.DESC);
    };
}
