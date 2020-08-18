
package org.ibs.cds.gode.stream.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataPipelineConf implements Serializable
{

    private List<Pipeline> pipelines = null;
    private final static long serialVersionUID = 3449967976714433108L;

}
