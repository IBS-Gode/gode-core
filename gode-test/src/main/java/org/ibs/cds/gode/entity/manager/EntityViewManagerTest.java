package org.ibs.cds.gode.entity.manager;

import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.test.unit.GodeUnitTest;
import org.ibs.cds.gode.util.RandomUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;
import org.ibs.cds.gode.entity.function.EntityFunctionBody;
import org.ibs.cds.gode.test.mock.Mock;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class EntityViewManagerTest<T extends EntityViewManager<V,Id>, V extends EntityView<Id>,Id extends Serializable, Function extends EntityFunctionBody<V>>
        extends GodeUnitTest {

    private T entityManager;


    @BeforeMethod
    @Override
    public void initTest() {
        entityManager = manager();
    }

    @Test
    public void testValidate(){

    }

    @Test
    public void testProcess(){

    }
    public abstract Class<T> managerClass();
    public abstract Class<V> viewClass();
    public abstract Class<Id> idClass();

    public Id id(){
        return RandomUtils.unique(idClass());
    }
    
    public abstract Class<Function> processFunctionClass();
    public abstract Function function();

    @SneakyThrows
    public T manager(){
        Function function = function();
        Mock.when(ConfigurableApplicationContext.class, "getBean", processFunctionClass()).thenReturn(function);
       return managerClass().getConstructor().newInstance();
    }
}
