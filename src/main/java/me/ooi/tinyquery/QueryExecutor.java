package me.ooi.tinyquery;

import java.sql.SQLException;

/**
 * @author jun.zhao
 */
public interface QueryExecutor extends Service {
	
	/**
	 * query executor
	 * @param queryDefinition
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	Object execute(QueryExecutionContext context) throws SQLException;

}
