package me.ooi.tinyquery.base;

import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class OraclePaging implements Paging{
	

//	SELECT * FROM
//	(
//	    SELECT a.*, rownum r__
//	    FROM
//	    (
//	        SELECT * FROM ORDERS WHERE CustomerID LIKE 'A%'
//	        ORDER BY OrderDate DESC, ShippingDate DESC
//	    ) a
//	    WHERE rownum < ((pageNumber * pageSize) + 1 )
//	)
//	WHERE r__ >= (((pageNumber-1) * pageSize) + 1)
	
	@Override
	public String getPagingQuery(String query) {
		//TODO 关键字check
		String pagingQuery = 
				"SELECT * FROM \r\n" + 
				"( \r\n" + 
				"    SELECT paging_origin_t.*, rownum r__ \r\n" + 
				"    FROM \r\n" + 
				"    ( \r\n" + 
					query + 
				"    ) paging_origin_t \r\n" + 
				"    WHERE rownum < ? \r\n" +  //(page.getPageNumber() * page.getPageSize()) + 1 
				") \r\n" + 
				"WHERE r__ >= ? "; //((page.getPageNumber()-1) * page.getPageSize()) + 1
		
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
		Page page = context.getPage();
		
		//去掉分页参数，添加分页参数
		Object[] useArgs = new Object[args.length+2];
		System.arraycopy(args, 0, useArgs, 0, args.length);
		useArgs[useArgs.length-2] = (page.getPageNumber() * page.getPageSize()) + 1 ;
		useArgs[useArgs.length-1] = ((page.getPageNumber()-1) * page.getPageSize()) + 1;
		
		context.setArgs(useArgs);
	}

}
