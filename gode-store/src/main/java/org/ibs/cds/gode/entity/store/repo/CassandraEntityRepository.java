package org.ibs.cds.gode.entity.store.repo;

import com.querydsl.core.types.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.entity.type.CassandraEntity;
import org.ibs.cds.gode.entity.type.JPAEntity;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.ibs.cds.gode.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CassandraEntityRepository<Entity extends CassandraEntity<Id>, Id extends Serializable, Repo extends CassandraEntityRepo<Entity,Id>> implements StoreEntityRepo<Entity,Id> {

    protected final Repo repo;

    public CassandraEntityRepository(Repo repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Entity> findByAppId(Long appId) {
        return repo.findByAppId(appId).filter(CassandraEntity::isActive);
    }

    @Override
    public Optional<Entity> findById(Id id) {
        return repo.findById(id).filter(CassandraEntity::isActive);
    }

    @Override
    public List<Entity> findByIdIn(List<Id> id) {
        return CollectionUtils.isEmpty(id) ? List.of() :this.repo.findAllById(()->id.iterator());
    }

    @Override
    public Stream<Entity> findByActive(boolean enabled) {
        return repo.findByActive(enabled);
    }

    @Override
    public PagedData<Entity> findByActive(boolean enabled, PageContext pageable) {
        return PageUtils.getData(pc-> repo.findByActive(enabled, pc), pageable);
    }

    @Override
    public Entity save(Entity entity) {
        return repo.save(entity);
    }

    @Override
    public PagedData<Entity> findAll(PageContext context) {
        return null;
    }
    //
    Page<Entity> convertToPage(Pageable var1){
        Page<Entity> page = new PageImpl<>(repo.findAll(var1).stream().map(x->x).collect(Collectors.toList()));
        return  page;
    }


    @Override
    public PagedData<Entity> findAll(Predicate predicate, PageContext context) {
        return null;
    }
}
