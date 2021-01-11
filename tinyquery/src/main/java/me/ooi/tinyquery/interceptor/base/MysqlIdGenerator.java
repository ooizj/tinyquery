package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.interceptor.InterceptorUtils;

/**
 * @author jun.zhao
 */
public class MysqlIdGenerator implements IdGenerator {
	
	private Map<String, QueryDefinition> queryDefinitionCache = new ConcurrentHashMap<String, QueryDefinition>();

	@Override
	public boolean isGenerateIdBeforeInsert() {
		return false;
	}
	
	@Override
	public Object generateId(Object entity, Field idField) {
		try {
			
			Invocation invocation = new Invocation(ServiceRegistry.INSTANCE.getQueryExecutor(), idGeneratorExecutionContext(idField));
			return invocation.invoke();
			
		} catch (Throwable t) {
			throw new QueryExecutionException(t);
		}
	}
	
	private QueryExecutionContext idGeneratorExecutionContext(Field idField) {
		Class<?> idFieldType = idField.getType();
		String key = getKey(idFieldType);
		QueryDefinition def = queryDefinitionCache.get(key);
		if( def == null ) {
			def = new QueryDefinition();
			def.setKey(key);
			initQueryDefinition(def, idFieldType);
        	queryDefinitionCache.put(key, def) ;
		}
		
		QueryExecutionContext idGeneratorExecutionContext = new QueryExecutionContext();
		idGeneratorExecutionContext.setQueryDefinition(def);
		Object[] arguments = new Object[0];
		idGeneratorExecutionContext.setArgs(arguments);
		idGeneratorExecutionContext.setQuery(def.getQuery());
		return idGeneratorExecutionContext;
	}
	
	private String getKey(Class<?> idFieldType) {
		return "last_insert_id_"+idFieldType.getName();
	}
	
	private QueryDefinition initQueryDefinition(QueryDefinition def, Class<?> idFieldType) {
		def.setType(QueryDefinition.Type.SELECT);
		def.setQuery("SELECT LAST_INSERT_ID()");
		def.setReturnType(idFieldType);
		def.setGenericReturnClass(idFieldType);
		def.setInterceptors(InterceptorUtils.getInterceptors(def, null));
		return def;
	}
	
}
