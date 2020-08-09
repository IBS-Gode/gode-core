package org.ibs.cds.gode.stream.publisher;

import com.google.common.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.ibs.cds.gode.queue.manager.QueueManager;
import org.ibs.cds.gode.queue.manager.QueueRepository;
import org.ibs.cds.gode.stream.config.StreamSourceType;
import org.ibs.cds.gode.stream.repo.DataPipeline;
import org.ibs.cds.gode.util.EntityUtil;
import org.ibs.cds.gode.util.QueueUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Slf4j
public abstract class SuppliedStatePublisher<T> implements StatePublisher {

    public abstract Supplier<List<T>> supplier();
    private final QueueManager<T> queueManager;
    private AtomicBoolean publishing = new AtomicBoolean(false);

    public SuppliedStatePublisher(QueueRepository queueRepository, DataPipeline dataPipeline) {
        Class<T> typeClass = EntityUtil.rawType(new TypeToken<T>(getClass()) {});
        this.queueManager = new QueueManager(QueueUtil.topic(queueRepository.getQueuePrefix(), typeClass), queueRepository.getQueueRepo(),queueRepository.getPusherProperties(), queueRepository.getSubscriberProperties());
        dataPipeline.registerPublisher(this);
    }

    @Override
    public StreamSourceType type() {
        return StreamSourceType.SUPPLIER;
    }

    @Override
    public void publish() {
        CompletableFuture
                .runAsync(this::publishAction)
                .whenComplete(logError());
        this.publishing.compareAndSet(false,true);
    }

    private void publishAction() {
        while(true){
           this.queueManager.push(supplier().get());
        }
    }

    protected BiConsumer<Void, Throwable> logError() {
        return (s, e) -> {
            if (e != null) log.error("Error thrown while publishing: {}", e);
        };
    }

    @Override
    public boolean isPublishing() {
        return this.publishing.get();
    }
}
