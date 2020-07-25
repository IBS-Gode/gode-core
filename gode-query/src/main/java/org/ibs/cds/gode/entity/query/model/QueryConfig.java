
package org.ibs.cds.gode.entity.query.model;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class QueryConfig<T> implements Serializable
{
    public QueryConfig(Class<T> entity){
        this.name = entity.getSimpleName().toLowerCase();
    }
    private String name;
    public Select select;
    public int pageNo;
    public int pageSize;
    private final static long serialVersionUID = -2199236369070389938L;
}
