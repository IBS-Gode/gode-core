package org.ibs.cds.gode.stream.synchroniser;

import com.google.common.reflect.TypeToken;
import org.ibs.cds.gode.entity.manager.EntityManager;
import org.ibs.cds.gode.entity.type.StateEntity;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.queue.manager.QueueManager;
import org.ibs.cds.gode.queue.manager.QueueRepository;
import org.ibs.cds.gode.stream.repo.DataPipeline;
import org.ibs.cds.gode.system.GodeAppEnvt;
import org.ibs.cds.gode.util.EntityUtil;
import org.ibs.cds.gode.util.QueueUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class QueueStateSynchroniser<V extends EntityView<?>, T extends StateEntity<?>> implements StateSynchroniser {

    private final EntityManager<V, T, ?> manager;
    private final QueueManager<T> queueManager;
    private AtomicBoolean syncing = new AtomicBoolean(false);

    public QueueStateSynchroniser(EntityManager<V,T,?> manager, QueueRepository queueRepository, DataPipeline dataPipeline){
        this.manager = manager;
        Class<T> rawType = EntityUtil.rawType( new TypeToken<T>(getClass()) { });
        this.queueManager = new QueueManager(QueueUtil.topic(queueRepository.getQueuePrefix(), rawType), queueRepository.getQueueRepo(),queueRepository.getPusherProperties(), queueRepository.getSubscriberProperties());
        dataPipeline.registerSynchroniser(this);
    }


    @Override
    public void synchronise() {
        this.queueManager.subscribe(k->manager.transformEntity(k).ifPresent(manager::save));
        this.syncing.compareAndSet(false, true);
    }

    @Override
    public boolean isSyncing(){
        return syncing.get();
    }

}
