package org.ibs.cds.gode.entity.store.repo;

import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.entity.type.StateEntity;
import org.ibs.cds.gode.pagination.PagedData;

import java.io.IOException;

public interface DynamicQueryRepo<Entity extends StateEntity<?>> {

    PagedData<Entity> findAll(QueryConfig<Entity> queryConfig);
}
