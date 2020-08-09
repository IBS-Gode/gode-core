package org.ibs.cds.gode.stream.repo;

import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.stream.config.DataPipelineConf;
import org.ibs.cds.gode.stream.synchroniser.StateSynchroniser;
import org.ibs.cds.gode.stream.publisher.StatePublisher;
import org.ibs.cds.gode.util.YamlReadWriteUtil;

import java.io.IOException;
import java.util.Properties;

public interface DataPipeline {
    String PIPELINE_CONFIGURATION = "pipeline.yml";
    Properties streamProperties();
    void startStream();
    void startPublish();
    void startSynchronisation();

    void registerSynchroniser(StateSynchroniser stateSynchroniser);
    void registerPublisher(StatePublisher statePublisher);

    default DataPipelineConf configuration(){
        try {
            return YamlReadWriteUtil.readResource(PIPELINE_CONFIGURATION, DataPipelineConf.class);
        } catch (IOException e) {
            throw KnownException.FAILED_TO_START.provide("Failed to read data pipeline configuration");
        }
    }

    default void start(){
        startPublish();
        startSynchronisation();
        startStream();
    }
}
