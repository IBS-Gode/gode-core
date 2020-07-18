package org.ibs.cds.gode.exception;

@FunctionalInterface
public interface CheckedFunction<T,R> {
    R apply(T t) throws Exception;
}