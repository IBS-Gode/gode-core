package org.ibs.cds.gode.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.ibs.cds.gode.entity.generic.Try;
import org.ibs.cds.gode.queue.manager.QueueDataParser;

/**
 *
 * @author manugraj
 */
@Slf4j
public abstract class DataProcessor<T, O> extends AbstractProcessor<String, String> {

    public abstract T process(T data);
    public abstract O transform(T data);
    private final QueueDataParser parser;

    public DataProcessor(){
        this.parser = new QueueDataParser();
    }

    @Override
    public void process(String key, String value) {

        Try
                .code((String k) -> (T) parser.read(k))
                .catchWith(e->log.error("Error while deserialization of queue data in downstream",e))
                .run(value)
                .map(this::process)
                .map(this::transform)
                .ifPresent(k -> pushForward(k, key));

    }

    private void pushForward(O k, String key) {
        Try.code( (Object j) -> parser.parse(j))
                .catchWith(e->log.error("Error while serialization of queue data in upstream",e))
                .run(k)
                .ifPresent(data -> {
                    context().forward(key, data);
                    context().commit();
                });
    }



}
