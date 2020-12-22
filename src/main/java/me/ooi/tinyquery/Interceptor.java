package me.ooi.tinyquery;

import java.sql.SQLException;

/**
 * @author jun.zhao
 */
public interface Interceptor {
	
	boolean accept(QueryDefinition queryDefinition);
	
	void prepare(QueryDefinition queryDefinition);
	
	Object intercept(Invocation invocation) throws SQLException;

}
