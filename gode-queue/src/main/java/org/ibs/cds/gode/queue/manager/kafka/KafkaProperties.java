package org.ibs.cds.gode.queue.manager.kafka;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.ibs.cds.gode.queue.manager.QueueRepoProperties;
import org.ibs.cds.gode.system.GodeConstant;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "gode.queue.kafka")
@Conditional(KafkaEnabler.class)
@PropertySource(GodeConstant.GODE_PROPERTIES)
@Data
public class KafkaProperties implements QueueRepoProperties.SubscriberProperties, QueueRepoProperties.PusherProperties {

    private String servers;
    private String groupId;
    private String acknowledgement;
    private String retries;
    private int batchSizeConfig;
    private int linger;
    private int bufferMemoryConfig;
    private KafkaSecurity security;
    private boolean autoCommit;
    private int autoCommitInterval;
    private String offsetReset;
    private String sesssionTimeout;
    private int pollInterval;
    private String clientId;
    private String appName;
    private String streamReplication;
    private int activeProcessors;

    public Properties pusherProperties(){
        Properties properties = generalProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        securityProperties(properties);
        return properties;
    }

    private void securityProperties(Properties properties) {
        if (this.security.isSasl()) {
            properties.put("security.protocol","SASL_SSL");
            properties.put("sasl.mechanism", this.security.getMechanism());
            properties.put("sasl.jaas.config", this.security.getJaas());
        }
    }

    @NotNull
    private Properties generalProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.servers);
        properties.put(ProducerConfig.ACKS_CONFIG, this.acknowledgement);
        properties.put(ProducerConfig.RETRIES_CONFIG, this.retries);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, this.batchSizeConfig);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, this.linger);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemoryConfig);
        properties.put("enable.auto.commit", this.autoCommit);
        properties.put("auto.commit.interval.ms", this.autoCommitInterval);
        properties.put("session.timeout.ms", sesssionTimeout);
        properties.put("auto.offset.reset", offsetReset);
        return properties;
    }

    @Override
    public Properties subscriberProperties() {
        Properties properties = generalProperties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        securityProperties(properties);
        return properties;
    }
}
