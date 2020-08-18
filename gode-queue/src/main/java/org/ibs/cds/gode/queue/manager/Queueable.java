package org.ibs.cds.gode.queue.manager;

public interface Queueable<K> {
    String context();
    K getKey();
}
