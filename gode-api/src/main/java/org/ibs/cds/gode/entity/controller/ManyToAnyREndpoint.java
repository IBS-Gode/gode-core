package org.ibs.cds.gode.entity.controller;

import io.swagger.annotations.ApiOperation;
import org.ibs.cds.gode.entity.generic.AB;
import org.ibs.cds.gode.entity.manager.ManyToAnyRManager;
import org.ibs.cds.gode.entity.operation.Executor;
import org.ibs.cds.gode.entity.type.Relationship;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.entity.view.RelationshipView;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.ibs.cds.gode.util.APIArgument;
import org.ibs.cds.gode.web.Request;
import org.ibs.cds.gode.web.Response;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;

public abstract class ManyToAnyREndpoint<View extends RelationshipView<A,B>, Entity extends Relationship<aid,bid>, Manager extends ManyToAnyRManager<View, Entity, A,B,aid,bid>, A extends EntityView<aid>, B extends EntityView<bid>,aid extends Serializable, bid extends Serializable>
 extends EntityStateEndPoint<View, Entity,Manager,Long>{

    private ManyToAnyRManager<View, Entity, A,B,aid,bid> manager;
    public ManyToAnyREndpoint(Manager manager) {
        super(manager);
        this.manager = manager;
    }

    @PostMapping(path="/from")
    @ApiOperation("Find relationship from entity")
    public Response<PagedData<View>> findRelationshipFrom(@RequestBody Request<A> asideRequest, @ModelAttribute APIArgument argument){
        return Executor.run((Request<A> a)->(ManyToAnyRManager<View, Entity, A,B,aid,bid> m)->m.findRelationshipFrom(a.getData(), PageContext.fromAPI(argument)), asideRequest ,manager, KnownException.QUERY_FAILED, "/from");
    }

    @PostMapping(path="/to")
    @ApiOperation("Find relationship to entity")
    public Response<PagedData<View>> findRelationshipTo(@RequestBody Request<B> bsideRequest, @ModelAttribute APIArgument argument){
        return Executor.run((Request<B> a)->(ManyToAnyRManager<View, Entity, A,B,aid,bid> m)->m.findRelationshipTo(a.getData(), PageContext.fromAPI(argument)), bsideRequest ,manager, KnownException.QUERY_FAILED, "/to");
    }

    @PostMapping(path="/between")
    @ApiOperation("Find relationship between entities")
    public Response<View> findRelationship(@RequestBody Request<AB<A,B>> absideRequest){
        return Executor.run((Request<AB<A,B>> a)->(ManyToAnyRManager<View, Entity, A,B,aid,bid> m)->m.findRelationship(a.getData().getA(), a.getData().getB()), absideRequest ,manager, KnownException.QUERY_FAILED, "/relation");
    }
}
