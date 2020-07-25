package org.ibs.cds.gode.entity.store.elasticsearch.repo;

import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ElasticSearchTechnicalRepo<Entity,Id>   {
    Entity save(Entity entity) throws IOException;
    Entity insert(Entity entity) throws IOException;
    Entity update(Entity entity) throws IOException;
    Optional<Entity> findById(Id id) throws IOException;
    List<Entity> findAllById(List<Id> id);
    PagedData<Entity> findAll(PageContext context) throws IOException;
    PagedData<Entity> findAny(String text, PageContext context) throws IOException;
    PagedData<Entity> findAll(String query, PageContext context) throws IOException;
    String getIndexName();
    Class<Entity> getEntityType();
    Optional<Entity> findByAppId(Long appId);
    Stream<Entity> findByActive(boolean active)  throws IOException;
    PagedData<Entity> findByActive(boolean active, PageContext pageable)  throws IOException;
}
