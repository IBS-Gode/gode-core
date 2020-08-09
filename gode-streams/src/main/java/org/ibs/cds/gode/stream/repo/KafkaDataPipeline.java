package org.ibs.cds.gode.stream.repo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.queue.manager.kafka.KafkaEnabler;
import org.ibs.cds.gode.stream.config.DataPipelineConf;
import org.ibs.cds.gode.stream.config.Node;
import org.ibs.cds.gode.stream.publisher.StatePublisher;
import org.ibs.cds.gode.stream.synchroniser.StateSynchroniser;
import org.ibs.cds.gode.util.QueueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Conditional(KafkaEnabler.class)
@Configuration
public class KafkaDataPipeline implements DataPipeline {

    private Environment environment;
    private String prefix;
    private List<StateSynchroniser> synchronisers;
    private List<StatePublisher> publishers;

    @Autowired
    public KafkaDataPipeline(Environment environment) {
        this.environment = environment;
        this.prefix = environment.getProperty("gode.stream.kafka.context.prefix", "gode-");
        this.synchronisers = new ArrayList();
        this.publishers = new ArrayList();
    }

    @Bean
    public Properties streamProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.CLIENT_ID_CONFIG, environment.getProperty("gode.stream.kafka.client_id"));
        properties.put("group.id", environment.getProperty("gode.stream.kafka.context"));
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, environment.getProperty("gode.stream.kafka.app"));
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("gode.stream.kafka.server"));
        properties.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, environment.getProperty("gode.stream.kafka.replication"));
        properties.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        Boolean authenticated = environment.getProperty("gode.stream.kafka.auth", Boolean.class, false);
        String protocol = environment.getProperty("gode.stream.kafka.auth.protocol", String.class, "SASL_SSL");
        authentication(properties, authenticated, protocol);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }

    @Override
    public void startStream() {
        KafkaStreams streams = kafkaStreams(configuration(), streamProperties());
        streams.start();
    }

    @Override
    public void startPublish() {
        publishers.stream().filter(k-> !k.isPublishing()).forEach(StatePublisher::publish);
    }

    @Override
    public void startSynchronisation() {
        synchronisers.stream().filter(k -> !k.isSyncing()).forEach(StateSynchroniser::synchronise);
    }

    @Override
    public void registerSynchroniser(StateSynchroniser stateSynchroniser) {
        this.synchronisers.add(stateSynchroniser);
    }

    @Override
    public void registerPublisher(StatePublisher statePublisher) {
        this.publishers.add(statePublisher);
    }


    private String topic(String name){
        return QueueUtil.topic(prefix, name);
    }

    private void addNodes(String sourceName, Topology topology, Node node) {
        topology.addProcessor(node.getName(), ()->StreamLogic.getProcessor(sourceName, node.getName()), sourceName);
        if (node.getNext() != null) {
            addNodes(node.getName(), topology, node.getNext());
            return;
        }
        if (node.getSink() != null) {
            topology.addSink(node.getSink().getName(), topic(node.getSink().getEntity()), node.getName());
            return;
        }
        throw KnownException.INVALID_CONFIG_EXCPETION.provide("No sink or successive node for pipeline configuration from node:" + node.getName());
    }

    private KafkaStreams kafkaStreams(DataPipelineConf dataPipelineConf, Properties properties) {
        Topology topology = new Topology();
        dataPipelineConf.getPipelines().stream().forEach(pipeline -> {
            String pipelineEntity = pipeline.getSource().getEntity();
            topology.addSource(pipelineEntity, topic(pipelineEntity));
            addNodes(pipelineEntity, topology, pipeline.getSource().getNext());
        });
        KafkaStreams streaming = new KafkaStreams(topology, properties);
        return streaming;
    }

    private void authentication(Properties props, Boolean authenticated, String protocol) {
        if (authenticated) {
            props.put("security.protocol", protocol);
            if (protocol.equals("SASL_SSL")) {
                props.put("sasl.mechanism", environment.getProperty("gode.stream.kafka.auth.sasl.mechanism"));
                props.put("sasl.jaas.config", environment.getProperty("gode.stream.kafka.auth.sasl.jaas"));
            }
        }
    }
}
