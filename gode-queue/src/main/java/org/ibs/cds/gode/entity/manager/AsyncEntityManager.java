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
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public abstract class AsyncEntityManager<View extends EntityView<Id>, Entity extends StateEntity<Id>,
        Id extends Serializable> extends EntityManager<View, Entity,Id>
        implements StateEntityManagerOperation<View, Entity, Id> {

    private QueueManager<Entity> queueManager;
    public <StoreRepo extends StoreEntityRepo<Entity, Id>, CacheRepo extends CacheableEntityRepo<Entity, Id>>
    AsyncEntityManager(String context, StoreRepo storeEntityRepo, CacheRepo cacheableEntityRepo,
                       QueueRepository queueRepository) {
        super(storeEntityRepo, cacheableEntityRepo);
        queueManager = new QueueManager(queueRepository.getQueuePrefix().concat(context).toLowerCase(), queueRepository.getQueueRepo(),queueRepository.getPusherProperties(), queueRepository.getSubscriberProperties());
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
    protected Consumer<Optional<Entity>> storeSave() {
        return opts->opts.ifPresent(entity->{
            log.debug("Async store persistence started for entity: {}",entity);
            var storeRepo = this.repository.get(RepoType.STORE);
            if(storeRepo != null){
                storeRepo.save(entity);
                log.debug("Async store persistence complete for entity: {}",entity);
            }
        });
    }


    @Override
    public Optional<Entity> doSave(Optional<Entity> entityOptional) {
        entityOptional.ifPresent(k->{
            var cache =repository.get(RepoType.CACHE);
            if(cache != null) cache.save(k);
            queueManager.push(k);
            log.debug("Store persistence deferred for entity: {}",k);
        });
        return entityOptional;
    }
}
