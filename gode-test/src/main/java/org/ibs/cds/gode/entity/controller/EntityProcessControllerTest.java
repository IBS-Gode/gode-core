package org.ibs.cds.gode.entity.controller;

import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.entity.generic.DataMap;
import org.ibs.cds.gode.entity.manager.EntityViewManager;
import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.test.mock.Mock;
import org.ibs.cds.gode.test.unit.GodeUnitTest;
import org.ibs.cds.gode.util.RandomUtils;
import org.ibs.cds.gode.web.Request;
import org.ibs.cds.gode.web.Response;
import org.ibs.cds.gode.web.context.RequestContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;

public abstract class EntityProcessControllerTest<C extends EntityProcessEndpoint<V,M,Id>, M extends EntityViewManager<V,Id>,V extends EntityView<Id>, Id extends Serializable> extends GodeUnitTest {

    protected C controller;
    @BeforeMethod
    @Override
    public void initTest() {
        this.controller = controller();
    }


    @Test
    public void testValidate(){
        V view = view();
        view.setId(id());
        ValidationStatus ok = ValidationStatus.ok();
        Mock.when(managerClass(), "validateView", view).thenReturn(ok);
        Response<ValidationStatus> response = this.controller.validate(new Request(view, new RequestContext()));
        Assert.assertEquals(ok.getStatus(), response.getData().getStatus());
        Assert.assertTrue(CollectionUtils.isEmpty(response.getData().getErrors()));
    }

    @Test
    public void testProcess(){
        V view = view();
        Id id = id();
        view.setId(id);
        DataMap dataMap = new DataMap();
        dataMap.put("test", true);
        Mock.when(managerClass(), "process", view).thenReturn(dataMap);
        Response<DataMap> response =this.controller.process(new Request(view, new RequestContext()));
        Assert.assertTrue((Boolean) response.getData().get("test"));
    }

    public Id id(){
        return RandomUtils.unique(idClass());
    }

    public abstract Class<M> managerClass();
    public abstract Class<C> controllerClass();
    public abstract Class<Id> idClass();
    public abstract V view();

    @SneakyThrows
    public C controller(){
        Class<M> managerClass = managerClass();
        return controllerClass().getConstructor(managerClass).newInstance(Mock.fresh(managerClass));
    }
}
