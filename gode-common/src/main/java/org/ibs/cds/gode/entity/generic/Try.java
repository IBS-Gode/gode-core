package org.ibs.cds.gode.entity.generic;

import org.ibs.cds.gode.exception.CheckedConsumerFunction;
import org.ibs.cds.gode.exception.CheckedFunction;
import org.ibs.cds.gode.exception.KnownException;

import java.util.Optional;
import java.util.function.Consumer;

public class Try<E extends Exception, T, R> {
    private final CheckedFunction<T, R> codeBlock;
    private Consumer<Exception> catchBlock;

    private Try(CheckedFunction<T, R> codeBlock) {
        this.codeBlock = codeBlock;
    }

    public static <T, R, E extends Exception> Try<E, T, R> code(CheckedFunction<T, R> codeBlock) {
        return new Try(codeBlock);
    }

    public static <E extends Exception, T, R> Try<E, T, R> code(CheckedConsumerFunction<T> codeBlock) {
        return new Try(t -> {
            codeBlock.accept((T) t);
            return 0;
        });
    }

    public Try<E, T, R> catchWith(Consumer<Exception> catchBlock) {
        this.catchBlock = catchBlock;
        return this;
    }

    public Try<E, T, R> catchWith(KnownException knownException) {
        this.catchBlock = s -> knownException.provide(s);
        return this;
    }

    public Optional<R> run(T data) {
        try {
            return Optional.ofNullable(this.codeBlock.apply(data));
        } catch (java.lang.Exception e) {
            if (this.catchBlock != null){
                this.catchBlock.accept(e);
            }
            return Optional.empty();
        }
    }
}