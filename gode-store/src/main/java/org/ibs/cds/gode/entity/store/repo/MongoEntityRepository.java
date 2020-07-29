package org.ibs.cds.gode.entity.store.repo;

import com.querydsl.core.types.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.query.model.QueryConfig;
import org.ibs.cds.gode.entity.query.parse.QueryParser;
import org.ibs.cds.gode.entity.query.parse.RawMongoQueryParser;
import org.ibs.cds.gode.entity.type.MongoEntity;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.ibs.cds.gode.system.GodeAppEnvt;
import org.ibs.cds.gode.util.PageUtils;
import org.ibs.cds.gode.util.StreamUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MongoEntityRepository<Entity extends MongoEntity<Id>, Id extends Serializable,Repo extends MongoEntityRepo<Entity,Id>> implements StoreEntityRepo<Entity,Id> {

    protected Repo repo;
    private QueryParser<Entity, BasicQuery> queryParser;
    public MongoEntityRepository(Repo repo) {
        this.repo = repo;
        this.queryParser = new RawMongoQueryParser();
    }

    @Override
    public Optional<Entity> findByAppId(Long appId) {
        return repo.findByAppId(appId).filter(MongoEntity::isActive);
    }

    @Override
    public Optional<Entity> findById(Id id) {
        return repo.findById(id).filter(MongoEntity::isActive);
    }

    @Override
    public Stream<Entity> findByActive(boolean enabled) {
        return repo.findByActive(enabled);
    }

    @Override
    public PagedData<Entity> findByActive(boolean enabled, PageContext pageable) {
        return null;
    }

    @Override
    public Entity save(Entity entity) {
        return repo.save(entity);
    }

    @Override
    public PagedData<Entity> findAll(PageContext context) {
        return PageUtils.getData(pc-> repo.findAll(pc), context);
    }

    @Override
    public PagedData<Entity> findAll(Predicate predicate, PageContext context) {
        return PageUtils.getData( pc-> repo.findAll(predicate, pc), context, predicate);
    }

    @Override
    public List<Entity> findByIdIn(List<Id> id) {
        return CollectionUtils.isEmpty(id) ? List.of() : StreamUtils.from(this.repo.findAllById(()->id.iterator())).collect(Collectors.toList());
    }

    @Override
    public PagedData<Entity> findAll(QueryConfig<Entity> queryConfig) {
        MongoTemplate mongoTemplate = GodeAppEnvt.getObject(MongoTemplate.class);
        Pair<BasicQuery, PageContext> queryAndCtx = queryParser.doParse(queryConfig);
        long totalCount = mongoTemplate.count(queryAndCtx.getKey(), queryConfig.getType());
        List<Entity> entities = mongoTemplate.find(queryAndCtx.getKey(), queryConfig.getType());
        return PageUtils.getData(entities, totalCount, queryAndCtx.getValue());
    }
}
