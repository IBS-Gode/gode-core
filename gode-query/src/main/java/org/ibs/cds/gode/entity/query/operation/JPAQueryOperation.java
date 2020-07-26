package org.ibs.cds.gode.entity.query.operation;

import lombok.Getter;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.model.Operand;
import org.ibs.cds.gode.entity.query.model.QueryOperation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.Function;

public enum JPAQueryOperation implements StoreQueryOperation<Function<Root<?>, Function<CriteriaBuilder, Predicate>>> {

    equal(1,
            (col, operands) -> {
                Operand operand = operands[0];
                if (operand.isAttribute()) {
                    return root -> criteria -> criteria.equal(root.get(col), root.get(operand.getValue()));
                }
                return root -> criteria -> criteria.equal(root.get(col), operand.getValue());
            }
            , QueryOperation.eq),
    notEqual(1,
            (col, operands) -> {
                Operand operand = operands[0];
                if (operand.isAttribute()) {
                    return root -> criteria -> criteria.notEqual(root.get(col), root.get(operand.getValue()));
                }
                return root -> criteria -> criteria.notEqual(root.get(col), operand.getValue());
            }
            , QueryOperation.neq),
    like(1,
            (col, operands) -> {
                Operand operand = operands[0];
                if (operand.isAttribute()) {
                    return root -> criteria -> criteria.like(root.get(col), root.get(operand.getValue()));
                }
                return root -> criteria -> criteria.like(root.get(col), operand.getValue());
            }
            , QueryOperation.like),
    gt(1,
            (col, operands) -> {
                Operand operand = operands[0];
                if (operand.isAttribute()) {
                    return root -> criteria -> criteria.greaterThan(root.get(col), root.get(operand.getValue()));
                }
                return root -> criteria -> criteria.greaterThan(root.get(col), operand.getValue());
            }
            , QueryOperation.gt),
    lt(1,
            (col, operands) -> {
                Operand operand = operands[0];
                if (operand.isAttribute()) {
                    return root -> criteria -> criteria.lessThan(root.get(col), root.get(operand.getValue()));
                }
                return root -> criteria -> criteria.lessThan(root.get(col), operand.getValue());
            }
            , QueryOperation.lt),
    gte(1,
            (col, operands) -> {
                Operand operand = operands[0];
                if (operand.isAttribute()) {
                    return root -> criteria -> criteria.greaterThanOrEqualTo(root.get(col), root.get(operand.getValue()));
                }
                return root -> criteria -> criteria.greaterThanOrEqualTo(root.get(col), operand.getValue());
            }
            , QueryOperation.gte),
    lte(1,
            (col, operands) -> {
                Operand operand = operands[0];
                if (operand.isAttribute()) {
                    return root -> criteria -> criteria.lessThanOrEqualTo(root.get(col), root.get(operand.getValue()));
                }
                return root -> criteria -> criteria.lessThanOrEqualTo(root.get(col), operand.getValue());
            }
            , QueryOperation.lte),
    btw(2,
            (col, operands) -> {
                Operand operand1 = operands[0];
                Operand operand2 = operands[1];
                if (operand1.isAttribute() && operand2.isAttribute()) {
                    return root -> criteria -> criteria.between(root.get(col), root.get(operand1.getValue()), root.get(operand2.getValue()));
                }
                return root -> criteria -> criteria.between(root.get(col), operand1.getValue(), operand2.getValue());
            }
            , QueryOperation.between);

    private CriteriaBuilder config;
    private int argCount;
    private final JPAQueryResolver builder;
    @Getter
    private final QueryOperation operation;

    JPAQueryOperation(int argCount, JPAQueryResolver builder, QueryOperation operation) {
        this.argCount = argCount;
        this.builder = builder;
        this.operation = operation;
    }

    @Override
    public Function<Root<?>, Function<CriteriaBuilder, Predicate>> getOperation(String column, Operand... args) {
        return builder.from(column, args);
    }

    @Override
    public QueryType store() {
        return null;
    }
}
