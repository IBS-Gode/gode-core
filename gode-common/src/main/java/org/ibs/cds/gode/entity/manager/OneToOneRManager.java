package org.ibs.cds.gode.entity.manager;

import org.ibs.cds.gode.entity.relationship.RelationshipType;
import org.ibs.cds.gode.entity.repo.OneToOneRelationshipRepo;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.entity.type.Relationship;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.entity.view.RelationshipView;
import org.ibs.cds.gode.util.Assert;

import java.io.Serializable;

public abstract class OneToOneRManager<RelationView extends RelationshipView<A, B>,
        Relation extends Relationship<aid, bid>,
        A extends EntityView<aid>,
        B extends EntityView<bid>,
        aid extends Serializable,
        bid extends Serializable> extends AbstractRelationshipManager<RelationView, Relation,A,B,aid,bid>{


    public <StoreRepo extends OneToOneRelationshipRepo<Relation, aid, bid>> OneToOneRManager(StoreRepo storeEntityRepo, EntityManager<A, ?, aid> asideEntityManager, EntityManager<B, ?, bid> bsideEntityManager) {
        super(storeEntityRepo, asideEntityManager, bsideEntityManager, true);
    }

    public RelationView findRelationshipFrom(A a){
        Assert.notNull("Relative details not available", a, a.getId());
        return transformEntity(repo().findByAid(a.getId())).orElse(null);
    }

    protected OneToOneRelationshipRepo<Relation, aid, bid> repo() {
        return this.repository.get();
    }

    public RelationView findRelationshipTo(B b){
        Assert.notNull("Relative details not available", b, b.getId());
        return transformEntity(repo().findByBid(b.getId())).orElse(null);
    }

    public RelationView findRelationship(A a, B b){
        Assert.notNull("Relative details not available", a, a.getId(), b, b.getId());
        return transformEntity(repo().findByAidAndBid(a.getId(), b.getId())).orElse(null);
    }

    public RelationView findAnyRelationship(A a, B b){
        Assert.notNull("Relative details not available", a, a.getId(), b, b.getId());
        return transformEntity(repo().findByAidOrBid(a.getId(), b.getId())).orElse(null);
    }

    @Override
    public RelationshipType type() {
        return RelationshipType.ONE_TO_ONE;
    }
}
