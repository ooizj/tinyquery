package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Method;
import java.sql.SQLException;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.interceptor.InterceptorUtils;
import me.ooi.tinyquery.interceptor.criteria.CriteriaInterceptor;

/**
 * @author jun.zhao
 */
public class BaseSelectObjsInterceptor extends AbstractBaseQueryInterceptor{
	
	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
		return false; //add by annotation
	}
	
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		Object[] args = context.getArgs();
		context.setArgs(new Object[] {args[1]});
		InterceptorUtils.getInterceptor(queryDefinition, CriteriaInterceptor.class).setCriteriaArgIndex(context, 0);
		
		String selectFields = (String) args[0];
		String tableName = (String) queryDefinition.get(DEF_KEY_TABLE_NAME);
		String sql = String.format("select %s from  %s ", selectFields, tableName);
		context.setQuery(sql);
		
		return invocation.invoke();
	}

}
