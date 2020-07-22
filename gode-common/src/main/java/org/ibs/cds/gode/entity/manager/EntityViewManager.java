package org.ibs.cds.gode.entity.manager;

import lombok.extern.slf4j.Slf4j;
import org.ibs.cds.gode.entity.generic.DataMap;
import org.ibs.cds.gode.entity.manager.operation.ViewEntityManagerOperation;
import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.entity.view.EntityView;

import java.io.Serializable;
import java.util.Optional;
import org.ibs.cds.gode.entity.function.EntityFunctionBody;

@Slf4j
public abstract class EntityViewManager<View extends EntityView<Id>, Id extends Serializable>
        implements ViewEntityManagerOperation<View, Id> {
    
    @Override
    public DataMap process(View view) {
        return processFunction().map(k->k.run(view)).orElseGet(DataMap::empty);
    }

    @Override
    public ValidationStatus validateView(View view) {
        return processFunction().map(k->k.validate(view)).orElseGet(ValidationStatus::ok);
    }

    public abstract <Function extends EntityFunctionBody<View>> Optional<Function> processFunction();

}
