package org.ibs.cds.gode.entity.query.dsl;

import lombok.AccessLevel;
import lombok.Getter;
import org.ibs.cds.gode.entity.query.model.*;

import java.util.List;

public class WhereClause {

    @Getter(AccessLevel.PROTECTED)
    private List<String> returnFields;
    @Getter(AccessLevel.PROTECTED)
    private Where where;

    public WhereClause(List<String> returnFields){
        this.returnFields = returnFields;
    }

    public WhereClause where(String field, QueryOperation operation, Operand... operands){
        where = new Where();
        where.setField(field);
        where.setOperation(operation);
        where.setOperands(List.of(operands));
        return this;
    }

    public static Where of(String field, QueryOperation operation, Operand... operands){
        Where where = new Where();
        where.setField(field);
        where.setOperation(operation);
        where.setOperands(List.of(operands));
        return where;
    }

    public WhereClause and(Where... wheres){
        Compose and = new Compose();
        and.setWhere(List.of(wheres));
        where.setAnd(and);
        return this;
    }

    public WhereClause or(Where... wheres){
        Compose or = new Compose();
        or.setWhere(List.of(wheres));
        where.setAnd(or);
        return this;
    }

    public OrderBuilder orderBy(String field){
        return new OrderBuilder(this, field);
    }

    public QueryConfig<?> query(Class<? extends QueryConfig> entityClass){
        return new OrderBuilder(this).desc().query(entityClass);
    }
}
