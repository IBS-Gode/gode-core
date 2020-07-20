package org.ibs.cds.gode.entity.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.manager.EntityManager;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.ibs.cds.gode.pagination.QueryContext;
import org.ibs.cds.gode.pagination.ResponsePageContext;
import org.ibs.cds.gode.test.mock.Mock;
import org.ibs.cds.gode.test.unit.AsyncTest;
import org.ibs.cds.gode.util.APIArgument;
import org.ibs.cds.gode.web.Request;
import org.ibs.cds.gode.web.Response;
import org.ibs.cds.gode.web.context.RequestContext;
import org.testng.Assert;
import org.testng.annotations.Test;


import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class EntityStateControllerTest<C extends EntityStateEndPoint<V,?,M,Id>, M extends EntityManager<V,?,Id> ,V extends EntityView<Id>, Id extends Serializable> extends EntityProcessControllerTest<C,M,V,Id> {

    @Test
    public void testSave(){
        V view = view();
        view.setId(id());
        Mock.when(managerClass(), "save", view).thenReturn(view);
        Response<V> response = this.controller.save(new Request(view, new RequestContext()));
        Assert.assertEquals(view.getId(), response.getData().getId());
    }

    @Test
    public void testDeactivate(){
        V view = view();
        Id id = id();
        view.setId(id);
        Mock.when(managerClass(), "deactivate", id).thenReturn(true);
        Response<Boolean> response =this.controller.deactivate(new Request(id, new RequestContext()));
        Assert.assertEquals(true, response.getData().booleanValue());
    }

    @Test
    public void testDeactivate_Failure(){
        V view = view();
        Id id = id();
        view.setId(id);
        Mock.when(managerClass(), "deactivate", id).thenReturn(false);
        Response<Boolean> response =this.controller.deactivate(new Request(id, new RequestContext()));
        Assert.assertEquals(false, response.getData().booleanValue());
    }

    @Test
    public void testFindById(){
        V view = view();
        Id id = id();
        view.setId(id);
        Mock.when(managerClass(), "find", id).thenReturn(view);
        Response<V> response =this.controller.find(id);
        Assert.assertEquals(view.getId(), response.getData().getId());
    }

    @Test
    public void testFindAll(){
        V view = view();
        Id id = id();
        view.setId(id);

        APIArgument apiArgument = new APIArgument();
        apiArgument.setPageNumber(1);
        apiArgument.setPageSize(10);
        PageContext pageContext = PageContext.fromAPI(apiArgument);
        ResponsePageContext responsePageContext = new ResponsePageContext(pageContext);
        PagedData<V> pagedData = new PagedData();
        pagedData.setData(List.of(view));
        pagedData.setContext(new QueryContext(responsePageContext, null));
        Mock.when(managerClass(), "find").thenReturn(pagedData);

        Response<PagedData<V>> response =this.controller.findAll(apiArgument);
        Assert.assertEquals(pageContext.getPageNumber(), response.getData().getContext().getPageContext().getPageNumber());
        Assert.assertEquals(pageContext.getPageSize(), response.getData().getContext().getPageContext().getPageSize());
        Assert.assertEquals(view.getId(), response.getData().getData().get(0).getId());
    }

    @Test
    public void testFindAllByPredicate(){
        V view = view();
        Id id = id();
        view.setId(id);
        Predicate predicate = new BooleanBuilder().getValue();
        APIArgument apiArgument = new APIArgument();
        apiArgument.setPageNumber(1);
        apiArgument.setPageSize(10);
        PageContext pageContext = PageContext.fromAPI(apiArgument);
        ResponsePageContext responsePageContext = new ResponsePageContext(pageContext);
        PagedData<V> pagedData = new PagedData();
        pagedData.setData(List.of(view));
        pagedData.setContext(new QueryContext(responsePageContext, "predicate"));
        Mock.when(managerClass(), "find").thenReturn(pagedData);
        Response<PagedData<V>> response =this.controller.findAllByPredicate(predicate, apiArgument);
        Assert.assertEquals(pageContext.getPageNumber(), response.getData().getContext().getPageContext().getPageNumber());
        Assert.assertEquals(pageContext.getPageSize(), response.getData().getContext().getPageContext().getPageSize());
        Assert.assertEquals(view.getId(), response.getData().getData().get(0).getId());
        Assert.assertEquals("predicate", response.getData().getContext().getPredicate());
    }

    public abstract Optional<Class> storeRepo();
    public abstract Optional<Class> cacheRepo();
    public boolean isAsync(){
        return false;
    };

    @SneakyThrows
    public C controller(){
        Class<M> managerClass = managerClass();
        storeRepo().ifPresent(Mock::partial);
        cacheRepo().ifPresent(Mock::partial);
        if(isAsync()) AsyncTest.initQueueRepository();
        return controllerClass().getConstructor(managerClass).newInstance(Mock.fresh(managerClass));
    }
}
