package org.ibs.cds.gode.stream.synchroniser;

import org.ibs.cds.gode.entity.manager.EntityManager;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.queue.manager.QueueManager;
import org.ibs.cds.gode.queue.manager.QueueRepository;
import org.ibs.cds.gode.stream.repo.DataPipeline;
import org.ibs.cds.gode.util.QueueUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class QueueStateSynchroniser<V extends EntityView<?>> implements StateSynchroniser {

    private final EntityManager<V, ?, ?> manager;
    private final QueueManager<?,V> queueManager;
    private AtomicBoolean syncing = new AtomicBoolean(false);

    public QueueStateSynchroniser(String type, EntityManager<V,?,?> manager, QueueRepository queueRepository, DataPipeline dataPipeline){
        this.manager = manager;
        this.queueManager = new QueueManager(QueueUtil.topic(queueRepository.getQueuePrefix(), type), queueRepository.getQueueRepo(),queueRepository.getPusherProperties(), queueRepository.getSubscriberProperties());
        dataPipeline.registerSynchroniser(this);
    }


    @Override
    public void synchronise() {
        this.queueManager.subscribe(k->k.ifPresent(manager::save));
        this.syncing.compareAndSet(false, true);
    }

    @Override
    public boolean isSyncing(){
        return syncing.get();
    }

}
