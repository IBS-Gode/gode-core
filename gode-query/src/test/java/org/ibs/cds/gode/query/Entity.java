package org.ibs.cds.gode.query;

import org.ibs.cds.gode.entity.query.model.QueryConfig;

public class Entity extends QueryConfig<Entity> {

    private Long id;
    private String name;
    private String std;
    private String mark;

    public Entity() {
        super(Entity.class);
    }

}
