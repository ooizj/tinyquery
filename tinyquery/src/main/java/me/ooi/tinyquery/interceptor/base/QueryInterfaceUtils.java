package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author jun.zhao
 */
public class QueryInterfaceUtils {
	
	private static boolean isEmpty(Type[] types) {
		return (types == null || types.length == 0);
	}
	
	/**
	 * get queryInterface's entity class
	 * @param queryInterface
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getEntityClass(Class<?> queryInterface) {
		Type[] genericInterfaces = queryInterface.getGenericInterfaces();
		if( isEmpty(genericInterfaces) ) {
			return null;
		}
		
		Type[] argTypes = ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments();
		if( isEmpty(argTypes) ) {
			return null;
		}
		
		if( !(argTypes[0] instanceof Class) ) {
			return null;
		}
		
		return (Class) argTypes[0];
	}

}
