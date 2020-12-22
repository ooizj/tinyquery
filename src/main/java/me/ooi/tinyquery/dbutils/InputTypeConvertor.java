package me.ooi.tinyquery.dbutils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Slf4j
public class InputTypeConvertor {
	
	private static final String path = "/typeconvert-input.properties";
	
	private static Map<Class<?>, Class<?>> needConvertTypeMap = new HashMap<Class<?>, Class<?>>();
	
	static {
		Properties needConvertTypeProperties = new Properties();
		try {
			needConvertTypeProperties.load(InputTypeConvertor.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Map.Entry<Object, Object> entry : needConvertTypeProperties.entrySet()) {
			try {
				String fromClassName = (String) entry.getKey();
				String toClassName = (String) entry.getValue();
				needConvertTypeMap.put(Class.forName(fromClassName), Class.forName(toClassName));
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	private static Object doConvert(Object source) {
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
	public static void argumentsConvert(Object arg) {
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
