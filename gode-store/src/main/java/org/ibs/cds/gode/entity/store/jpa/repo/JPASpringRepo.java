package org.ibs.cds.gode.entity.store.jpa.repo;

import org.ibs.cds.gode.entity.type.JPAEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@NoRepositoryBean
public interface JPASpringRepo<Entity extends JPAEntity<Id>,Id extends Serializable> extends JpaRepository<Entity, Id>, JpaSpecificationExecutor<Entity> {

    Stream<Entity> findByActive(boolean active);
    Page<Entity> findByActive(boolean active, Pageable pageable);
    Optional<Entity> findByAppId(Long appId);
    List<Entity> findAllById(Iterable<Id> ids);
    Optional<Entity> findById(Id id);
}
