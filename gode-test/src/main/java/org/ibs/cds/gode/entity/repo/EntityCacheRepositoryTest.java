package org.ibs.cds.gode.entity.repo;

import com.mysema.commons.lang.Pair;
import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.cache.repo.CacheableEntityRepo;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.test.mock.Mock;
import org.ibs.cds.gode.test.unit.GodeUnitTest;
import org.ibs.cds.gode.util.RandomUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EntityCacheRepositoryTest<T extends CacheableEntityRepo<E,Id>, E extends StoreEntity<Id>, Id extends Serializable>  extends GodeUnitTest {

    private T repository;

    @SneakyThrows
    public T initRepository(){
        return repositoryClass().getConstructor(repoClass()).newInstance(Mock.partial(repoClass()));
    };

    public abstract Class repoClass();
    public abstract Class<T> repositoryClass();
    public abstract Class<Id> idClass();
    public Id id(){
        return RandomUtils.unique(idClass());
    };
    public abstract E entity();

    @BeforeMethod
    @Override
    public void initTest() {
        repository = initRepository();
    }

    @Test
    public void testSave(){
        E e = entity();
        Id id = id();
        e.setId(id);
        Mock.when(repoClass(), "save", id, e).thenReturn(e);
        E actual = repository.save(e);
        Assert.assertEquals(e.getId(), actual.getId());
    }

    @Test
    public void testFindById(){
        E e = entity();
        Id id = id();
        e.setActive(true);
        e.setId(id);
        Mock.when(repoClass(), "findById", id).thenReturn(Optional.of(e));
        Optional<E> actual = repository.findById(id);
        Assert.assertEquals(e.getId(), actual.get().getId());
    }

    @Test
    public void testFindById_DoNotGiveInactive(){
        E e = entity();
        Id id = id();
        e.setId(id);
        e.setActive(false);
        Mock.when(repoClass(), "findById", id).thenReturn(Optional.of(e));
        Optional<E> actual = repository.findById(id);
        Assert.assertFalse(actual.isPresent(), "Inactive entity should not given");
    }

    @Test
    public void testFindByIdIn(){
        Map<Id, E> t = Stream.iterate(0, i->i<11, i->i+1).map(i-> {
            Id id = id();
            E e = entity();
            e.setActive(true);
            e.setId(id);
            return Pair.of(id, e);
        })
                .collect(Collectors.toMap(s->s.getFirst(), s->s.getSecond()));
        List<E> expected = new ArrayList(t.values());
        Mock.when(repoClass(), "findAllById").thenReturn(expected);
        List<E> actual = repository.findByIdIn(new ArrayList(t.keySet()));
        Assert.assertEquals(t.values().size(), actual.size());
        Assert.assertEquals(expected.get(0).getId(), actual.get(0).getId());
    }
}
