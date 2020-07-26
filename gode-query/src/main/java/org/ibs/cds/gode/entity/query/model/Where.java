
package org.ibs.cds.gode.entity.query.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonPropertyOrder({
        "field",
        "operation",
        "operands",
        "and",
        "or"
})
public class Where implements Serializable
{

    private  String field;
    private  QueryOperation operation;
    private  List<Operand> operands;
    private  Compose and;
    private  Compose or;
    private final static long serialVersionUID = 5967398068265857767L;

    public Where() {
        this.operands = new ArrayList();
    }

    public String toString(){
        String args = operands.stream().map(Operand::getValue).collect(Collectors.joining(","));
        String whereString = field.concat(" ").concat(operation.toString()).concat(" ").concat(args);
        if(and != null || or != null){
            return whereString.concat("...");
        }
        return whereString;
    }
}
