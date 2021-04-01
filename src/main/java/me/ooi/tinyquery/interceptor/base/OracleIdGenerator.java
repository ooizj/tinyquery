package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.interceptor.InterceptorUtils;

/**
 * @author jun.zhao
 */
public class OracleIdGenerator implements IdGenerator {
	
	private Map<String, QueryDefinition> queryDefinitionCache = new ConcurrentHashMap<String, QueryDefinition>();
	
	@Override
	public boolean isGenerateIdBeforeInsert() {
		return true;
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
		String sequence = idField.getAnnotation(Id.class).sequence();
		if( StringUtils.isEmpty(sequence) ) {
			throw new QueryExecutionContextBuildException("sequence is null");
		}
		
		Class<?> idFieldType = idField.getType();
		String key = getKey(sequence, idFieldType);
		QueryDefinition def = queryDefinitionCache.get(key);
		if( def == null ) {
			def = new QueryDefinition();
			def.setKey(key);
			initQueryDefinition(def, idFieldType, sequence);
        	queryDefinitionCache.put(key, def) ;
		}
		
		QueryExecutionContext idGeneratorExecutionContext = new QueryExecutionContext();
		idGeneratorExecutionContext.setQueryDefinition(def);
		Object[] arguments = new Object[0];
		idGeneratorExecutionContext.setArgs(arguments);
		idGeneratorExecutionContext.setQuery(def.getQuery());
		return idGeneratorExecutionContext;
	}
	
	private String getKey(String sequence, Class<?> idFieldType) {
		return "sequence_"+sequence+"@"+idFieldType.getName();
	}
	
	private QueryDefinition initQueryDefinition(QueryDefinition def, Class<?> idFieldType, String sequence) {
		def.setType(QueryDefinition.Type.SELECT);
		def.setQuery("select "+sequence+".nextval from dual");
		def.setReturnType(idFieldType);
		def.setGenericReturnClass(idFieldType);
		def.setInterceptors(InterceptorUtils.getInterceptors(def, null));
		return def;
	}

}
