package org.ibs.cds.gode.stream.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.queue.manager.kafka.KafkaEnabler;
import org.ibs.cds.gode.queue.manager.kafka.KafkaProperties;
import org.ibs.cds.gode.queue.manager.kafka.KafkaSecurity;
import org.ibs.cds.gode.stream.GodeStreamLogic;
import org.ibs.cds.gode.stream.config.DataPipelineConf;
import org.ibs.cds.gode.stream.config.Node;
import org.ibs.cds.gode.stream.publisher.StatePublisher;
import org.ibs.cds.gode.stream.synchroniser.StateSynchroniser;
import org.ibs.cds.gode.util.Assert;
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
@Slf4j
public class KafkaDataPipeline implements DataPipeline {

    private final DataPipelineConf configuration;
    private final GodeStreamLogic logic;
    private Environment environment;
    private String prefix;
    private List<StateSynchroniser> synchronisers;
    private List<StatePublisher> publishers;
    private KafkaProperties kafkaProperties;

    @Autowired
    public KafkaDataPipeline(Environment environment, KafkaProperties kafkaProperties, GodeStreamLogic logic) {
        this.environment = environment;
        this.prefix = environment.getProperty("gode.queue.context.prefix", "gode-");
        this.synchronisers = new ArrayList();
        this.publishers = new ArrayList();
        this.kafkaProperties = kafkaProperties;
        this.configuration = this.configuration();
        this.logic = logic;
    }

    @Bean
    public Properties streamProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.CLIENT_ID_CONFIG, kafkaProperties.getClientId());
        properties.put("group.id", kafkaProperties.getGroupId());
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaProperties.getAppName());
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getServers());
        properties.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, kafkaProperties.getStreamReplication());
        properties.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        properties.put("max.poll.interval.ms", 600000);
        boolean authenticated = kafkaProperties.getSecurity() != null;
        authentication(properties, authenticated);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }

    @Override
    public void startStream() {
        KafkaStreams streams = kafkaStreams(streamProperties());
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
        return QueueUtil.topic(prefix, name.concat("View"));
    }
    private String nodeName(String pipelineName, String name){
        return QueueUtil.pipelineNode(pipelineName, name);
    }

    private void addNodes(String pipelineName, String sourceName, Topology topology, Node node) {
        String nodeResolvedName = nodeName(pipelineName, node.getMapTo());
        topology.addProcessor(nodeResolvedName,()-> logic.getProcessor(sourceName, nodeResolvedName).get(), sourceName);
        if (node.getNext() != null) {
            addNodes(pipelineName, nodeResolvedName, topology, node.getNext());
            return;
        }
        if (node.getSink() != null) {
            topology.addSink(nodeName(pipelineName,node.getSink().getName()), topic(node.getSink().getEntity()), nodeResolvedName);
            return;
        }
        log.error("No sink or successive node for pipeline configuration w.r.t pipeline:{} => node:{}",pipelineName, node.getName());
        throw KnownException.INVALID_CONFIG_EXCPETION.provide("No sink or successive node for pipeline configuration from node:" + node.getName());
    }

    private KafkaStreams kafkaStreams(Properties properties) {
        Topology topology = new Topology();
        configuration.getPipelines().stream().forEach(pipeline -> {
            String pipelineEntity = pipeline.getSource().getEntity();
            String pipelineName = pipeline.getName();
            Assert.notNull("Pipeline name and source entity cannot be null", pipelineName, pipelineEntity);
            String source = nodeName(pipelineName, pipelineEntity);
            topology.addSource(source, topic(pipelineEntity));
            addNodes(pipelineName, source, topology, pipeline.getSource().getNext());
        });
        log.info("Topology build complete: {}", topology.describe());
        KafkaStreams streaming = new KafkaStreams(topology, properties);
        return streaming;
    }

    private void authentication(Properties properties, Boolean authenticated) {
        if (authenticated) {
            KafkaSecurity security = kafkaProperties.getSecurity();
            if (security.isSasl()) {
                properties.put("security.protocol","SASL_SSL");
                properties.put("sasl.mechanism", security.getMechanism());
                properties.put("sasl.jaas.config", security.getJaas());
            }
        }
    }
}
