package org.ibs.cds.gode.entity.manager;

import org.ibs.cds.gode.queue.manager.QueueManager;
import org.ibs.cds.gode.queue.manager.QueueRepo;
import org.ibs.cds.gode.queue.manager.QueueRepoProperties;
import org.ibs.cds.gode.queue.manager.kafka.KafkaEnabler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Conditional(KafkaEnabler.class)
public class GeneralQueueManager extends QueueManager<String,GeneralData> {

    @Autowired
    public GeneralQueueManager(Environment env, QueueRepo queueRepo, QueueRepoProperties.PusherProperties pusherProperties, QueueRepoProperties.SubscriberProperties subscriberProperties) {
        super(env.getProperty("gode.queue.general","general"), queueRepo, pusherProperties, subscriberProperties);
    }

    public boolean push(GeneralData... generalData){
        return Arrays.stream(generalData).map(k-> this.push(k.getKey(), k)).allMatch(k->k);
    }
}
