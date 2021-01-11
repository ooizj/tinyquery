package me.ooi.tinyquery.interceptor.base;

import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.interceptor.paging.PrePagingInterceptor;

/**
 * @author jun.zhao
 */
public class MysqlPaging implements Paging{

	@Override
	public String getPagingQuery(String query) {
		//TODO 关键字check
		String pagingQuery = 
				query + " limit ?, ? ";
		
		return pagingQuery;
	}
	
	@Override
	public String getCountQuery(String query) {
		//TODO 关键字check
		String countQuery = 
				" select count(*) from ("+query+") count_origin_t";
		
		return countQuery;
	}

	@Override
	public void setPagingParams(QueryExecutionContext context) {
		Object[] args = context.getArgs();
		Page page = (Page) context.get(PrePagingInterceptor.CTX_KEY_PAGE);
		
		//去掉分页参数，添加分页参数
		Object[] useArgs = new Object[args.length+2];
		System.arraycopy(args, 0, useArgs, 0, args.length);
		useArgs[useArgs.length-2] = ((page.getPageNumber()-1) * page.getPageSize());
		useArgs[useArgs.length-1] = page.getPageSize();
		
		context.setArgs(useArgs);
	}

}
