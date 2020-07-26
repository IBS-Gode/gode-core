package org.ibs.cds.gode.entity.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author manugraj
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operand {
    private String value;
    private boolean attribute;

    public static Operand literal(String value){
        return new Operand(value, false);
    }

    public static Operand field(String name){
        return new Operand(name, true);
    }
}
