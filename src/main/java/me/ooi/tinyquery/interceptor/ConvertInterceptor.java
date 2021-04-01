package me.ooi.tinyquery.interceptor;

import java.lang.reflect.Method;
import java.sql.SQLException;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.ServiceRegistry;

/**
 * @author jun.zhao
 */
public class ConvertInterceptor implements Interceptor{

	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
		return true;
	}

	@Override
	public void prepare(QueryDefinition queryDefinition, Method method) {
	}
	
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		ServiceRegistry.INSTANCE.getInputTypeConvertor().argumentsConvert(context.getArgs());
		
		return invocation.invoke();
	}
	
}
