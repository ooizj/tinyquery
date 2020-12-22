package me.ooi.tinyquery.base;

import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 */
public interface RecordCountGenerator {
	
	/**
	 * 获取总记录数
	 * @param context
	 * @return
	 */
	Long getRecordCount(QueryExecutionContext context);
	
}
