package org.ibs.cds.gode.entity.operation;

import com.querydsl.core.types.Predicate;
import org.ibs.cds.gode.entity.generic.DataMap;
import org.ibs.cds.gode.entity.manager.EntityManager;
import org.ibs.cds.gode.entity.manager.EntityViewManager;
import org.ibs.cds.gode.entity.manager.PureEntityManager;
import org.ibs.cds.gode.entity.store.StoreEntity;
import org.ibs.cds.gode.entity.validation.ValidationStatus;
import org.ibs.cds.gode.entity.view.EntityView;
import org.ibs.cds.gode.pagination.PageContext;
import org.ibs.cds.gode.pagination.PagedData;

import java.io.Serializable;
import java.util.function.Function;

public class Logic {

    public static <Id extends Serializable,View extends EntityView<Id>,Manager extends EntityManager<View, ?, Id>>  Function<View, Function<Manager,View>> save(){
        return view -> manager -> manager.save(view);
    }

    public static <Id extends Serializable,View extends EntityView<Id>,Manager extends EntityManager<View, ?, Id>>  Function<Id, Function<Manager,View>> findById(){
        return id -> manager -> manager.find(id);
    }

    public static <View extends EntityView<?>,Manager extends EntityManager<View, ?, ?>>  Function<Long, Function<Manager,View>> findByAppId(){
        return appId -> manager -> manager.findByAppId(appId);
    }

    public static <Id extends Serializable,View extends EntityView<Id>,Manager extends EntityManager<View, ?, Id>>  Function<Id, Function<Manager,Boolean>> deactivate(){
        return id -> manager -> manager.deactivate(id);
    }

    public static <View extends EntityView<?>,Manager extends EntityManager<View, ?, ?>>  Function<PageContext, Function<Manager, PagedData<View>>> findAll(){
        return pc -> manager -> manager.find(pc);
    }

    public static <View extends EntityView<?>,Manager extends EntityManager<View, ?, ?>>  Function<PageContext,Function<Predicate, Function<Manager,PagedData<View>>>> findAllByPredicate(){
        return pc -> predicate -> manager -> manager.find(predicate, pc);
    }

    public static <View extends EntityView<?>,Manager extends EntityViewManager<View, ?>>  Function<View, Function<Manager, DataMap>> process(){
        return view -> manager -> manager.process(view);
    }

    public static <View extends EntityView<?>,Manager extends EntityViewManager<View, ?>>  Function<View, Function<Manager, ValidationStatus>> validate(){
        return view -> manager -> manager.validateView(view);
    }

    public static <View extends EntityView<?>,Manager extends EntityManager<View, ?, ?>>  Function<PageContext, Function<Manager, PagedData<View>>> findAll0(){
        return pc -> manager -> manager.find(pc);
    }

    public static <View extends EntityView<?>,Manager extends EntityManager<View, ?, ?>>  Function<PageContext,Function<Predicate, Function<Manager,PagedData<View>>>> findAllByPredicate0(){
        return pc -> predicate -> manager -> manager.find(predicate, pc);
    }

    public static <Id extends Serializable,View extends StoreEntity<Id>, Manager extends PureEntityManager<View, Id>>  Function<View, Function<Manager,View>> savePure(){
        return view -> manager -> manager.save(view);
    }

    public static <Id extends Serializable,View extends StoreEntity<Id>,Manager extends PureEntityManager<View, Id>>  Function<Id, Function<Manager,View>> findByIdPure(){
        return id -> manager -> manager.find(id);
    }

    public static <Id extends Serializable,View extends StoreEntity<Id>,Manager extends PureEntityManager<View, Id>>  Function<Id, Function<Manager,Boolean>> deactivatePure(){
        return id -> manager -> manager.deactivate(id);
    }

    public static <View extends StoreEntity<?>,Manager extends PureEntityManager<View, ?>>  Function<PageContext, Function<Manager, PagedData<View>>> findAllPure(){
        return pc -> manager -> manager.find(pc);
    }

    public static <View extends StoreEntity<?>,Manager extends PureEntityManager<View, ?>>  Function<PageContext,Function<Predicate, Function<Manager,PagedData<View>>>> findAllByPredicatePure(){
        return pc -> predicate -> manager -> manager.find(predicate, pc);
    }
}
