package me.ooi.tinyquery.interceptor.paging;

import java.lang.reflect.Method;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.interceptor.base.Page;
import me.ooi.tinyquery.util.ClassUtils;

/**
 * @author jun.zhao
 */
public class PrePagingInterceptor implements Interceptor{
	
	public static final String DEF_KEY_PAGE_PARAM_INDEX = "pageParamIndex";
	public static final String CTX_KEY_PAGE_ARG_INDEX = "pageArgIndex";
	public static final String CTX_KEY_PAGE = "page";
	
	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
		if( method == null ) {
			return false;
		}
		return ClassUtils.hasParamType(method, Page.class);
	}

	@Override
	public void prepare(QueryDefinition queryDefinition, Method method) {
		int pageParamIndex = ClassUtils.getParamTypeIndex(method, Page.class);
		queryDefinition.put(DEF_KEY_PAGE_PARAM_INDEX, pageParamIndex);
	}

	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		Integer pageArgIndex = (Integer) context.get(CTX_KEY_PAGE_ARG_INDEX);
		if( pageArgIndex == null ) {
			pageArgIndex = (Integer) queryDefinition.get(DEF_KEY_PAGE_PARAM_INDEX);
		}
		Object[] args = context.getArgs();
		Page page = (Page) args[pageArgIndex];
		context.put(CTX_KEY_PAGE, page);
		
		args = ArrayUtils.remove(args, pageArgIndex);
		context.setArgs(args);
		
		return invocation.invoke();
	}
	
	public void setPageArgIndex(QueryExecutionContext context, int pageArgIndex) {
		context.put(CTX_KEY_PAGE_ARG_INDEX, pageArgIndex);
	}
	
}
