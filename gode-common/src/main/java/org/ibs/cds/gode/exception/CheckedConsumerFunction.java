package org.ibs.cds.gode.exception;

@FunctionalInterface
public interface CheckedConsumerFunction<T> {
    void accept(T t) throws Exception;
}