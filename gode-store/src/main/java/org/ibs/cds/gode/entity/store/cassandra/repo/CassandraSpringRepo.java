package org.ibs.cds.gode.entity.store.cassandra.repo;

import org.ibs.cds.gode.entity.type.CassandraEntity;
import org.ibs.cds.gode.entity.type.JPAEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
@NoRepositoryBean
public interface CassandraSpringRepo<Entity extends CassandraEntity<Id>,Id extends Serializable> extends CassandraRepository<Entity, Id> {

    Stream<Entity> findByActive(boolean active);
    Slice<Entity> findByActive(boolean active, Pageable pageable);
    Long countByActive(boolean active);
    Optional<Entity> findByAppId(Long appId);
    List<Entity> findAllById(Iterable<Id> ids);
    Optional<Entity> findById(Id id);
}
