package org.ibs.cds.gode.entity.manager;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Query;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.ibs.cds.gode.pagination.QueryContext;
import org.ibs.cds.gode.pagination.ResponsePageContext;
import org.ibs.cds.gode.test.mock.Mock;
import org.ibs.cds.gode.test.unit.GodeUnitTest;
import org.ibs.cds.gode.util.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Test
    public void testFindAll() {
        List<E> entities = IntStream.range(1, 12).boxed()
                .map(k-> {
                    E e = entity();
                    e.setId(id());
                    return e;
                })
                .collect(Collectors.toList());
        PageContext context = PageContext.std();
        PagedData<E> entityPage = new PagedData<>();
        entityPage.setData(entities.subList(0, 10));
        QueryContext queryContext = new QueryContext();
        queryContext.setPageContext(new ResponsePageContext(context));
        entityPage.setContext(queryContext);
        storeRepo().ifPresent(j -> Mock.when(j, "findAll", context).thenReturn(entityPage));
        PagedData<V> actual = entityManager.find(context);
        Assert.assertEquals(context.getPageSize(), actual.getContext().getPageContext().getPageSize());
        Assert.assertEquals(context.getPageSize(), actual.getData().size());
        Assert.assertEquals(entities.get(0).getId(), actual.getData().get(0).getId());
    }

    @Test
    public void testFindAllWithWhereClause() {
        List<E> entities = IntStream.range(1, 12).boxed()
                .map(k-> {
                    E e = entity();
                    e.setId(id());
                    return e;
                })
                .collect(Collectors.toList());
        PageContext context = PageContext.std();
        PagedData<E> entityPage = new PagedData<>();
        entityPage.setData(entities.subList(0, 10));
        QueryContext queryContext = new QueryContext();
        queryContext.setPageContext(new ResponsePageContext(context));
        entityPage.setContext(queryContext);
        Predicate expression = new BooleanBuilder().getValue();
        storeRepo().ifPresent(j -> Mock.when(j, "findAll", expression, context).thenReturn(entityPage));
        PagedData<V> actual = entityManager.find(expression, context);
        Assert.assertEquals(context.getPageSize(), actual.getContext().getPageContext().getPageSize());
        Assert.assertEquals(context.getPageSize(), actual.getData().size());
        Assert.assertEquals(entities.get(0).getId(), actual.getData().get(0).getId());
    }

    public abstract Optional<Class<?>> storeRepo();
    public abstract Optional<Class<?>> cacheRepo();
    public abstract Class<V> viewClass();
    public abstract E entity();
    public abstract Id id();
    public abstract T manager();
}
