package me.ooi.tinyquery;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author jun.zhao
 */
public class Invocation {
	
	private QueryExecutionContext queryExecutionContext;
	private QueryExecutor queryExecutor;
	private Iterator<Interceptor> iterator;
	
	public Invocation(QueryExecutor queryExecutor, QueryExecutionContext queryExecutionContext) {
		super();
		this.queryExecutionContext = queryExecutionContext;
		this.queryExecutor = queryExecutor;
		
		QueryDefinition queryDefinition = queryExecutionContext.getQueryDefinition();
		iterator = Arrays.asList(queryDefinition.getInterceptors()).iterator();
	}
	
	public QueryExecutionContext getQueryExecutionContext() {
		return queryExecutionContext;
	}
	
	public QueryExecutor getQueryExecutor() {
		return queryExecutor;
	}
	
	public Object invoke() throws SQLException {
		if( iterator.hasNext() ) {
			return iterator.next().intercept(this);
		}else {
			return queryExecutor.execute(queryExecutionContext);
		}
	}

}
