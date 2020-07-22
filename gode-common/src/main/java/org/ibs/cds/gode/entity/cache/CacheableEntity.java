package org.ibs.cds.gode.entity.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ibs.cds.gode.entity.type.StateEntity;

import java.io.Serializable;

public interface CacheableEntity<Id extends Serializable> extends StateEntity<Id> {
    @JsonIgnore
    @Override
    Id getId();

    @Override
    void setId(Id id);
}
