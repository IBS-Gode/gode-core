package org.ibs.cds.gode.entity.query.operation;

import lombok.Getter;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.exception.GodeQueryException;
import org.ibs.cds.gode.entity.query.model.Operand;
import org.ibs.cds.gode.entity.query.model.QueryOperation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.BiFunction;

/**
 *
 * @author manugraj
 */
public enum SQLQueryOperation implements StoreQueryOperation<String> {

    stringEquals(1, (col, operands)->stringFormatter(col, "=", operands[0]), QueryOperation.eq, String.class),
    stringNotEquals(1, (col, operands)->stringFormatter(col, "!=", operands[0]), QueryOperation.neq, String.class),
    like(1, (col, operands)->stringFormatter(col, "like", operands[0]), QueryOperation.like, String.class),
    equals(1,(col, operands)->formatter(col, "=", operands[0]), QueryOperation.eq, Long.class, Double.class, BigInteger.class, BigDecimal.class),
    notEquals(1, (col, operands)->formatter(col, "!=", operands[0]), QueryOperation.neq, Long.class, Double.class, BigInteger.class, BigDecimal.class),
    gt(1, (col, operands)->formatter(col, ">", operands[0]), QueryOperation.gt, Long.class, Double.class, BigInteger.class, BigDecimal.class),
    lt(1, (col, operands)->formatter(col, "<", operands[0]), QueryOperation.lt, Long.class, Double.class, BigInteger.class, BigDecimal.class),
    gte(1, (col, operands)->formatter(col, ">=", operands[0]), QueryOperation.gte, Long.class, Double.class, BigInteger.class, BigDecimal.class),
    lte(1, (col, operands)->formatter(col, "<=", operands[0]), QueryOperation.lte, Long.class, Double.class, BigInteger.class, BigDecimal.class),
    btw(2, (col, operands)->formatter(col, ">=", operands[0]).concat(" AND ").concat(formatter(col, "<=", operands[1])), QueryOperation.between, Long.class, Double.class, BigInteger.class, BigDecimal.class)
    ;

    @Getter
    private int argCount;
    private final BiFunction<String, Operand[], String> format;
    private final @Getter QueryOperation operation;
    @Getter
    private final List<Class> type;

    SQLQueryOperation(int argCount, BiFunction<String, Operand[], String> format, QueryOperation operation, Class... type) {
        this.argCount = argCount;
        this.format = format;
        this.operation = operation;
        this.type = List.of(type);
    }

    @Override
    public String getOperation(String column, Operand... args) {
        return this.format.apply(column, args);
    }

    @Override
    public QueryType store() {
        return QueryType.MYSQL;
    }

    private static String stringFormatter(String column, String operator, Operand operand){
        if(operand.isAttribute()){
            return formatter(column, operator, operand);
        }
        return column.concat(" ").concat(operator).concat(" '").concat(operand.getValue()).concat("'");
    }

    private static String formatter(String column, String operator, Operand operand){
        return column.concat(" ").concat(operator).concat(" ").concat(operand.getValue());
    }
}
