package me.ooi.tinyquery;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * @author jun.zhao
 */
@Data
public class QueryDefinition {
	
	public enum Type{
		SELECT,
		UPDATE
	}
	
	//唯一标识
	private String key;
	
	//query type
	private Type type;
	
	//query interface
	private Class<?> queryInterface;
	
	//声明方法的类
	private Class<?> methodDeclaringClass;
	
	//方法名
	private String methodName;
	
	//返回类型
	private Class<?> returnType;
	
	//返回类型的泛型，如返回List<User>，则其泛型为User
	private Class<?> genericReturnClass;
	
	//query
	private String query;
	
	//参数类型
	private Class<?>[] parameterTypes;
	
	//拦截器
	private Interceptor[] interceptors;
	
	private Map<String, Object> additionals = new HashMap<String, Object>();
	
	public void put(String key, Object value) {
		additionals.put(key, value);
	}
	
	public Object get(String key) {
		return additionals.get(key);
	}
	
}
