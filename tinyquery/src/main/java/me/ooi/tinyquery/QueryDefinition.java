package me.ooi.tinyquery;

import java.lang.reflect.Method;

import lombok.Data;

/**
 * @author jun.zhao
 * @since 1.0
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
	
	//方法
	private Method method;
	
	//返回类型
	private Class<?> returnType;
	
	//返回类型的泛型
	private java.lang.reflect.Type genericReturnType;
	
	//query
	private String query;
	
	//参数类型
	private Class<?>[] parameterTypes;
	
	//拦截器
	private Interceptor[] interceptors;
	
}
