package org.ibs.cds.gode.entity.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

public interface TypicalEntity<Id extends Serializable> extends Serializable{

    @JsonIgnore
    Id getId();

    void setId(Id id) ;

    boolean isActive();

    void setActive(boolean active);

    Date getCreatedOn();

    void setCreatedOn(Date createdOn) ;

    Date getUpdatedOn();

    void setUpdatedOn(Date updatedOn);

    Long getAppId();

    void setAppId(Long appId);

    void setValidated(boolean validated);

    boolean isValidated();

    @JsonIgnore
    default String getClassifier(){
        return this == null ? null : this.getClass().getSimpleName();
    }

    @JsonIgnore
    default Set<AutoIncrementField> autoIncrementFields(){
        return Set.of(AutoIncrementField.of(this::getAppId,this::setAppId));
    }
}
