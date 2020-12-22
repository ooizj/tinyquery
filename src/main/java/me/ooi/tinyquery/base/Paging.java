package me.ooi.tinyquery.base;

import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface Paging {
	
	void setPagingParams(QueryExecutionContext context);
	
	String getPagingQuery(String query);
	
	String getCountQuery(String query);

}
