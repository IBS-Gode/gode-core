package org.ibs.cds.gode.entity.repo;

import com.mysema.commons.lang.Pair;
import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.entity.store.repo.StoreEntityRepo;
import org.ibs.cds.gode.test.mock.Mock;
import org.ibs.cds.gode.test.unit.GodeUnitTest;
import org.ibs.cds.gode.util.RandomUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EntityStoreRepositoryTest<T extends StoreEntityRepo<E,Id>, E extends StoreEntity<Id>, Id extends Serializable> extends GodeUnitTest {

    private T repository;

    @SneakyThrows
    public T initRepository(){
        return repositoryClass().getConstructor(repoClass()).newInstance(Mock.partial(repoClass()));
    };

    public abstract Class repoClass();
    public abstract Class<Id> idClass();
    public Id id(){
       return RandomUtils.unique(idClass());
    };
    public abstract E entity();
    public abstract Class<T> repositoryClass();

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
        Mock.when(repoClass(), "save", e).thenReturn(e);
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
    public void testFindByAppId(){
        E e = entity();
        Id id = id();
        long appId = UUID.randomUUID().getLeastSignificantBits();
        e.setActive(true);
        e.setId(id);
        e.setAppId(appId);
        Mock.when(repoClass(), "findByAppId", appId).thenReturn(Optional.of(e));
        Optional<E> actual = repository.findByAppId(appId);
        Assert.assertEquals(e.getId(), actual.get().getId());
        Assert.assertEquals(e.getAppId(), actual.get().getAppId());

    }

    @Test
    public void testFindByAppId_DoNotGiveInactive(){
        E e = entity();
        Id id = id();
        long appId = UUID.randomUUID().getLeastSignificantBits();
        e.setActive(false);
        e.setId(id);
        e.setAppId(appId);
        Mock.when(repoClass(), "findByAppId", appId).thenReturn(Optional.of(e));
        Optional<E> actual = repository.findByAppId(appId);
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
                .collect(Collectors.toMap(s->s.getFirst(),s->s.getSecond()));
        List<E> expected = new ArrayList(t.values());
        Mock.when(repoClass(), "findAllById").thenReturn(expected);
        List<E> actual = repository.findByIdIn(new ArrayList(t.keySet()));
        Assert.assertEquals(t.values().size(), actual.size());
        Assert.assertEquals(expected.get(0).getId(), actual.get(0).getId());
    }

    @Test
    public void testFindByActive(){
        Map<Boolean, List<E>> t = Stream.iterate(0, i->i<11, i->i+1).map(i-> {
            Id id = id();
            E e = entity();
            if(i % 2 == 0){
                e.setActive(true);
            }else{
                e.setActive(false);
            }
            e.setId(id);
            return e;
        }).collect(Collectors.partitioningBy(s->s.isActive()));

        List<E> expected = t.get(Boolean.TRUE);
        Mock.when(repoClass(), "findByActive", true).thenReturn(expected.stream());
        List<E> actual = repository.findByActive(true).collect(Collectors.toList());
        Assert.assertEquals(expected.get(0).getId(), actual.get(0).getId());
        Assert.assertEquals(expected.size(), actual.size());

        expected = t.get(Boolean.FALSE);
        Mock.when(repoClass(), "findByActive", false).thenReturn(expected.stream());
        actual = repository.findByActive(false).collect(Collectors.toList());
        Assert.assertEquals(expected.get(0).getId(), actual.get(0).getId());
        Assert.assertEquals(expected.size(), actual.size());
    }
}
