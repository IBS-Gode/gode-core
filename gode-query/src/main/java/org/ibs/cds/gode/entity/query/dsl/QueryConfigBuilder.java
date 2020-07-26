package org.ibs.cds.gode.entity.query.dsl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ibs.cds.gode.entity.query.model.Order;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.entity.query.model.Select;
import org.ibs.cds.gode.entity.query.model.Where;
import org.ibs.cds.gode.pagination.Sortable;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryConfigBuilder {

    private List<String> returnFields;
    private Where where;
    private String orderField;
    private Sortable.Type orderType;

    public QueryConfig<?> query(Class<? extends QueryConfig> classType){
        QueryConfig config = new QueryConfig(classType);
        Select select = new Select();
        select.setOnly(returnFields);
        select.setOrder(new Order(orderField, orderType));
        select.setWhere(where);
        config.setSelect(select);
        config.setPageSize(10);
        config.setPageNo(1);
        return config;
    }

    public QueryConfig<?> query(Class<? extends QueryConfig> classType, int pageNo, int pageSize){
        QueryConfig config = new QueryConfig(classType);
        Select select = new Select();
        select.setOnly(returnFields);
        select.setOrder(new Order(orderField, orderType));
        select.setWhere(where);
        config.setSelect(select);
        config.setPageNo(pageNo);
        config.setPageSize(pageSize);
        return config;
    }
}
