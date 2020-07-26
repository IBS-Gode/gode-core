package org.ibs.cds.gode.entity.query.operation;

import org.ibs.cds.gode.entity.query.model.Operand;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.Function;

@FunctionalInterface
public interface JPAQueryResolver {
    Function<Root<?>, Function<CriteriaBuilder, Predicate>> from(String field, Operand[] operands);
}
