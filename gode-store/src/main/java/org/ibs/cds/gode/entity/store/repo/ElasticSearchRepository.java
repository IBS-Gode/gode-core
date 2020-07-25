package org.ibs.cds.gode.entity.store.repo;

import com.querydsl.core.types.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.entity.type.ElasticSearchEntity;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ElasticSearchRepository<Entity extends ElasticSearchEntity<Id>, Id extends Serializable, Repo extends ElasticSearchRepo<Entity,Id>> implements StoreEntityRepo<Entity,Id> {

    protected final Repo repo;
    public ElasticSearchRepository(Repo repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Entity> findByAppId(Long appId) {
        return this.repo.findByAppId(appId).filter(Entity::isActive);
    }

    @Override
    public Optional<Entity> findById(Id id) {
        try {
            return repo.findById(id).filter(Entity::isActive);
        } catch (IOException e) {
            throw KnownException.SAVE_FAILED.provide(e, "Error from repository");
        }
    }

    @Override
    public Stream<Entity> findByActive(boolean enabled) {
        try {
            return repo.findByActive(enabled);
        } catch (IOException e) {
            throw KnownException.QUERY_FAILED.provide(e, "Error from repository");
        }
    }

    @Override
    public PagedData<Entity> findByActive(boolean enabled, PageContext pageable) {
        try {
            return repo.findByActive(enabled, pageable);
        } catch (IOException e) {
            throw KnownException.QUERY_FAILED.provide(e, "Error from repository");
        }
    }

    @Override
    public Entity save(Entity entity) {
        try {
            return repo.save(entity);
        } catch (IOException e) {
            throw KnownException.SAVE_FAILED.provide(e, "Error from repository");
        }
    }

    @Override
    public PagedData<Entity> findAll(PageContext pageable) {
        try {
            return repo.findAll(pageable);
        } catch (IOException e) {
            throw KnownException.QUERY_FAILED.provide(e, "Error from repository");
        }
    }

    @Override
    public PagedData<Entity> findAll(Predicate predicate, PageContext context) {
        throw KnownException.QUERY_FAILED.provide("Dynamic query is not supported in Elastic-search");
    }

    @Override
    public List<Entity> findByIdIn(List<Id> ids) {
        if(CollectionUtils.isEmpty(ids)) return Collections.emptyList();
        return repo.findAllById(ids);
    }

    @Override
    public PagedData<Entity> findAll(QueryConfig<Entity> queryConfig) {
        return repo.findAll(queryConfig);
    }
}
