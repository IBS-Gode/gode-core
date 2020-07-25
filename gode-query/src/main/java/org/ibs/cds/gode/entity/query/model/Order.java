
package org.ibs.cds.gode.entity.query.model;

import lombok.Data;
import org.ibs.cds.gode.pagination.Sortable;

import java.io.Serializable;

@Data
public class Order implements Serializable
{
    private  String by;
    private Sortable.Type in;
    private final static long serialVersionUID = -1442812545321813721L;

}
