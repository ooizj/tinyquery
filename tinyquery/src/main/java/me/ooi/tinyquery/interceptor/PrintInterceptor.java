package me.ooi.tinyquery.interceptor;

import java.lang.reflect.Method;
import java.sql.SQLException;

import org.apache.commons.lang.builder.ToStringBuilder;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 */
public class PrintInterceptor implements Interceptor{

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
		printQuery(context);
		
		return invocation.invoke();
	}
	
	private void printQuery(QueryExecutionContext context) {
		System.out.println(context.getQuery());
		System.out.println(ToStringBuilder.reflectionToString(context.getArgs()));
	}

}
