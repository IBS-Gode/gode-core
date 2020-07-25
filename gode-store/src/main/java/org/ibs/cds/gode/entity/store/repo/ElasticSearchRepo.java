package org.ibs.cds.gode.entity.store.repo;

import org.ibs.cds.gode.entity.store.elasticsearch.repo.ElasticSearchTechnicalRepo;
import org.ibs.cds.gode.entity.type.ElasticSearchEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface ElasticSearchRepo<Entity extends ElasticSearchEntity<Id>, Id extends Serializable> extends ElasticSearchTechnicalRepo<Entity, Id>,DynamicQueryRepo<Entity>{

}
