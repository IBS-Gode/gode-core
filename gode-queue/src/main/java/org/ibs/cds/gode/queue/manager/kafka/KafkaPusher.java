package org.ibs.cds.gode.queue.manager.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.queue.manager.QueueDataParser;
import org.ibs.cds.gode.queue.manager.QueuePusher;
import org.ibs.cds.gode.queue.manager.Queueable;
import org.ibs.cds.gode.util.Assert;
import org.ibs.cds.gode.util.Promise;

import java.util.Properties;

@Slf4j
public class KafkaPusher<K,V> implements QueuePusher<K, V, KafkaProperties>{

    private KafkaProducer<K, V> kafkaPusher;
    private QueueDataParser parser;
    private KafkaProperties properties;

    @Override
    public boolean init(KafkaProperties kafkaPusherProperties) {
        this.properties = kafkaPusherProperties;
        kafkaPusher = new KafkaProducer(kafkaPusherProperties.pusherProperties());
        parser = new QueueDataParser();
        return true;
    }


    @Override
    public boolean send(String context, V message) {
       return send(context, null, message);
    }

    @Override
    public boolean send(String context, K key, V message) {
        try {
            Assert.notNull("Kafka queue pusher/message/context cannot be null", kafkaPusher, context, message);
            return sendData(context, key, message);
        } catch (JsonProcessingException e) {
            throw KnownException.QUEUE_PUSH_FAILED_EXCEPTION.provide(e);
        }
    }

    private boolean sendData(String context, K key, Object message) throws JsonProcessingException {
        String queueMessage = parser.parse(message);
        if(key == null) log.warn("No key provided for queue publish for {}", message);
        ProducerRecord producerRecord = key == null ? new ProducerRecord(context, message.hashCode()%this.properties.getActiveProcessors(), null, queueMessage) : new ProducerRecord(context, key.toString(), queueMessage);
        return !(new Promise(kafkaPusher.send(producerRecord)).whenComplete((s, e)->{
            if(e instanceof  Throwable && e != null){
                log.error("Queue push failed for {} | Record: {}", message, producerRecord);
                throw KnownException.QUEUE_PUSH_FAILED_EXCEPTION.provide((Throwable)e);
            }
        }).isCompletedExceptionally());
    }

    @Override
    public boolean send(Queueable<K> message) {
        try {
            Assert.notNull("Kafka queue pusher/queueable message/context cannot be null", kafkaPusher, message, message.context());
            return sendData(message.context(), message.getKey(), message);
        } catch (JsonProcessingException e) {
            throw KnownException.QUEUE_PUSH_FAILED_EXCEPTION.provide(e);
        }
    }

}
