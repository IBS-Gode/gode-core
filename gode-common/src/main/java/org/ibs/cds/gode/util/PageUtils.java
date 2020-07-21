package org.ibs.cds.gode.util;


import com.querydsl.core.types.Predicate;
import org.ibs.cds.gode.entity.cache.CacheableEntity;
import org.ibs.cds.gode.entity.type.TypicalEntity;
import org.ibs.cds.gode.pagination.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageUtils {

    public static <T> PagedData<T> fromPage(Page<T> page) {
        return fromPage(page, null);
    }
    public static <T> PagedData<T> fromSlice(Slice<T> slice, long totalCount)
    {
        PagedData<T> pagedData = new PagedData<T>();
        pagedData.setData(slice.getContent());
        int pageSize = slice.getPageable().getPageSize();
        ResponsePageContext ctx = new ResponsePageContext(pageSize);
        ctx.setNext(slice.hasNext());
        ctx.setPrevious(slice.hasPrevious());
        ctx.setPageNumber(slice.getPageable().getPageNumber() + 1);
        ctx.setTotalCount(totalCount);
        ctx.setTotalPages(pageSize > 0 ? totalCount/pageSize : pageSize);
        Set<Sortable> sortOrders = new HashSet<>();
        slice.getSort().forEach(order->sortOrders.add(fromSort(order)));
        ctx.setSortOrder(sortOrders);
        pagedData.setContext(new QueryContext(ctx, null));
        return pagedData;
    }

    public static <T> PagedData<T> fromPage(Page<T> page, Predicate predicate) {
        PagedData<T> pagedData = new PagedData<T>();
        pagedData.setData(page.getContent());
        ResponsePageContext ctx = new ResponsePageContext(page.getPageable().getPageSize());
        ctx.setNext(page.hasNext());
        ctx.setPrevious(page.hasPrevious());
        ctx.setPageNumber(page.getPageable().getPageNumber() + 1);
        ctx.setTotalCount(page.getTotalElements());
        ctx.setTotalPages(page.getTotalPages());
        Set<Sortable> sortOrders = new HashSet<>();
        page.getSort().forEach(order->sortOrders.add(fromSort(order)));
        ctx.setSortOrder(sortOrders);
        pagedData.setContext(new QueryContext(ctx, predicate == null ? null : predicate.toString()));
        return pagedData;
    }

    private static Sortable fromSort(Sort.Order order){
        switch (order.getDirection()){
            case ASC: return Sortable.by(Sortable.Type.ASC, order.getProperty());
            case DESC: return Sortable.by(Sortable.Type.DESC, order.getProperty());
            default: return Sortable.by(order.getProperty());
        }
    }

    public static <T extends TypicalEntity<?>> PagedData<T> getData(Function<PageRequest, Page<T>> function, PageContext ctx) {
        return fromPage(function.apply(toBaseRequest(ctx)));
    }

    public static <T extends TypicalEntity<?>> PagedData<T> getData(Function<PageRequest, Slice<T>> function, long totalCount ,PageContext ctx) {
        return fromSlice(function.apply(toBaseRequest(ctx)), totalCount);
    }

    public static <T extends TypicalEntity<?>> PagedData<T> getData(Function<PageRequest, Page<T>> function, PageContext ctx, Predicate predicate) {
        return fromPage(function.apply(toBaseRequest(ctx)),predicate);
    }

    public static <T extends TypicalEntity<?>,R> PagedData<R> transform(PagedData<T> data, Function<T,R> transformer) {
        return new PagedData(data.stream().map(transformer).collect(Collectors.toList()), data.getContext());
    }

    public static <T extends TypicalEntity<?>,O> PagedData<O> getData(Function<PageRequest, Page<T>> function, PageContext ctx, Function<T,O> map) {
        return fromPage(function.apply(toBaseRequest(ctx)).map(map));
    }

    public static PageRequest toBaseRequest(PageContext context) {
        int pageNo = context.getPageNumber() - 1;
        return context.getSortOrder() == null ?
                PageRequest.of(pageNo < 0 ? 0 : pageNo, context.getPageSize()) :
                PageRequest.of(pageNo < 0 ? 0 : pageNo, context.getPageSize(), toBaseSort(context.getSortOrder()));
    }


    public static Sort.Direction toBaseSortDirection(Sortable.Type sortType) {
        switch (sortType){
            case ASC: default: return Sort.Direction.ASC;
            case DESC: return Sort.Direction.DESC;
        }

    }

    public static Sort toBaseSort(Set<Sortable> sortOrders) {
        Sort sort = null;
        for (Sortable sortOrder: sortOrders) {
                sort = sort == null ?
                        Sort.by(toBaseSortDirection(sortOrder.getSortType()), sortOrder.getField()) :
                        sort.and(Sort.by(toBaseSortDirection(sortOrder.getSortType()), sortOrder.getField()));
        }
        return sort;
    }


    public static boolean isEmpty(PagedData<?> page) {
        return page == null || CollectionUtils.isEmpty(page.getData());
    }

    public static <T> PagedData<T> emptyPage(){
        return new PagedData<>();
    }

    public static <T extends CacheableEntity<?>> PagedData<T> getData(Iterable<T> data, long totalCount, PageContext context){
        PagedData<T> page = new PagedData<>();
        int pageSize = context.getPageSize();
        int pageNo = context.getPageNumber();
        List<T> listOfData = StreamUtils.from(data, (pageNo - 1) * pageSize, pageSize).collect(Collectors.toList());
        page.setData(listOfData);
        ResponsePageContext pageContext = new ResponsePageContext(context);
        pageContext.setTotalCount(totalCount);
        pageContext.setTotalPages(totalCount/pageSize);
        pageContext.setNext(pageNo*pageSize < totalCount);
        pageContext.setPrevious(pageNo-1 > 0 && (pageNo-1)*pageSize < totalCount);
        page.setContext(new QueryContext(pageContext, null));
        return page;
    }

}
