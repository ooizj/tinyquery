package me.ooi.tinyquery;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import me.ooi.tinyquery.base.BaseQueryDefinitionProvider;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class QueryDefinitionManager {
	
	private static Map<String, QueryDefinition> queryDefinitionCache = new HashMap<String, QueryDefinition>();
	
	public static QueryDefinition getQueryDefinition(Class<?> queryInterface, Method method) {
		String key = getKey(queryInterface, method) ;
		QueryDefinition obj = queryDefinitionCache.get(key);
		if( obj == null ) {
			obj = createQueryDefinition(queryInterface, method);
        	queryDefinitionCache.put(key, obj) ;
		}
		return obj;
	}
	
	public static String getKey(Class<?> queryInterface, Method method) {
		return queryInterface.getName()+"."+method.getName();
	}
	
	private static QueryDefinition createQueryDefinition(Class<?> queryInterface, Method method) {
		QueryDefinition def = new QueryDefinition();
		new BaseQueryDefinitionProvider(queryInterface, method).init(def);
		return def;
	}

}
