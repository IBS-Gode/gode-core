package org.ibs.cds.gode.entity.store.repo;

import com.querydsl.core.types.Predicate;
import org.ibs.cds.gode.entity.repo.Repo;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public interface StoreEntityRepo<Entity extends StoreEntity<Id>, Id extends Serializable> extends QuerydslPredicateExecutor<Entity>, Repo<Entity,Id> {
    Optional<Entity> findById(Id id);
    Stream<Entity> findByActive(boolean enabled);
    PagedData<Entity> findByActive(boolean enabled, PageContext pageable);
    Entity save(Entity entity);
    boolean deactivate(Id id);
    PagedData<Entity> findAll(PageContext pageable);

    PagedData<Entity> findAll(Predicate predicate, PageContext context);

}