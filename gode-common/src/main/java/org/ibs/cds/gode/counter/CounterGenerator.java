package org.ibs.cds.gode.counter;

import java.math.BigInteger;

public interface CounterGenerator {

    BigInteger getNextValue(String context);
    BigInteger getCurrentValue(String context);
    boolean increment(String context);
    boolean atomicIncrement(String context);
}
