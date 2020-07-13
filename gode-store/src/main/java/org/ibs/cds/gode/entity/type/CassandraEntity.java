package org.ibs.cds.gode.entity.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.entity.store.StoreType;
import org.ibs.cds.gode.util.EntityUtil;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@MappedSuperclass
public abstract class CassandraEntity<Id extends Serializable> extends StoreEntity<Id> {
    private Date createdOn;
    private Date updatedOn;
    private Long appId;
    private boolean active;

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
        CassandraEntity<?> that = (CassandraEntity<?>) o;
        return validated == that.validated &&
                active == that.active &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(updatedOn, that.updatedOn) &&
                Objects.equals(appId, that.appId);
    }

    @Override @JsonIgnore
    public StoreType getStoreType() {
        return StoreType.CASSANDRA;
    }
}
