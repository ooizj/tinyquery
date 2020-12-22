package me.ooi.tinyquery.base;

import java.util.ArrayList;
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
	
	/**
	 * copy from other pageResult(exclude “records” field)
	 * @param other
	 */
	public void copy(PageResult<?> other) {
		this.pageNumber = other.getPageNumber();
		this.pageSize = other.getPageSize();
		this.total = other.getTotal();
	}

	
}
