package org.ibs.cds.gode.entity.manager;

import org.ibs.cds.gode.entity.relationship.RelationshipType;
import org.ibs.cds.gode.entity.repo.ManyToAnyRelationshipRepo;
import org.ibs.cds.gode.entity.type.Relationship;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.entity.view.RelationshipView;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.ibs.cds.gode.util.Assert;

import java.io.Serializable;

public abstract class ManyToAnyRManager<RelationView extends RelationshipView<A, B>,
        Relation extends Relationship<aid, bid>,
        A extends EntityView<aid>,
        B extends EntityView<bid>,
        aid extends Serializable,
        bid extends Serializable> extends AbstractRelationshipManager<RelationView, Relation,A,B,aid,bid>{


    public <StoreRepo extends ManyToAnyRelationshipRepo<Relation, aid, bid>> ManyToAnyRManager(StoreRepo storeEntityRepo, EntityManager<A, ?, aid> asideEntityManager, EntityManager<B, ?, bid> bsideEntityManager) {
        super(storeEntityRepo, asideEntityManager, bsideEntityManager);
    }

    public PagedData<RelationView> findRelationshipFrom(A a, PageContext context){
        Assert.notNull("Relative details not available", a, a.getId());
        return transformEntityPage(repo().findByAid(a.getId(), context));
    }

    protected ManyToAnyRelationshipRepo<Relation, aid, bid> repo() {
        return this.repository.get();
    }

    public PagedData<RelationView> findRelationshipTo(B b, PageContext context){
        Assert.notNull("Relative details not available", b, b.getId());
        return transformEntityPage(repo().findByBid(b.getId(), context));
    }

    @Override
    public RelationshipType type() {
        return RelationshipType.ONE_TO_MANY;
    }
}
