package me.ooi.tinyquery.interceptor.base;

import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 */
public interface Paging {
	
	void setPagingParams(QueryExecutionContext context);
	
	String getPagingQuery(String query);
	
	String getCountQuery(String query);

}
