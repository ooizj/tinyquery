package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.common.base.CaseFormat;

import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.util.ReflectUtils;

/**
 * @author jun.zhao
 */
public class EntityUtils {
	
	/**
	 * get table's name by entityClass
	 * @param entityClass
	 * @return
	 */
	public static String tableName(Class<?> entityClass) {
		Table table = entityClass.getAnnotation(Table.class);
		if( table == null ) {
			throw new QueryExecutionContextBuildException("table name not found.");
		}
		return table.name();
	}
	
	/**
	 * javabean字段名转换为数据库字段名，采用驼峰转下划线的方式（如：“userId”会转为“user_id”）
	 * @param beanField
	 * @return
	 */
	public static String beanFieldToDbField(String beanField) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, beanField);
	}
	
	/**
	 * 获取不为NULL的字段
	 * @param entity
	 * @return
	 */
	public static Map<String, Object> getNotNullField(Object entity){
		return ReflectUtils.getNotNullField(entity);
	}
	
	/**
	 * 获取ID字段（标注有{@link me.ooi.tinyquery.interceptors.base.ibm.common.tinyquery.annotation.Id}注解的字段）
	 * @param entityClass
	 * @return
	 */
	public static Field getIdField(Class<?> entityClass) {
		for (Field field : entityClass.getDeclaredFields()) {
			if( field.isAnnotationPresent(Id.class) ) {
				return field;
			}
		}
		
		return null;
	}

}
