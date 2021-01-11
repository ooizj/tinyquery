package me.ooi.tinyquery.dbutils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ooi.tinyquery.Configuration;
import me.ooi.tinyquery.QueryBuildException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.util.ClassUtils;
import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 */
public class InputTypeConvertor {
	
	private Map<Class<?>, Class<?>> needConvertTypeMap = new HashMap<Class<?>, Class<?>>();
	
	public InputTypeConvertor() {
		Configuration cfg = ServiceRegistry.INSTANCE.getConfiguration();
		
		if( Configuration.DBTYPE_MYSQL.equalsIgnoreCase(cfg.getDbtype()) ) {
			needConvertTypeMap.put(java.util.Date.class, java.sql.Timestamp.class);
		}else if( Configuration.DBTYPE_ORACLE.equalsIgnoreCase(cfg.getDbtype()) ) {
			needConvertTypeMap.put(java.util.Date.class, ClassUtils.getClass("oracle.sql.TIMESTAMP"));
		}
		
		String needConvertStr = (String) ServiceRegistry.INSTANCE.getConfiguration().get("app.default_input_type_converts");
		if( needConvertStr != null ) {
			String[] needConvertClassPairStrs = needConvertStr.split(",");
			for (String needConvertClassPairStr : needConvertClassPairStrs) {
				try {
					String[] needConvertClassPairs = needConvertClassPairStr.split("=");
					String fromClassName = needConvertClassPairs[0];
					String toClassName = needConvertClassPairs[1];
					needConvertTypeMap.put(Class.forName(fromClassName), Class.forName(toClassName));
				} catch (ClassNotFoundException e) {
					throw new QueryBuildException(e);
				}
			}
		}
	}
	
	private Object doConvert(Object source) {
		if( source == null ) {
			return null;
		}
		
		Class<?> sourceType = source.getClass();
		Class<?> targetType = needConvertTypeMap.get(sourceType);
		if( targetType == null ) {
			return source;
		}
		
		return TypeConvertUtils.convert(source, targetType);
	}
	
	/**
	 * 参数类型转换
	 * @param arg
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void argumentsConvert(Object arg) {
		if( arg == null ) {
			return;
		}
		
		if( arg.getClass().isArray() ) {
			Object[] args = (Object[]) arg;
			for (int i = 0; i < args.length; i++) {
				args[i] = doConvert(args[i]);
			}
		}else if( List.class.isAssignableFrom(arg.getClass()) ) {
			List list = (List) arg;
			for (int i = 0; i < list.size(); i++) {
				list.set(i, doConvert(list.get(i)));
			}
		}else {
			doConvert(arg);
		}
	}
	

}
