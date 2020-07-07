package org.ibs.cds.gode.entity.type;

import lombok.Data;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Data
public class AutoIncrementField {

    private final String name;
    private final Supplier<Long> fieldGetter;
    private final Consumer<Long> fieldSetter;

    public static AutoIncrementField of(String name, Supplier<Long> fieldGetter, Consumer<Long> fieldSetter){
        return new AutoIncrementField(name, fieldGetter, fieldSetter);
    }
}
