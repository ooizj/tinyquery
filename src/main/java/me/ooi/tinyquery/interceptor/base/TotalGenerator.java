package me.ooi.tinyquery.interceptor.base;

import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 */
public interface TotalGenerator {
	
	/**
	 * get total
	 * @param context
	 * @return
	 */
	Long getTotal(QueryExecutionContext context);
	
}
