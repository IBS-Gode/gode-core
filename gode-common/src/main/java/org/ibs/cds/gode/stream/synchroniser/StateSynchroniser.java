package org.ibs.cds.gode.stream.synchroniser;

public interface StateSynchroniser<T> {
    void synchronise();
    boolean isSyncing();
}
