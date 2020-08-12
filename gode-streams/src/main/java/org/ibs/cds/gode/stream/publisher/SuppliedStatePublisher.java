package org.ibs.cds.gode.stream.publisher;

import com.google.common.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.generic.Try;
import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.queue.manager.QueueManager;
import org.ibs.cds.gode.queue.manager.QueueRepository;
import org.ibs.cds.gode.stream.config.StreamSourceType;
import org.ibs.cds.gode.stream.repo.DataPipeline;
import org.ibs.cds.gode.util.EntityUtil;
import org.ibs.cds.gode.util.QueueUtil;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Slf4j
public abstract class SuppliedStatePublisher<T extends TypicalEntity<Id>, Id extends Serializable> implements StatePublisher {

    public abstract Supplier<List<T>> supplier();

    private final QueueManager<Id, T> queueManager;
    private AtomicBoolean publishing = new AtomicBoolean(false);
    private ScheduledExecutorService executor;

    public SuppliedStatePublisher(QueueRepository queueRepository, DataPipeline dataPipeline) {
        Class<T> typeClass = EntityUtil.rawType(new TypeToken<T>(getClass()) {
        });
        this.queueManager = new QueueManager(QueueUtil.topic(queueRepository.getQueuePrefix(), typeClass), queueRepository.getQueueRepo(), queueRepository.getPusherProperties(), queueRepository.getSubscriberProperties());
        this.executor = Executors.newScheduledThreadPool(1);
        dataPipeline.registerPublisher(this);
    }

    @Override
    public StreamSourceType type() {
        return StreamSourceType.SUPPLIER;
    }

    @PreDestroy
    public void close() {
        this.executor.shutdown();
    }

    @Override
    public void publish() {
        executor.scheduleAtFixedRate(() -> publishAction(), 60, publishInterval().getValue(), publishInterval().getKey());
        this.publishing.compareAndSet(false, true);
    }

    private void publishAction() {
        List<T> ts = supplier().get();
        if (ts != null) ts.forEach(t -> queueManager.push(t.getId(), t));
    }

    @Override
    public boolean isPublishing() {
        return this.publishing.get();
    }

    public Pair<TimeUnit, Long> publishInterval() {
        return Pair.of(TimeUnit.SECONDS, 60L);
    }
}
