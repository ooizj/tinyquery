package me.ooi.tinyquery;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jun.zhao
 */
public class QueryDefinitionManager {
	
	private static Map<String, QueryDefinition> queryDefinitionCache = new ConcurrentHashMap<String, QueryDefinition>();
	
	public static QueryDefinition getQueryDefinition(Class<?> queryInterface, Method method) {
		String key = getKey(queryInterface, method) ;
		QueryDefinition def = queryDefinitionCache.get(key);
		if( def == null ) {
			def = new QueryDefinition();
			def.setKey(key);
			new QueryDefinitionProvider(queryInterface, method).init(def);
        	queryDefinitionCache.put(key, def) ;
		}
		return def;
	}
	
	public static String getKey(Class<?> queryInterface, Method method) {
		return queryInterface.getName()+"."+method.getName();
	}
	
}
