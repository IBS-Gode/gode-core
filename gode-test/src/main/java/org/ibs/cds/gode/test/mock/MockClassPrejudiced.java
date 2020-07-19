package org.ibs.cds.gode.test.mock;

import javassist.util.proxy.MethodHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MockClassPrejudiced<T> extends MockClass<T> {

    private final Prejudice[] prejudices;

    protected MockClassPrejudiced(Class<T> target, Prejudice[] prejudices) {
        super(target);
        this.prejudices = prejudices;
    }

    public static <T> MockClassPrejudiced<T> of(T object, Prejudice... prejudices) {
        return new MockClassPrejudiced(object.getClass(), prejudices);
    }

    public static <T> MockClassPrejudiced<T> of(Class<T> object, Prejudice... prejudices) {
        return new MockClassPrejudiced(object, prejudices);
    }

    @NotNull
    protected MethodHandler getMethodHandler() {
        return (self, m, proceed, args) -> {
            if (ArrayUtils.isEmpty(prejudices)) return defaultValueProvider(self, m, proceed, args);
            Arrays.stream(prejudices)
                    .filter(p -> p.getFilter().test(m, args))
                    .forEach(prejudice ->
                                super.getResultMap().put(
                                        prejudice.getMethodNameFunction().apply(m),
                                        prejudice.getArgFunction().apply(args),
                                        prejudice.getResultFunction().apply(m, args)
                                )
                    );
            if(m.getReturnType().getName().equalsIgnoreCase("void")) return null;
            return defaultValueProvider(self, m, proceed, args);
        };
    }

}
