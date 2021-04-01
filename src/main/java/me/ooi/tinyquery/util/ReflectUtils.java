package me.ooi.tinyquery.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jun.zhao
 */
public class ReflectUtils {
	
	private static Map<Class<?>, PropertyDescriptor[]> propertyDescriptorsCache = 
			new HashMap<Class<?>, PropertyDescriptor[]>();
	
	public static Object getFieldValue(Object obj, Field field) {
		if( field == null ) {
			throw new IllegalArgumentException("field is null");
		}
		
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(obj.getClass(), field.getName());
		Method readMethod = (propertyDescriptor==null)?null:propertyDescriptor.getReadMethod();
		if( readMethod != null ) {
			try {
				return propertyDescriptor.getReadMethod().invoke(obj);
			} catch (Exception e) {
				throw new PropertyOperationException(e);
			} 
		}else {
			if( !field.isAccessible() ) {
				field.setAccessible(true);
			}
			
			try {
				return field.get(obj);
			} catch (Exception e) {
				throw new PropertyOperationException(e);
			} 
		}
	}
	
	public static void setFieldValue(Object obj, Field field, Object value) {
		if( field == null ) {
			throw new IllegalArgumentException("field is null");
		}
		
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(obj.getClass(), field.getName());
		Method writeMethod = (propertyDescriptor == null) ? null : propertyDescriptor.getWriteMethod();
		if( writeMethod != null ) {
			try {
				writeMethod.invoke(obj, value);
			} catch (Exception e) {
				throw new PropertyOperationException(e);
			}
		}else {
			if( !field.isAccessible() ) {
				field.setAccessible(true);
			}
			
			try {
				field.set(obj, value);
			} catch (Exception e) {
				throw new PropertyOperationException(e);
			} 
		}
	}
	
	/**
	 * 获取不为NULL的字段
	 * @param source
	 * @return
	 */
	public static Map<String, Object> getNotNullField(Object source){
		
		Map<String, Object> map = new HashMap<String, Object>();
		PropertyDescriptor[] pds = getPropertyDescriptors(source.getClass()) ;
		for (PropertyDescriptor sourcePd : pds) {
			
			Method readMethod = sourcePd.getReadMethod();
			if( readMethod != null ){
				if(readMethod.getDeclaringClass() == Object.class ){
					continue;
				}
				
				try {
					Object value = readMethod.invoke(source, new Object[]{});
					if( value != null ){
						map.put(sourcePd.getName(), value);
					}
				} catch (Exception e) {
					throw new PropertyOperationException(e);
				}
			}
		}
		return map;
	}

	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
		if( propertyDescriptorsCache.containsKey(clazz) ) {
			return propertyDescriptorsCache.get(clazz);
		}else {
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				propertyDescriptorsCache.put(clazz, propertyDescriptors);
				return propertyDescriptors;
			} catch (Exception e) {
				throw new PropertyOperationException(e);
			}
		}
	}
	
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName) {
		PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if( fieldName.equals(propertyDescriptor.getName()) ) {
				return propertyDescriptor;
			}
		}
		return null;
	}
	
}
