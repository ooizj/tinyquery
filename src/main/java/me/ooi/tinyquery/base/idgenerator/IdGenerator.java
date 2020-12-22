package me.ooi.tinyquery.base.idgenerator;

import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface IdGenerator {
	
	/**
	 * 生成ID
	 * @param context
	 * @return 如果在执行insert之前获取ID，则返回获取的ID；<br>
	 * 如果在insert之后获取ID，则返回NULL
	 */
	Object generateId(QueryExecutionContext context);

}
