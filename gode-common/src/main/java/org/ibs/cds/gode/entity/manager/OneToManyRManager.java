package org.ibs.cds.gode.entity.manager;

import org.ibs.cds.gode.entity.relationship.RelationshipType;
import org.ibs.cds.gode.entity.repo.OneToManyRelationshipRepo;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.entity.type.Relationship;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.entity.view.RelationshipView;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.ibs.cds.gode.util.Assert;

import java.io.Serializable;

public abstract class OneToManyRManager<RelationView extends RelationshipView<A, B>,
        Relation extends Relationship<aid, bid>,
        A extends EntityView<aid>,
        B extends EntityView<bid>,
        aid extends Serializable,
        bid extends Serializable> extends AbstractRelationshipManager<RelationView, Relation,A,B,aid,bid>{


    public <StoreRepo extends OneToManyRelationshipRepo<Relation, aid, bid>> OneToManyRManager(StoreRepo storeEntityRepo, EntityManager<A, ?, aid> asideEntityManager, EntityManager<B, ?, bid> bsideEntityManager) {
        super(storeEntityRepo, asideEntityManager, bsideEntityManager, true);
    }

    public PagedData<RelationView> findRelationshipFrom(A a, PageContext context){
        Assert.notNull("Relative details not available", a, a.getId());
        return transformEntityPage(repo().findByAid(a.getId(), context));
    }

    protected OneToManyRelationshipRepo<Relation, aid, bid> repo() {
        return this.repository.get();
    }

    public RelationView findRelationshipTo(B b){
        Assert.notNull("Relative details not available", b, b.getId());
        return transformEntity(repo().findByBid(b.getId())).orElse(null);
    }

    @Override
    public RelationshipType type() {
        return RelationshipType.ONE_TO_MANY;
    }
}
