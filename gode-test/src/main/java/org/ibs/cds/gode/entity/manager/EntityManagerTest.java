package org.ibs.cds.gode.entity.manager;

import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.test.mock.Mock;
import org.ibs.cds.gode.test.unit.GodeUnitTest;
import org.ibs.cds.gode.util.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Optional;

public abstract class EntityManagerTest<T extends EntityManager, V extends EntityView<Id>, E extends TypicalEntity<Id>, Id extends Serializable>
        extends GodeUnitTest {

    private T entityManager;

    @Before
    public void initTest() {
        storeRepo().ifPresent(Mock::of);
        cacheRepo().ifPresent(Mock::of);
        entityManager = manager();
    }

    @Test
    public void testSave() {
        V view = Mock.data(viewClass());
        view.setValidated(true);
        view.setActive(true);
        Id id = id();
        view.setId(id);
        E e = entity();
        e.setId(id);
        storeRepo().ifPresent(k -> Mock.when(k, "save").thenReturn(e));
        cacheRepo().ifPresent(k -> Mock.when(k, "save").thenReturn(e));
        Assert.assertEquals(id, entityManager.save(view).getId());
    }

    @Test
    public void testFind() {
        Id id = id();
        E e = entity();
        e.setId(id);
        storeRepo().ifPresent(k -> Mock.when(k, "findById").thenReturn(Optional.of(e)));
        cacheRepo().ifPresent(k -> Mock.when(k, "findById").thenReturn(Optional.of(e)));
        Assert.assertEquals(id, entityManager.find(id).getId());
    }

    @Test
    public void testFindByAppId() {
        Id id = id();
        E e = entity();
        e.setId(id);
        Long appId = RandomUtils.unique();
        storeRepo().ifPresent(k -> Mock.when(k, "findByAppId",appId).thenReturn(Optional.of(e)));
        cacheRepo().ifPresent(k -> Mock.when(k, "findByAppId",appId).thenReturn(Optional.of(e)));
        Assert.assertEquals(id, entityManager.findByAppId(appId).getId());
    }

    @Test
    public void testDeactivate() {
        Id id = id();
        E e = entity();
        e.setId(id);
        storeRepo().ifPresent(k -> Mock.when(k, "findById").thenReturn(Optional.of(e)));
        cacheRepo().ifPresent(k -> Mock.when(k, "findById").thenReturn(Optional.of(e)));
        storeRepo().ifPresent(k -> Mock.when(k, "save").thenReturn(e));
        cacheRepo().ifPresent(k -> Mock.when(k, "save").thenReturn(e));
        Assert.assertEquals(true, entityManager.deactivate(id));
    }

    public abstract Optional<Class<?>> storeRepo();
    public abstract Optional<Class<?>> cacheRepo();
    public abstract Class<V> viewClass();
    public abstract E entity();
    public abstract Id id();
    public abstract T manager();
}
