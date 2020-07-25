package org.ibs.cds.gode.entity.controller;

import io.swagger.annotations.ApiOperation;
import org.ibs.cds.gode.entity.manager.EntityManager;
import org.ibs.cds.gode.entity.operation.Executor;
import org.ibs.cds.gode.entity.operation.Logic;
import org.ibs.cds.gode.entity.type.StateEntity;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;
import org.ibs.cds.gode.util.APIArgument;
import org.ibs.cds.gode.web.Request;
import org.ibs.cds.gode.web.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;

public class EntityStateEndPoint<View extends EntityView<Id>, Entity extends StateEntity<Id>, Manager extends EntityManager<View, Entity, Id>, Id extends Serializable>
        extends EntityProcessEndpoint<View, Manager, Id> {

    private static final String SAVE = "/save";
    private static final String DEACTIVATE = "/deactivate";
    private static final String FIND = "/find";
    private static final String FIND_ALL = "/findAll";
    protected Manager manager;
    public EntityStateEndPoint(Manager manager) {
        super(manager);
        this.manager = manager;
    }

    @PostMapping(path= SAVE)
    @ApiOperation("Create/Update entity")
    public Response<View> save(@RequestBody Request<View> request) {
        return Executor.run(Logic.save(), request, manager, KnownException.SAVE_FAILED, SAVE);
    }

    @PostMapping(path= DEACTIVATE)
    @ApiOperation("Deactivate entity")
    public Response<Boolean> deactivate(@RequestBody Request<Id> request) {
        return Executor.run(Logic.deactivate(), request, manager, KnownException.SAVE_FAILED, DEACTIVATE);
    }

    @GetMapping(path= FIND)
    @ApiOperation("Find entity by id")
    public Response<View> find(Id request) {
        return Executor.run(Logic.findById(), request, manager, KnownException.QUERY_FAILED, FIND);
    }

    @GetMapping(path= FIND_ALL)
    @ApiOperation("Find all entity instances")
    public Response<PagedData<View>> findAll(@ModelAttribute APIArgument argument) {
        return Executor.run(Logic.findAll0(), PageContext.fromAPI(argument), manager, KnownException.QUERY_FAILED, FIND_ALL);
    }

}
