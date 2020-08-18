package org.ibs.cds.gode.entity.manager;

import lombok.extern.slf4j.Slf4j;
import org.ibs.cds.gode.entity.cache.repo.CacheableEntityRepo;
import org.ibs.cds.gode.entity.manager.operation.StateEntityManagerOperation;
import org.ibs.cds.gode.entity.repo.RepoType;
import org.ibs.cds.gode.entity.store.repo.StoreEntityRepo;
import org.ibs.cds.gode.entity.type.StateEntity;
import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.queue.manager.QueueManager;
import org.ibs.cds.gode.queue.manager.QueueRepository;
import org.ibs.cds.gode.util.QueueUtil;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public abstract class AsyncEntityManager<View extends EntityView<Id>, Entity extends StateEntity<Id>,
        Id extends Serializable> extends EntityManager<View, Entity,Id>
        implements StateEntityManagerOperation<View, Entity, Id> {

    private QueueManager<Id,View> queueManager;
    public <StoreRepo extends StoreEntityRepo<Entity, Id>, CacheRepo extends CacheableEntityRepo<Entity, Id>>
    AsyncEntityManager(String context, StoreRepo storeEntityRepo, CacheRepo cacheableEntityRepo,
                       QueueRepository queueRepository) {
        super(storeEntityRepo, cacheableEntityRepo);
        queueManager = new QueueManager(QueueUtil.topic(queueRepository.getQueuePrefix(), context), queueRepository.getQueueRepo(),queueRepository.getPusherProperties(), queueRepository.getSubscriberProperties());
        queueManager.subscribe(storeSave());
    }

    public <StoreRepo extends StoreEntityRepo<Entity, Id>, CacheRepo extends CacheableEntityRepo<Entity, Id>>
    AsyncEntityManager(String context, StoreRepo storeEntityRepo, CacheRepo cacheableEntityRepo,
                       QueueManager queueManager) {
        super(storeEntityRepo, cacheableEntityRepo);
        this.queueManager = queueManager;
        this.queueManager.subscribe(storeSave());
    }

    @NotNull
    protected Consumer<Optional<View>> storeSave() {
        return opts->opts.ifPresent(view->{
            log.debug("Async store persistence started for view: {}",view);
            var storeRepo = this.repository.get(RepoType.STORE);
            if(storeRepo != null){
                storeRepo.save(transform(view));
                log.debug("Async store persistence complete for view: {}",view);
            }
        });
    }


    @Override
    public Optional<Entity> doSave(Optional<Entity> entityOptional) {
        entityOptional.ifPresent(k->{
            var cache =repository.get(RepoType.CACHE);
            if(cache != null) cache.save(k);
            queueManager.push(k.getId(), this.transform(k));
            log.debug("Store persistence deferred for entity: {}",k);
        });
        return entityOptional;
    }
}
