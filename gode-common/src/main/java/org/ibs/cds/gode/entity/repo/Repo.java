package org.ibs.cds.gode.entity.repo;

import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface Repo<Entity extends TypicalEntity<Id>, Id extends Serializable> {
    Optional<Entity> findByAppId(Long appId);
    Optional<Entity> findById(Id id);
    List<Entity> findByIdIn(List<Id> id);
    Entity save(Entity entity);
    PagedData<Entity> findAll(PageContext context);
    RepoType type();
    default PagedData<Entity> findAll(QueryConfig<Entity> queryConfig){
        throw KnownException.QUERY_FAILED.provide("Not implemented");
    }
}
