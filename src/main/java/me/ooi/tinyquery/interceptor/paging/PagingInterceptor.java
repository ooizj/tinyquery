package me.ooi.tinyquery.interceptor.paging;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.interceptor.base.Page;
import me.ooi.tinyquery.interceptor.base.PageResult;
import me.ooi.tinyquery.util.ClassUtils;

/**
 * @author jun.zhao
 */
public class PagingInterceptor implements Interceptor{
	
	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
		if( method == null ) {
			return false;
		}
		return ClassUtils.hasParamType(method, Page.class);
	}

	@Override
	public void prepare(QueryDefinition queryDefinition, Method method) {
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		Page page = (Page) context.get(PrePagingInterceptor.CTX_KEY_PAGE);
		if( page != null ){ //传了page才进行分页，否则会查询全部
			
			//返回结果为“PageResult”，则先查询总记录数，然后查询记录，生成“PageResult”
			if( PageResult.class.isAssignableFrom(queryDefinition.getReturnType()) ) {
				Long total = ServiceRegistry.INSTANCE.getTotalGenerator().getTotal(context);
				
				PageResult pageResult = new PageResult();
				pageResult.setPage(page);
				pageResult.setTotal(total);
				if( total > 0 ) {
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
			
		}else {
			
			//返回结果为“PageResult”，则将查询的记录转换为PageResult
			if( PageResult.class.isAssignableFrom(queryDefinition.getReturnType()) ) {
				List list = (List) invocation.invoke();
				return new PageResult(list);
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
