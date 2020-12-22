package me.ooi.tinyquery.util;

import java.lang.reflect.Field;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jun.zhao
 */
@Slf4j
public class ReflectUtils {
	
	public static Object getFieldValue(Object obj, Field field) {
		if( field == null ) {
			throw new IllegalArgumentException("field is null");
		}
		
		if( !field.isAccessible() ) {
			field.setAccessible(true);
		}
		
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}

}
