package me.ooi.tinyquery.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.annotation.Id;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class OracleIdGenerator implements IdGenerator {
	
	private Map<String, QueryDefinition> queryDefinitionCache = new HashMap<String, QueryDefinition>();
	
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
		String key = getKey(idFieldType);
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
	
	private String getKey(Class<?> idFieldType) {
		return "sequence_"+idFieldType.getName();
	}
	
	private QueryDefinition initQueryDefinition(QueryDefinition def, Class<?> idFieldType, String sequence) {
		def.setType(QueryDefinition.Type.SELECT);
		def.setQuery("select "+sequence+".nextval from dual");
		def.setReturnType(idFieldType);
		def.setGenericReturnType(idFieldType);
		def.setInterceptors(getInterceptors(def));
		
		return def;
	}
	
	/**
	 * get all of Interceptors and prepare
	 * @param queryDefinition
	 * @return
	 */
	private Interceptor[] getInterceptors(QueryDefinition queryDefinition) {
		
		List<Interceptor> allInterceptor = new ArrayList<Interceptor>();
		
		//add interceptors order by META-INF/services/me.ooi.tinyquery.Interceptor 
		for (Interceptor interceptor : ServiceRegistry.INSTANCE.getInterceptors()) {
			
			if( (!allInterceptor.contains(interceptor)) && interceptor.accept(queryDefinition) ) {
				interceptor.prepare(queryDefinition);
				allInterceptor.add(interceptor);
			}
		}
		
		Interceptor[] ret = new Interceptor[allInterceptor.size()];
		allInterceptor.toArray(ret);
		return ret;
	}

}
