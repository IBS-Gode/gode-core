package org.ibs.cds.gode.entity.manager;

import lombok.extern.slf4j.Slf4j;
import org.ibs.cds.gode.counter.CounterSpectator;
import org.ibs.cds.gode.entity.cache.repo.CacheableEntityRepo;
import org.ibs.cds.gode.entity.manager.operation.PureStateEntityManagerOperation;
import org.ibs.cds.gode.entity.repo.Repo;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.entity.store.repo.StoreEntityRepo;
import org.ibs.cds.gode.entity.type.StateEntity;
import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.status.BinaryStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public class PureEntityManager<Entity extends StateEntity<Id>,
        Id extends Serializable> extends EntityManager<Entity,Entity, Id>
        implements PureStateEntityManagerOperation< Entity, Id> {

    public <StoreRepo extends StoreEntityRepo<Entity, Id>, CacheRepo extends CacheableEntityRepo<Entity, Id>> PureEntityManager(
            StoreRepo storeEntityRepo,
            CacheRepo cacheableEntityRepo) {
        super(storeEntityRepo, cacheableEntityRepo);
    }

    @Override
    public Entity transformFields(Entity entity) {
        return entity;
    }

    @Override
    public Optional<Entity> transformEntity(Optional<Entity> entityOpts) {
        return entityOpts;
    }

    @Override
    public Optional<Entity> transformView(Optional<Entity> viewOpts) {
        return viewOpts;
    }

    @Override
    public ValidationStatus validateEntity(Entity entity) {
        return ValidationStatus.ok();
    }

    protected Optional<Entity> findById(Id id) {
        return operateFirst(id, (r, i) -> r.findById(i));
    }

    @Override
    public Entity transform(Entity entity) {
        return entity;
    }
}
