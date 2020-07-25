
package org.ibs.cds.gode.entity.query.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Compose implements Serializable
{

    private List<Where> where;
    private final static long serialVersionUID = -4410846845607333661L;

    public Compose() {
        this.where = new ArrayList();
    }
}
