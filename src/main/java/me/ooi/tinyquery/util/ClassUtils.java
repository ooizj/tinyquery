package me.ooi.tinyquery.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

/**
 * @author jun.zhao
 */
@Log4j
public class ClassUtils {
	
	/**
	 * 根据类名获取类
	 * @param className
	 * @return 不存在则返回null，不抛出异常
	 */
	public static Class<?> getClass(String className){
		try {
			return Class.forName(className, true, Thread.currentThread().getContextClassLoader()) ;
		} catch (ClassNotFoundException e) {
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
	 * 判断方法参数是否包含某注解
	 * @param method
	 * @param annatationClass
	 * @return
	 */
	public static boolean hasAnnotation(Method method, Class<? extends Annotation> annatationClass) {
		Annotation[][] annotationss = method.getParameterAnnotations();
		if( annotationss == null ) {
			return false;
		}
		for (Annotation[] annotations : annotationss) {
			if( annotations == null ) {
				continue;
			}
			for (Annotation annotation : annotations) {
				if( annotation != null && annatationClass.isInstance(annotation) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断参数中是否包含某类型参数
	 * @param method
	 * @param clazz
	 * @return
	 */
	public static boolean hasParamType(Method method, Class<?> clazz) {
		Class<?>[] paramTypes = method.getParameterTypes();
		if( paramTypes == null ) {
			return false;
		}
		for (Class<?> paramType : paramTypes) {
			if( clazz == paramType ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取方法某类型的参数的位置
	 * @param method
	 * @param clazz
	 * @return -1 if not found
	 */
	public static int getParamTypeIndex(Method method, Class<?> clazz) {
		final int NOT_FOUND = -1;
		Class<?>[] paramTypes = method.getParameterTypes();
		if( paramTypes == null ) {
			return NOT_FOUND;
		}
		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];
			if( clazz == paramType ) {
				return i;
			}
		}
		return NOT_FOUND;
	}
	
	/**
	 * 获取方法的注解
	 * @param method
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T getAnnotationByIndex(Method method, int index, Class<T> annatationClass) {
		Annotation[][] annotationss = method.getParameterAnnotations();
		if( annotationss == null || annotationss.length <= index ) {
			return null;
		}
		
		Annotation[] annotations = annotationss[index];
		if( annotations == null ) {
			return null;
		}
		for (Annotation annotation : annotations) {
			if( annotation != null && annatationClass.isInstance(annotation) ) {
				return (T) annotation;
			}
		}
		return null;
	}
	
}