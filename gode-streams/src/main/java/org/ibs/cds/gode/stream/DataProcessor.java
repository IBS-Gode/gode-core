package org.ibs.cds.gode.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.ibs.cds.gode.entity.generic.Try;
import org.ibs.cds.gode.queue.manager.QueueDataParser;
import org.ibs.cds.gode.stream.repo.StreamLogic;

/**
 *
 * @author manugraj
 */
@Slf4j
public abstract class DataProcessor<T, O> extends AbstractProcessor<String, String> {

    private final String from;
    private final String to;
    public abstract T process(T data);
    public abstract O transform(T data);
    private final QueueDataParser parser;

    public DataProcessor(String from, String to){
        this.from = from;
        this.to = to;
        this.parser = new QueueDataParser();
        StreamLogic.addProcessor(from, to, this);
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
                    this.context().forward(key, data);
                    this.context().commit();
                });
    }



}
