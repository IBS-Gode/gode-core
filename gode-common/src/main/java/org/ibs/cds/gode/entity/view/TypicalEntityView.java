package org.ibs.cds.gode.entity.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public abstract class TypicalEntityView<Id extends Serializable> implements EntityView<Id> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createdOn;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date updatedOn;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long appId;
    private boolean active;
    public transient boolean validated;
    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public Date getUpdatedOn() {
        return updatedOn;
    }

    @Override
    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public Long getAppId() {
        return appId;
    }

    @Override
    public void setAppId(Long appId) {
        this.appId = appId;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override @JsonIgnore
    public boolean isValidated() {
        return validated;
    }

    @Override
    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
