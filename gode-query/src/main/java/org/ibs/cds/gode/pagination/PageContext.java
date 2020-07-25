package org.ibs.cds.gode.pagination;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ibs.cds.gode.util.APIArgument;

import java.util.HashSet;
import java.util.Set;

public class PageContext {

	private int pageNumber;
	private final int pageSize;
	private Set<Sortable> sortOrder;

	public static PageContext of(int pageNumber,int pageSize) {
		PageContext context=new PageContext(pageSize);
		context.setPageNumber(pageNumber);
		return context;
	}

	public static PageContext of(int pageNumber,int pageSize, Set<Sortable> sortOrderSet) {
		PageContext context=new PageContext(pageSize);
		context.setPageNumber(pageNumber);
		context.setSortOrder(sortOrderSet);
		return context;
	}

	public static PageContext of(int pageNumber,int pageSize, Sortable sortOrder) {
		PageContext context=new PageContext(pageSize);
		context.setPageNumber(pageNumber);
		context.addSortOrder(sortOrder);
		return context;
	}

	public static final PageContext std() {
		return PageContext.of(1, 10);
	}

	public PageContext(int pageSize){
		if(pageSize < 1) pageSize = 10;
		this.pageSize=pageSize;
	}

	public Set<Sortable> getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Set<Sortable> sortOrder) {
		this.sortOrder = sortOrder;
	}

	@JsonIgnore
	public void addSortOrder(Sortable sortOrder) {
		if(this.sortOrder == null)  this.sortOrder = new HashSet<>();
		this.sortOrder.add(sortOrder);
	}
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		if(pageNumber < 1) pageNumber = 1;
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public PageContext next(){
		return PageContext.of(this.pageNumber+1, this.pageSize);
	}

	public PageContext previous(){
		return this.pageNumber < 2  ? this : PageContext.of(this.pageNumber-1, this.pageSize) ;
	}

	public static PageContext fromAPI(APIArgument argument){
		PageContext context = PageContext.of(argument.getPageNumber(), argument.getPageSize());
		if(argument.getSortBy() != null) context.setSortOrder(Set.of(new Sortable(argument.getSortOrder(),argument.getSortBy())));
		return context;
	}


}
