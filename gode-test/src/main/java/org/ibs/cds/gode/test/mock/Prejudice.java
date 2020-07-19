package org.ibs.cds.gode.test.mock;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Data
public class Prejudice {
    private final String name;
    private final BiPredicate<Method,Object[]> filter;
    private final Function<Method, String> methodNameFunction;
    private final Function<Object[], List<Object>> argFunction;
    private final BiFunction<Method, Object[], Object> resultFunction;
}
