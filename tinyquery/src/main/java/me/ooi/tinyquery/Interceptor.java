package me.ooi.tinyquery;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * @author jun.zhao
 */
public interface Interceptor {
	
	boolean accept(QueryDefinition queryDefinition, Method method);
	
	void prepare(QueryDefinition queryDefinition, Method method);
	
	Object intercept(Invocation invocation) throws SQLException;

}
