package org.ibs.cds.gode.entity.manager;

import lombok.SneakyThrows;
import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.exception.Error;
import org.ibs.cds.gode.exception.GodeRuntimeException;
import org.ibs.cds.gode.queue.manager.*;
import org.ibs.cds.gode.test.mock.Mock;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.util.Properties;

public abstract class AsyncEntityManagerTest<T extends AsyncEntityManager, V extends EntityView<Id>, E extends TypicalEntity<Id>, Id extends Serializable>
        extends EntityManagerTest<T,V,E,Id> {

    @Override @SneakyThrows
    public T manager() {
        QueueRepository queueRepository = initQueueRepository();

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

    protected QueueRepository initQueueRepository() {
        QueueRepoProperties.PusherProperties pp = Mock.of(QueueRepoProperties.PusherProperties.class);
        QueueRepoProperties.SubscriberProperties sp = Mock.of(QueueRepoProperties.SubscriberProperties.class);
        Mock.when(Environment.class, "getProperty","gode.queue.context.prefix","gode-").thenReturn("gode-");
        QueueRepository queueRepository = Mock.of(QueueRepository.class);
        Mock.when(QueueRepository.class, "getQueuePrefix").thenReturn("gode-");
        QueueRepo repo = Mock.of(QueueRepo.class);
        QueueSubscriber queueSubscriber = Mock.of(QueueSubscriber.class);
        QueuePusher queuePusher = Mock.of(QueuePusher.class);
        Mock.when(QueueRepo.class, "pusher").thenReturn(queuePusher);
        Mock.when(QueuePusher.class, "send").thenReturn(true);
        Mock.when(QueueRepo.class, "consumer").thenReturn(queueSubscriber);
        Mock.when(QueueRepository.class, "getQueueRepo").thenReturn(repo);
        Mock.when(QueueRepository.class, "getPusherProperties").thenReturn(pp);
        Mock.when(QueueRepository.class, "getSubscriberProperties").thenReturn(sp);
        Mock.when(QueueRepoProperties.SubscriberProperties.class, "getPollInterval").thenReturn(60);
        Mock.when(QueueRepoProperties.SubscriberProperties.class, "subscriberProperties").thenReturn(new Properties());
        return queueRepository;
    }
}
