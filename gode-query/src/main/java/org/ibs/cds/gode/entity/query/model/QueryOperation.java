package org.ibs.cds.gode.entity.query.model;

import lombok.Getter;

public enum QueryOperation {
    like(1,String.class),
    eq(1, Number.class, String.class),  
    neq(1,Number.class, String.class),
    gt(1,Number.class),
    lt(1,Number.class),
    gte(1,Number.class),
    lte(1,Number.class),
    between(2,Number.class),
    in(-1,Object.class);
    private  @Getter final int args;
    private  @Getter final Class[] classTypeSet;
    
    QueryOperation(int args, Class... classTypeSet){
        this.args = args;
        this.classTypeSet = classTypeSet;
    }
    
}
