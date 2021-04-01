package me.ooi.tinyquery.interceptor.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.interceptor.InterceptorUtils;
import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 */
public abstract class AbstractTotalGenerator implements TotalGenerator{
	
	private Map<String, QueryDefinition> queryDefinitionCache = new ConcurrentHashMap<String, QueryDefinition>();
	
	@Override
	public Long getTotal(QueryExecutionContext context) {
		try {
			
			Invocation invocation = new Invocation(ServiceRegistry.INSTANCE.getQueryExecutor(), countQueryExecutionContext(context));
			return (Long) TypeConvertUtils.convert(invocation.invoke(), Long.class);
			
		} catch (Throwable t) {
			throw new QueryExecutionException(t);
		}
	}
	
	private QueryExecutionContext countQueryExecutionContext(QueryExecutionContext context) {
		
		String useQuery = context.getQuery();
		String key = getKey(useQuery);
		QueryDefinition def = queryDefinitionCache.get(key);
		if( def == null ) {
			def = new QueryDefinition();
			def.setKey(key);
			initQueryDefinition(def, useQuery);
			queryDefinitionCache.put(key, def);
		}
		
		QueryExecutionContext pageResultGeneratorExecutionContext = new QueryExecutionContext();
		pageResultGeneratorExecutionContext.setQueryDefinition(def);
		pageResultGeneratorExecutionContext.setQuery(def.getQuery());
		pageResultGeneratorExecutionContext.setArgs(context.getArgs());
		return pageResultGeneratorExecutionContext;
	}
	
	private String getKey(String useQuery) {
		return "countquery_"+useQuery;
	}
	
	private QueryDefinition initQueryDefinition(QueryDefinition def, String useQuery) {
		String countQuery = ServiceRegistry.INSTANCE.getPaging().getCountQuery(useQuery);
		
		def.setType(QueryDefinition.Type.SELECT);
		def.setQuery(countQuery);
		def.setReturnType(Long.class);
		def.setGenericReturnClass(Long.class);
		def.setInterceptors(InterceptorUtils.getInterceptors(def, null));
		return def;
	}
}
