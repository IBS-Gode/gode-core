package org.ibs.cds.gode.stream;

import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class Test<T> extends AbstractProcessor<String, T> {

    @Override
    public void process(String s, T t) {


    }


    public static void main(String[] args) {
        Topology topology = new Topology();

    }
}
