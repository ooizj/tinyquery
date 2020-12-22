package me.ooi.tinyquery.interceptors;

import java.lang.reflect.Method;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.base.Page;
import me.ooi.tinyquery.util.ClassUtils;

/**
 * @author jun.zhao
 */
public class PrePagingInterceptor implements Interceptor{

	@Override
	public boolean accept(QueryDefinition queryDefinition) {
		Method method = queryDefinition.getMethod();
		if( method == null ) {
			return false;
		}
		return ClassUtils.hasParamType(method, Page.class);
	}

	@Override
	public void prepare(QueryDefinition queryDefinition) {
	}

	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		Object[] args = context.getArgs();
		Page page = getPage(args);
		if( page != null ) {
			args = ArrayUtils.removeElement(args, page);
			context.setArgs(args);
			context.setPage(page);
		}
		
		return invocation.invoke();
	}
	
	/**
	 * 获取参数中的“Criteria”
	 * @param args
	 * @return
	 */
	private Page getPage(Object[] args) {
		if( args != null ) {
			for (Object arg : args) {
				if( Page.class.isInstance(arg) ) {
					return (Page) arg;
				}
			}
		}
		return null;
	}
	
}
