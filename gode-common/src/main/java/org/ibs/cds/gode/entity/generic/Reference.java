package org.ibs.cds.gode.entity.generic;

import lombok.Data;

@Data
public class Reference<T> {

    private final Class<T> classType;
    private final Object object;

    public Reference(T object){
        this.classType = (Class<T>) object.getClass();
        this.object = object;
    }

    public T getValue(){
        return this.classType.cast(this.object);
    }
}
