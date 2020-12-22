package me.ooi.tinyquery.interceptors;

import java.sql.SQLException;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 */
public class BaseSelectListInterceptor extends AbstractBaseQueryInterceptor{
	
	@Override
	public boolean accept(QueryDefinition queryDefinition) {
		return false; //add by annotation
	}
	
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		String tableName = getBaseQueryDefinition(queryDefinition).getTableName();
		String sql = String.format("select * from  %s ", tableName);
		context.setQuery(sql);
		
		return invocation.invoke();
	}

}
