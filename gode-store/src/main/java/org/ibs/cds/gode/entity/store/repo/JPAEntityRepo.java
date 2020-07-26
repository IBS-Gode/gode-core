package org.ibs.cds.gode.entity.store.repo;

import org.ibs.cds.gode.entity.store.jpa.repo.JPASpringRepo;
import org.ibs.cds.gode.entity.type.JPAEntity;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface JPAEntityRepo<Entity extends JPAEntity<Id>, Id extends Serializable> extends JPASpringRepo<Entity, Id>, QuerydslPredicateExecutor<Entity>{

}
