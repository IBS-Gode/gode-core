package org.ibs.cds.gode.entity.query.dsl;

import java.util.List;

public class SelectBuilder {

    private List<String> fields;

    public SelectBuilder(String... fields){
        this.fields = List.of(fields);
    }

    public WhereClause where(){
        return new WhereClause(this.fields);
    }
}
