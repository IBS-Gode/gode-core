
package org.ibs.cds.gode.stream.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class Pipeline implements Serializable
{

    private String name;
    private Source source;
    private final static long serialVersionUID = -2658940907395455355L;

}
