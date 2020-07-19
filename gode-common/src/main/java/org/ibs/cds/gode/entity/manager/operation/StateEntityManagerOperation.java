package org.ibs.cds.gode.entity.manager.operation;

import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.pagination.PagedData;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Collectors;

public interface StateEntityManagerOperation<View extends EntityView<Id>, Entity extends TypicalEntity<Id>, Id extends Serializable> extends ViewEntityManagerOperation<View, Id> {

    View save(View view);

    View findByAppId(Long appId);

    View find(Id id);

    View transformFields(Entity entity);

    Entity transformFields(View View);

   default Optional<View> transformEntity(Optional<Entity> entityOpts){
       return entityOpts.map(this::transform);
   };

    default Optional<Entity> transformView(Optional<View> viewOpts){
        return viewOpts.map(this::transform);
    };

    default ValidationStatus validateEntity(Entity entity){
        return ValidationStatus.ok();
    }

    boolean deactivate(Id id);

    default PagedData<View> transformEntityPage(PagedData<Entity> pagedData) {
        return new PagedData(pagedData.stream().map(Optional::ofNullable).map(this::transformEntity).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()), pagedData.getContext());
    }

    default PagedData<Entity> transformViewPage(PagedData<View> pagedData) {
        return new PagedData(pagedData.stream().map(Optional::ofNullable).map(this::transformView).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()), pagedData.getContext());
    }

    default View transform(Entity entity){
        View view =transformFields(entity);
        view.setValidated(entity.isValidated());
        view.setActive(entity.isActive());
        view.setAppId(entity.getAppId());
        view.setId(entity.getId());
        view.setCreatedOn(entity.getCreatedOn());
        view.setUpdatedOn(entity.getUpdatedOn());
        return view;
    }

    default Entity transform(View view){
        Entity entity=transformFields(view);
        entity.setActive(view.isActive());
        entity.setAppId(view.getAppId());
        entity.setId(view.getId());
        entity.setCreatedOn(view.getCreatedOn());
        entity.setUpdatedOn(view.getUpdatedOn());
        return entity;
    }
}
