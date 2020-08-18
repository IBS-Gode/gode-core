
package org.ibs.cds.gode.stream.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class Sink implements Serializable
{

    private String name;
    private String entity;
    private final static long serialVersionUID = -8109456363358074492L;

}
