package org.ibs.cds.gode.entity.store.repo;

import org.ibs.cds.gode.entity.store.cassandra.repo.CassandraSpringRepo;
import org.ibs.cds.gode.entity.store.jpa.repo.JPASpringRepo;
import org.ibs.cds.gode.entity.type.CassandraEntity;
import org.ibs.cds.gode.entity.type.JPAEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface CassandraEntityRepo<Entity extends CassandraEntity<Id>, Id extends Serializable> extends CassandraSpringRepo<Entity, Id>, QuerydslPredicateExecutor<Entity> {


}
