package me.ooi.tinyquery.interceptors;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.base.EntityUtils;
import me.ooi.tinyquery.base.QueryInterfaceUtils;

/**
 * @author jun.zhao
 */
public abstract class AbstractBaseQueryInterceptor implements Interceptor{
	
	private static Map<String, BaseQueryDefinition> baseQueryDefinitionCache = new HashMap<String, BaseQueryDefinition>();
	
	@Data
	public static class BaseQueryDefinition{
		private Class<?> entityClass;
		private String tableName;
	}

	@Override
	public void prepare(QueryDefinition queryDefinition) {
		BaseQueryDefinition baseQueryDefinition = new BaseQueryDefinition();
		
		Class<?> queryInterface = queryDefinition.getQueryInterface();
		Class<?> entityClass = QueryInterfaceUtils.getEntityClass(queryInterface);
		queryDefinition.setGenericReturnType(entityClass);
		baseQueryDefinition.setEntityClass(entityClass);
		
		String tableName = EntityUtils.tableName(QueryInterfaceUtils.getEntityClass(queryDefinition.getQueryInterface()));
		baseQueryDefinition.setTableName(tableName);
		
		baseQueryDefinitionCache.put(queryDefinition.getKey(), baseQueryDefinition);
	}

	public BaseQueryDefinition getBaseQueryDefinition(QueryDefinition queryDefinition) {
		BaseQueryDefinition bd = baseQueryDefinitionCache.get(queryDefinition.getKey());
		return bd;
	}
	
}
