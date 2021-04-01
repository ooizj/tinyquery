package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Field;

/**
 * @author jun.zhao
 */
public interface IdGenerator {
	
	/**
	 * 是否在新增之前生成ID
	 * @return true：在新增之前生成ID；false：在新增之后生成ID
	 */
	boolean isGenerateIdBeforeInsert();
	
	/**
	 * generate ID
	 * @param entity
	 * @param idField
	 * @return
	 */
	Object generateId(Object entity, Field idField);

}
