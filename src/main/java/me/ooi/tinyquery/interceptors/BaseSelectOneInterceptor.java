package me.ooi.tinyquery.interceptors;

import java.sql.SQLException;
import java.util.List;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.base.TooManyResultsException;

/**
 * @author jun.zhao
 */
public class BaseSelectOneInterceptor extends AbstractBaseQueryInterceptor{
	
	@Override
	public boolean accept(QueryDefinition queryDefinition) {
		return false; //add by annotation
	}

	@Override
	public void prepare(QueryDefinition queryDefinition) {
		super.prepare(queryDefinition);
		queryDefinition.setReturnType(List.class);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		String tableName = getBaseQueryDefinition(queryDefinition).getTableName();
		String sql = String.format("select * from  %s ", tableName);
		context.setQuery(sql);
		
		List list = (List) invocation.invoke();
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
			throw new TooManyResultsException(
					"Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
		} else {
			return null;
		}
	}

}
