package me.ooi.tinyquery.interceptor.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jun.zhao
 */
public class PageResult<E> extends ArrayList<E>{
	
	private static final long serialVersionUID = 1L;
	
	@Getter @Setter private Integer pageNumber;
	@Getter @Setter private Integer pageSize;
	
	@Getter @Setter private Long total;
	
	public PageResult() {
	}
	
	public PageResult(Collection<? extends E> c) {
		super(c);
		setPageNumber(1);
		setPageSize(c.size());
		setTotal((long) c.size());
	}
	
	public void setPage(Page page) {
		this.pageNumber = page.getPageNumber();
		this.pageSize = page.getPageSize();
	}

	public void setRecords(List<E> records) {
		this.clear();
		this.addAll(records);
	}
	
	public List<E> getRecords() {
		return this;
	}
	
}
