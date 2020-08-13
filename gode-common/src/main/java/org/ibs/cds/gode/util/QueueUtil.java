package org.ibs.cds.gode.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class QueueUtil {

    public static String topic(String... contexts){
        return Arrays.stream(contexts).collect(Collectors.joining()).toLowerCase();
    }

    public static String topic(String prefix, Class classType){
        return topic(prefix, classType.getSimpleName());
    }
    public static String pipelineNode(String pipelineName, String nodeName){
        return pipelineName.concat("-").concat(nodeName).toLowerCase();
    }

}
