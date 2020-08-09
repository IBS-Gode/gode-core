package org.ibs.cds.gode.queue.manager;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class QueueManager<T> {

    private final QueueRepo queueRepo;
    private QueuePusher<T,?> pusher;
    private String context;
    private QueueRepoProperties.PusherProperties pusherProperties;
    private QueueRepoProperties.SubscriberProperties subscriberProperties;

    public QueueManager(String context, QueueRepo queueRepo,
                        QueueRepoProperties.PusherProperties pusherProperties,
                        QueueRepoProperties.SubscriberProperties subscriberProperties){
        this.pusher = queueRepo.pusher(pusherProperties);
        this.queueRepo = queueRepo;
        this.context = context;
        this.pusherProperties = pusherProperties;
        this.subscriberProperties = subscriberProperties;
    }

    public boolean push(T... data){
        return Arrays.stream(data).map(d ->pusher.send(context, d)).allMatch(k->k);
    }

    public boolean push(List<T> data){
        if(CollectionUtils.isEmpty(data)) return false;
        return data.stream().map(d ->pusher.send(context, d)).allMatch(k->k);
    }

    public boolean push(T data){
        return this.pusher.send(context, data);
    }

    public boolean subscribe(Consumer<Optional<T>> consumptionFunction){
        QueueSubscriber<T,?> consumer = queueRepo.consumer(subscriberProperties);
        consumer.subscribe(context, subscriberProperties.getPollInterval(), consumptionFunction);
        return true;
    }

}
