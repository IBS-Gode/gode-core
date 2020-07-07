package org.ibs.cds.gode.entity.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ibs.cds.gode.entity.view.EntityView;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

public interface StateEntity<Id extends Serializable> extends EntityView<Id> {

    @JsonIgnore
    Set<AutoIncrementField> getAutoIncrementFields();

    @JsonIgnore
    default AutoIncrementField[] systemIncrementFields(){
        return new AutoIncrementField[]{
                AutoIncrementField.of("appId",this::getAppId, this::setAppId),
        };
    }

    @JsonIgnore
    default Optional<AutoIncrementField[]> userIncrementFields(){
        return Optional.empty();
    }
}
