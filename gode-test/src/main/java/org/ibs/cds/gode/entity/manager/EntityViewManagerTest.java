package org.ibs.cds.gode.entity.manager;

import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.test.unit.GodeUnitTest;
import org.ibs.cds.gode.util.RandomUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;

public abstract class EntityViewManagerTest<T extends EntityViewManager<V,Id>, V extends EntityView<Id>,Id extends Serializable>
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

    @SneakyThrows
    public T manager(){
       return managerClass().getConstructor().newInstance();
    }
}
