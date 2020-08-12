package org.ibs.cds.gode.queue.manager;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class QueueManager<K, V> {

    private final QueueRepo queueRepo;
    private QueuePusher<K, V,?> pusher;
    private String context;
    private QueueRepoProperties.SubscriberProperties subscriberProperties;

    public QueueManager(String context, QueueRepo queueRepo,
                        QueueRepoProperties.PusherProperties pusherProperties,
                        QueueRepoProperties.SubscriberProperties subscriberProperties){
        this.pusher = queueRepo.pusher(pusherProperties);
        this.queueRepo = queueRepo;
        this.context = context;
        this.subscriberProperties = subscriberProperties;
    }

    public boolean push(Pair<K,V>... data){
        return Arrays.stream(data).map(d ->pusher.send(context,d.getKey(), d.getValue())).allMatch(k->k);
    }

    public boolean push(Map<K,V> data){
        if(MapUtils.isEmpty(data)) return false;
        return data.entrySet().stream().map(d ->pusher.send(context,d.getKey(), d.getValue())).allMatch(k->k);
    }

    public boolean push(K key, V data){
        return this.pusher.send(context, key,data);
    }

    public boolean push(V data){
        return this.pusher.send(context,data);
    }

    public boolean subscribe(Consumer<Optional<V>> consumptionFunction){
        QueueSubscriber<V,?> consumer = queueRepo.consumer(subscriberProperties);
        consumer.subscribe(context, subscriberProperties.getPollInterval(), consumptionFunction);
        return true;
    }

}
