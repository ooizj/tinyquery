package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Method;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.QueryDefinition;

/**
 * @author jun.zhao
 */
public abstract class AbstractBaseQueryInterceptor implements Interceptor{
	
	public static final String DEF_KEY_ENTITY_CLASS = "entityClass";
	public static final String DEF_KEY_TABLE_NAME = "tableName";
	
	@Override
	public void prepare(QueryDefinition queryDefinition, Method method) {
		Class<?> queryInterface = queryDefinition.getQueryInterface();
		Class<?> entityClass = QueryInterfaceUtils.getEntityClass(queryInterface);
		queryDefinition.put(DEF_KEY_ENTITY_CLASS, entityClass);
		
		String tableName = EntityUtils.tableName(QueryInterfaceUtils.getEntityClass(queryDefinition.getQueryInterface()));
		queryDefinition.put(DEF_KEY_TABLE_NAME, tableName);
	}
	
}
