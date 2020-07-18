package org.ibs.cds.gode.entity.generic;

import lombok.Data;
import org.ibs.cds.gode.util.EntityUtil;

import java.io.Serializable;

@Data
public class AB<A,B> implements Serializable {
    private final A a;
    private final B b;

    public static <A,B> AB<A,B> of(A a, B b){
        return new AB(a,b);
    }

    @Override
    public String toString() {
        return EntityUtil.toString(this);
    }

    @Override
    public int hashCode() {
        return EntityUtil.hashCode(this);
    }
}
