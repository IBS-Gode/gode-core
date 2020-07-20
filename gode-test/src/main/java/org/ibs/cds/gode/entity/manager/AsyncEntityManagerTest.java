package org.ibs.cds.gode.entity.manager;

import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.type.StateEntity;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.exception.Error;
import org.ibs.cds.gode.exception.GodeRuntimeException;
import org.ibs.cds.gode.queue.manager.QueueRepository;
import org.ibs.cds.gode.test.mock.Mock;
import org.ibs.cds.gode.test.unit.AsyncTest;

import java.io.Serializable;

public abstract class AsyncEntityManagerTest<T extends AsyncEntityManager<V,E,Id>, V extends EntityView<Id>, E extends StateEntity<Id>, Id extends Serializable>
        extends EntityManagerTest<T,V,E,Id> {

    @Override @SneakyThrows
    public T manager() {
        QueueRepository queueRepository = AsyncTest.initQueueRepository();

        if(storeRepo().isPresent() && cacheRepo().isPresent()){
            return managerClass().getDeclaredConstructor(storeRepo().get(), cacheRepo().get(), QueueRepository.class)
                    .newInstance(Mock.partial(storeRepo().get()), Mock.partial(cacheRepo().get()), queueRepository);
        }else if(storeRepo().isPresent()){
            return managerClass().getDeclaredConstructor(storeRepo().get(), QueueRepository.class)
                    .newInstance(Mock.partial(storeRepo().get()), queueRepository);
        }else if(cacheRepo().isPresent()){
            return managerClass().getDeclaredConstructor(cacheRepo().get(), QueueRepository.class)
                    .newInstance(Mock.partial(cacheRepo().get()), queueRepository);
        }else{
            throw new GodeRuntimeException(new Error(-999, "No configuration for manager",null));
        }

    }


}
