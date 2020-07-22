package org.ibs.cds.gode.entity.manager;

import org.apache.commons.lang3.tuple.Pair;
import org.ibs.cds.gode.entity.relationship.RelationshipType;
import org.ibs.cds.gode.entity.repo.OneToManyRelationshipRepo;
import org.ibs.cds.gode.entity.repo.OneToOneRelationshipRepo;
import org.ibs.cds.gode.entity.repo.RelationshipRepo;
import org.ibs.cds.gode.entity.type.Relationship;
import org.ibs.cds.gode.entity.validation.ValidationFailed;
import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.entity.view.RelationshipView;
import org.ibs.cds.gode.status.BinaryStatus;
import org.ibs.cds.gode.util.Assert;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;

public abstract class AbstractRelationshipManager<RelationView extends RelationshipView<A, B>,
        Relation extends Relationship<aid, bid>,
        A extends EntityView<aid>,
        B extends EntityView<bid>,
        aid extends Serializable,
        bid extends Serializable>
        extends EntityManager<RelationView, Relation, Long>
        implements RelationshipManager<A,B,RelationView,Relation,aid,bid>  {

    private EntityManager<A, ?, aid> asideEntityManager;
    private EntityManager<B, ?, bid> bsideEntityManager;

    public <StoreRepo extends RelationshipRepo<Relation, aid,bid>> AbstractRelationshipManager(StoreRepo storeEntityRepo, EntityManager<A, ?, aid> asideEntityManager, EntityManager<B, ?, bid> bsideEntityManager) {
        super(storeEntityRepo, null);
        this.asideEntityManager = asideEntityManager;
        this.bsideEntityManager = bsideEntityManager;
    }

    @Override
    public RelationView transformRelationshipFields(Relation relation) {
        return this.transformFields(relation);
    }

    @Override
    public Relation transformRelationshipFields(RelationView relationship) {
        return this.transformFields(relationship);
    }

    @Override
    public Pair<A, B> findAsideAndBside(Relation relation) {
        return Pair.of(asideEntityManager.find(relation.getAid()), bsideEntityManager.find(relation.getBid()));
    }

    @Override
    public ValidationStatus validateView(RelationView view) {
        ValidationStatus superStatus = super.validateView(view);
        ValidationStatus validationStatus = relationshipValidation(view);
        if(superStatus.getStatus().isSuccess()){
            return validationStatus;
        }
        superStatus.getErrors().addAll(validationStatus.getErrors());
        return superStatus;
    }

    @Override
    public ValidationStatus relationshipValidation(RelationView view) {

        if(view.getBside() == null || view.getAside() == null || view.getBside().getId() == null || view.getAside().getId() == null){
            return noRelativeDetails("Relative details are not available");
        }
        Pair<aid, bid> asideAndBside = findAsideAndBside(view);
        A a = asideEntityManager.find(asideAndBside.getLeft());
        B b = bsideEntityManager.find(asideAndBside.getRight());
        if(a == null || b == null){
            return noRelativeDetails("Relative details are not saved");
        }
        return relationshipIntegrityValidation(asideAndBside);
    }

    @Override
    public abstract RelationshipType type() ;

    @NotNull
    private ValidationStatus noRelativeDetails(String s) {
        ValidationStatus validationStatus = new ValidationStatus(BinaryStatus.FAILURE);
        validationStatus.addError(new ValidationFailed("relationship", s));
        return validationStatus;
    }

    @NotNull
    protected ValidationStatus relationshipIntegrityValidation(Pair<aid, bid> ids) {
        RelationshipType type = type();
        switch (type){
            case ONE_TO_ONE: {
                OneToOneRelationshipRepo<Relation,aid,bid> repo = this.repository.get();
                Optional<Relation> relationOptional = repo.findByAidOrBid(ids.getLeft(), ids.getRight());
                return relationOptional
                        .filter(relation -> !(relation.getAid().equals(ids.getLeft()) && relation.getBid().equals(ids.getRight())))
                        .map(relation -> noRelativeDetails("One to One relationship integrity violation"))
                        .orElse(ValidationStatus.ok());
            }case ONE_TO_MANY:{
                OneToManyRelationshipRepo<Relation,aid,bid> repo = this.repository.get();
                Optional<Relation> relationOptional = repo.findByBid(ids.getRight());
                return relationOptional
                        .filter(relation -> relation != null && !relation.getAid().equals(ids.getLeft()))
                        .map(k->noRelativeDetails("One to Many relationship integrity violation"))
                        .orElse(ValidationStatus.ok());
            }case MANY_TO_ANY: default: {
                return ValidationStatus.ok();
            }

        }
    }

    public RelationView findRelationship(A a, B b){
        Assert.notNull("Relative details not available", a, a.getId(), b, b.getId());
        RelationshipRepo<Relation, aid,bid> repo = this.repository.get();
        return transformEntity(repo.findByAidAndBid(a.getId(), b.getId())).orElse(null);
    }

    @Override
    public Optional<RelationView> transformEntity(Optional<Relation> relationOptional) {
        return transformRelation(relationOptional);
    }

    @Override
    public Optional<Relation> transformView(Optional<RelationView> entity) {
        return transformRelationView(entity);
    }
}
