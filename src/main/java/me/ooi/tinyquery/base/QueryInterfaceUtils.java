package me.ooi.tinyquery.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import me.ooi.tinyquery.util.ClassUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class QueryInterfaceUtils {
	
	/**
	 * get queryInterface's entity class
	 * @param queryInterface
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getEntityClass(Class<?> queryInterface) {
		Type[] genericInterfaces = queryInterface.getGenericInterfaces();
		if( ClassUtils.isEmpty(genericInterfaces) ) {
			return null;
		}
		
		Type[] argTypes = ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments();
		if( ClassUtils.isEmpty(argTypes) ) {
			return null;
		}
		
		if( !(argTypes[0] instanceof Class) ) {
			return null;
		}
		
		return (Class) argTypes[0];
	}

}
