
package org.ibs.cds.gode.entity.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ibs.cds.gode.pagination.Sortable;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable
{
    private  String by;
    private Sortable.Type in = Sortable.Type.ASC;
    private final static long serialVersionUID = -1442812545321813721L;

    public String toString(){
        return "Order By: ".concat(by).concat(" ").concat(in.toString());
    }
}
