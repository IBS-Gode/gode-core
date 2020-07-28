
package org.ibs.cds.gode.entity.query.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class QueryConfig<T> implements Serializable
{
    public QueryConfig(Class<T> entity){
        this.name = entity.getSimpleName().toLowerCase();
        this.type = entity;
    }
    @JsonIgnore
    private String name;
    public Select select;
    public int pageNo;
    public int pageSize;
    private final static long serialVersionUID = -2199236369070389938L;
    @JsonIgnore
    private final Class<T> type;
    private static final String TEMP = "Query[ model: %s, condition: %s]";

    @JsonIgnore
    public String toString(){
        return String.format(TEMP, name, select.toString());
    }

    @JsonIgnore
    public Class<T> getType() {
        return type;
    }
}
