package org.ibs.cds.gode.entity.repo;

import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;

import java.io.Serializable;
import java.util.Optional;

public interface Repo<Entity extends TypicalEntity<Id>, Id extends Serializable> {
    Entity findByAppId(Long appId);
    Optional<Entity> findById(Id id);
    Entity save(Entity entity);
    PagedData<Entity> findAll(PageContext context);
    RepoType type();
}
