package me.ooi.tinyquery.interceptors;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.base.Page;
import me.ooi.tinyquery.base.PageResult;
import me.ooi.tinyquery.util.ClassUtils;

/**
 * @author jun.zhao
 */
public class PagingInterceptor implements Interceptor{
	
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		//是否有分页参数
		boolean isHasPagingParam = false;
		//是否需要计算总记录数
		boolean needRecordCount = false;
		
		if( context.getPage() != null ){ 
			isHasPagingParam = true;
			
			//如果返回是“PageResult”
			if( PageResult.class.isAssignableFrom(queryDefinition.getReturnType()) ) {
				needRecordCount = true;
			}
		}
		
		//返回结果为“PageResult”，则先查询总记录数，然后查询记录，生成“PageResult”
		if( isHasPagingParam ) {
			if( needRecordCount ) {
				Long recordCount = ServiceRegistry.INSTANCE.getRecordCountGenerator().getRecordCount(context);
				
				PageResult pageResult = new PageResult();
				pageResult.setPage(context.getPage());
				pageResult.setTotal(recordCount);
				if( recordCount > 0 ) {
					setPagingQueryAndArgs(context);
					List list = (List) invocation.invoke();
					pageResult.setRecords(list);
				}else {
					pageResult.setRecords(new ArrayList());
				}
				return pageResult;
			}else { //不需要查询总记录
				setPagingQueryAndArgs(context);
			}
		}
		
		return invocation.invoke();
	}
	
	private void setPagingQueryAndArgs(QueryExecutionContext context) {
		String pagingQuery = ServiceRegistry.INSTANCE.getPaging().getPagingQuery(context.getQuery());
		context.setQuery(pagingQuery);
		ServiceRegistry.INSTANCE.getPaging().setPagingParams(context);
	}

}
