package org.ibs.cds.gode.entity.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ibs.cds.gode.entity.type.AutoIncrementField;
import org.ibs.cds.gode.entity.type.StateEntity;
import org.ibs.cds.gode.util.EntityUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class StoreEntity<Id extends Serializable> implements StateEntity<Id> {

    private transient boolean validated;
    private transient final Set<AutoIncrementField> autoIncrementFields;

    public StoreEntity() {
        this.autoIncrementFields = new HashSet<>();
        this.autoIncrementFields.addAll(Arrays.asList(systemIncrementFields()));
        userIncrementFields().ifPresent(k->{
            this.autoIncrementFields.addAll(Arrays.asList(k));
        });
    }

    @JsonIgnore
    public abstract Id getId();

    public abstract void setId(Id id);
    @JsonIgnore
    public abstract IStoreType getStoreType();

    @JsonIgnore
    public boolean isValidated() {
        return validated;
    }

    @JsonIgnore
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public int hashCode() {
        return EntityUtil.hashCode(this);
    }

    @Override
    public String toString() {
        return EntityUtil.toString(this);
    }


    @JsonIgnore
    public AutoIncrementField[] systemIncrementFields(){
        return new AutoIncrementField[]{
                AutoIncrementField.of("appId",this::getAppId, this::setAppId)
        };
    }

    @JsonIgnore
    public Set<AutoIncrementField> getAutoIncrementFields() {
        return autoIncrementFields;
    }


}
