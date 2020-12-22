package me.ooi.tinyquery.base;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jun.zhao
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PageResult<E> extends ArrayList<E>{
	
	private static final long serialVersionUID = 1L;
	
	private Integer pageNumber;
	private Integer pageSize;
	
	private Long total;
	private List<E> records;
	
	public void setPage(Page page) {
		this.pageNumber = page.getPageNumber();
		this.pageSize = page.getPageSize();
	}
	
}
