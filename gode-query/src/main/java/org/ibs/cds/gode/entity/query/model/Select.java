
package org.ibs.cds.gode.entity.query.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({
        "only",
        "where",
        "order"
})
public class Select implements Serializable
{
    private  List<String> only;
    private  Where where;
    private  Order order;
    private final static long serialVersionUID = 3193275266655126375L;

    public Select() {
        this.only = new ArrayList();
    }
}
