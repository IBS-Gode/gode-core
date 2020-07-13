package org.ibs.cds.gode.counter;

import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.system.GodeAppEnvt;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class CounterSpectator {

    private final static AtomicReference<CounterGenerator> counterGeneratorRef;

    static {
        counterGeneratorRef = new AtomicReference<>();
    }

    public static Long getNext(String context) {
        return counterGenerator().getNextValue(context).longValue();
    }

    public static boolean increment(String context) {
        return counterGenerator().increment(context);
    }

    public static boolean incrementAtomic(String context) {
        return counterGenerator().atomicIncrement(context);
    }

    private static CounterGenerator counterGenerator() {
        counterGeneratorRef.compareAndSet(null, getCounterGen());
        return counterGeneratorRef.get();
    }

    @NotNull
    private static CounterGenerator getCounterGen() {
        CounterGenerator counterGenerator = GodeAppEnvt.getObject(CounterGenerator.class);
        if (counterGenerator == null) {
            throw KnownException.SYSTEM_FAILURE.provide("Counter system is not initialised");
        }
        return counterGenerator;
    }

    public static Long getCurrent(String context) {
        return counterGenerator().getCurrentValue(context).longValue();
    }
}
