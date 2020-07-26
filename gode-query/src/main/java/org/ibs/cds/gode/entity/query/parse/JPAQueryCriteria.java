package org.ibs.cds.gode.entity.query.parse;

import lombok.Data;
import org.ibs.cds.gode.entity.query.model.QueryOperation;

@Data
public class JPAQueryCriteria {
    private String key;
    private String value;
    private QueryOperation operation;
}
