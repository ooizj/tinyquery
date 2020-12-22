package me.ooi.tinyquery.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Log4j
public class ClassUtils {
	
	public static boolean isEmpty(Type[] types) {
		return (types == null || types.length == 0);
	}
	
	public static Class<?> getClass(String className){
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
//			return Class.forName(className) ;
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		} 
		return null ; 
	}
	
	public static <T> T newInstance(Class<T> clazz){
		try {
			return clazz.newInstance() ;
		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
		return null; 
	}
	
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz){
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			return beanInfo.getPropertyDescriptors() ; 
		} catch (IntrospectionException e) {
			log.error(e.getMessage(), e);
		} 
		return null ; 
	}
	
	/**
	 * 查找类中有某注解的方法
	 * @param clzz
	 * @param annotation
	 * @return
	 */
	public static List<Method> getDeclaredMethodsByAnnotation(Class<?> clzz, Class<? extends Annotation> annotation){
		List<Method> ret = new ArrayList<Method>(1);
		for (Method method : clzz.getDeclaredMethods()) {
			if( method.isAnnotationPresent(annotation) ) {
				ret.add(method);
			}
		}
		return ret;
	}
	
	/**
	 * 获取方法返回类型的第一个泛型（如：List<String>返回String），如果不是class会产生Cast异常
	 * @param type
	 * @return
	 */
	public static Class<?> getReturnTypeFirstGenericClass(Type type){
		if( type instanceof Class<?> ) {
			return (Class<?>) type;
		}
		
		if( !(type instanceof ParameterizedType) ) {
			return null;
		}
		
		ParameterizedType pt = (ParameterizedType) type;
		Type[] argTypes = pt.getActualTypeArguments();
		if( argTypes == null || argTypes.length == 0 ) {
			return null;
		}
		return (Class<?>) argTypes[0];
	}
	
}