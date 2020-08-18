package org.ibs.cds.gode.queue.manager.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.queue.manager.QueueDataParser;
import org.ibs.cds.gode.queue.manager.QueueSubscriber;
import org.ibs.cds.gode.util.Assert;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
public class KafkaSubscriber<T> implements QueueSubscriber<T, KafkaProperties> {

    private KafkaConsumer<String,String> consumer;
    private QueueDataParser parser;

    @Override
    public boolean init(KafkaProperties properties) {
        consumer = new KafkaConsumer(properties.subscriberProperties());
        parser = new QueueDataParser();
        return false;
    }

    @Override
    public void subscribe(String context, int pollInterval, Consumer<Optional<T>> consumptionFunction) {
        Assert.notNull("Subscriber is not initialised", consumer);
        consumer.subscribe(List.of(context));
        CompletableFuture
                .runAsync(() -> this.startSubscription( pollInterval < 1 ? 60 : pollInterval, consumptionFunction))
                .whenComplete(logError());
        log.info("Subscription for {} started", context);
    }

    @NotNull
    protected BiConsumer<Void, Throwable> logError() {
        return (s,e)->{
            if(e != null) log.error("Error thrown while subscription: {}", ExceptionUtils.getStackTrace(e));
        };
    }

    @Override
    public void subscribe(String topic, Consumer<Optional<T>> consumptionFunction) {
        subscribe(topic, 60, consumptionFunction);
    }

    private void startSubscription(int pollInterval, Consumer<Optional<T>> consumptionFunction) {
        while (true) {
            consumption(pollInterval, consumptionFunction);
        }
    }

    private void consumption(int pollInterval, Consumer<Optional<T>> consumptionFunction) {

        try {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(pollInterval));
            log.debug("Poll done for queue, received count:{}", records.count());
            for (ConsumerRecord<String, String> record : records) {
                log.debug("Consuming record from queue: offset = {}, value = {}", record.offset(), record.value());
                 T t =  parser.read(record.value());
                 consumptionFunction.accept(Optional.ofNullable(t));
                log.debug("Consumption of record complete");
            }
            consumer.commitSync();
        } catch (Throwable e) {
            log.error("Consumption of record failed due to {}",e);
        }
    }
}
