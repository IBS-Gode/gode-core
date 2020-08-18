
package org.ibs.cds.gode.stream.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class Node implements Serializable
{

    private String name;
    private String mapTo;
    private Sink sink;
    private Node next;
    private final static long serialVersionUID = 520730522255386580L;

}
