package org.ibs.cds.gode.test.unit;

import org.ibs.cds.gode.queue.manager.*;
import org.ibs.cds.gode.test.mock.Mock;
import org.springframework.core.env.Environment;

import java.util.Properties;

public class AsyncTest {

    public static QueueRepository initQueueRepository() {
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
