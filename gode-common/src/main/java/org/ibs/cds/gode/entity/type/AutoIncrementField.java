package org.ibs.cds.gode.entity.type;

import lombok.Data;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Data
public class AutoIncrementField {
    private final Supplier<Long> fieldGetter;
    private final Consumer<Long> fieldSetter;

    public static AutoIncrementField of(Supplier<Long> fieldGetter, Consumer<Long> fieldSetter){
        return new AutoIncrementField(fieldGetter, fieldSetter);
    }
}
