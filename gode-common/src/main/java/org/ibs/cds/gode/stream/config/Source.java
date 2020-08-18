
package org.ibs.cds.gode.stream.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class Source implements Serializable
{

    private StreamSourceType type;
    private String entity;
    private String queue;
    private Node next;
    private final static long serialVersionUID = -4959370730009887738L;

}
