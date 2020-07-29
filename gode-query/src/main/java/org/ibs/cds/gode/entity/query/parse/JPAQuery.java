package org.ibs.cds.gode.entity.query.parse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.entity.query.QueryType;
import org.ibs.cds.gode.entity.query.model.Compose;
import org.ibs.cds.gode.entity.query.model.Operand;
import org.ibs.cds.gode.entity.query.model.QueryOperation;
import org.ibs.cds.gode.entity.query.model.Where;
import org.ibs.cds.gode.entity.query.operation.QueryOperationTranslator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JPAQuery<T> implements Specification<T> {

    private Where where;
    private List<String> returnFields;
    private Map<String, Class> fieldMetadata;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if(CollectionUtils.isNotEmpty(returnFields)){
            query.multiselect(parseWhat(returnFields, root));
        }
        return parseWhereClause(where, fieldMetadata, root, criteriaBuilder);
    }

    private List<Selection<?>> parseWhat(List<String> returnFields, Root<?> root) {
        return returnFields.stream().map(root::get).collect(Collectors.toList());
    }

    private Predicate parseWhereClause(Where where, Map<String, Class> fieldMetadata, Root<?> root, CriteriaBuilder criteriaBuilder) {
        Function<Root<?>, Function<CriteriaBuilder, Predicate>> queryParseable = translate(where, fieldMetadata);
        Predicate query = queryParseable.apply(root).apply(criteriaBuilder);
        Predicate and = parseCompose(where.getAnd(), "AND", fieldMetadata, root, criteriaBuilder);
        Predicate or = parseCompose(where.getOr(), "OR", fieldMetadata, root, criteriaBuilder);
        if (and == null && or == null)
            return query;
        else if (and == null)
            return criteriaBuilder.and(query, or);
        else if (or == null)
            return criteriaBuilder.or(query, and);
        else
            return criteriaBuilder.or(criteriaBuilder.and(query, and), or);
    }

    private Function<Root<?>, Function<CriteriaBuilder, Predicate>> translate(Where where, Map<String, Class> fieldMetadata) {
        String field = where.getField();
        QueryOperation operation = where.getOperation();
        List<Operand> operands = where.getOperands();
        return (Function<Root<?>, Function<CriteriaBuilder, Predicate>>) QueryOperationTranslator.translate(fieldMetadata, QueryType.JPA, field, operation, operands.toArray(Operand[]::new));
    }

    private Predicate parseCompose(Compose composed, String type, Map<String, Class> fieldMetadata, Root<?> root, CriteriaBuilder criteriaBuilder) {
        if (composed != null && CollectionUtils.isNotEmpty(composed.getWhere())) {
            Predicate[] restrictions = composed.getWhere().stream().map(where -> parseWhereClause(where, fieldMetadata, root, criteriaBuilder)).toArray(Predicate[]::new);
            if (type.equals("AND")) {
                return criteriaBuilder.and(restrictions);
            }
            return criteriaBuilder.or(restrictions);
        }
        return null;
    }
}
