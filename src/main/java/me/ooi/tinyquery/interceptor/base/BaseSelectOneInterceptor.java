package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;

/**
 * @author jun.zhao
 */
public class BaseSelectOneInterceptor extends AbstractBaseQueryInterceptor{
	
	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
		return false; //add by annotation
	}

	@Override
	public void prepare(QueryDefinition queryDefinition, Method method) {
		super.prepare(queryDefinition, method);
		
		Class<?> entityClass = (Class<?>) queryDefinition.get(DEF_KEY_ENTITY_CLASS);
		queryDefinition.setGenericReturnClass(entityClass);
		queryDefinition.setReturnType(List.class);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		String tableName = (String) queryDefinition.get(DEF_KEY_TABLE_NAME);
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
