package org.ibs.cds.gode.queue.manager;

import org.ibs.cds.gode.exception.GodeQueuePushFailedException;

public interface QueuePusher<K,V,T extends QueueRepoProperties.PusherProperties> {
    boolean init(T properties);
    boolean send(String context, K key, V message) throws GodeQueuePushFailedException;
    boolean send(String context, V message) throws GodeQueuePushFailedException;
    boolean send(Queueable<K> message);
}
