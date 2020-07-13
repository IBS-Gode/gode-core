package org.ibs.cds.gode.entity.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ibs.cds.gode.entity.type.AutoIncrementField;
import org.ibs.cds.gode.util.EntityUtil;

import java.io.Serializable;
import java.util.*;

public abstract class CacheEntity<Id extends Serializable> implements CacheableEntity<Id> {

    public transient boolean validated;

    private Date createdOn;
    private Date updatedOn;
    private Long appId;
    private boolean active;

    private transient volatile Set<AutoIncrementField> autoIncrementFields;

    public CacheEntity() {
        this.autoIncrementFields = new HashSet<>();
        this.autoIncrementFields.addAll(Arrays.asList(this.systemIncrementFields()));
        this.userIncrementFields().ifPresent(k->{
            this.autoIncrementFields.addAll(Arrays.asList(k));
        });
    }
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    @Override
    public int hashCode() {
        return EntityUtil.hashCode(this);
    }

    @Override
    public String toString() {
        return EntityUtil.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheEntity<?> that = (CacheEntity<?>) o;
        return validated == that.validated &&
                active == that.active &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(updatedOn, that.updatedOn) &&
                Objects.equals(appId, that.appId);
    }

    @JsonIgnore
    public boolean isValidated() {
        return validated;
    }

    @JsonIgnore
    public void setValidated(boolean validated) {
        this.validated = validated;
    }


    @JsonIgnore
    public Set<AutoIncrementField> getAutoIncrementFields() {
        return autoIncrementFields;
    }
}
