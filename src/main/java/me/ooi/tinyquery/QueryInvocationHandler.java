package me.ooi.tinyquery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;
import me.ooi.tinyquery.util.ExceptionUtil;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Slf4j
public class QueryInvocationHandler implements InvocationHandler{
	
	private Class<?> queryInterface;

	public QueryInvocationHandler(Class<?> queryInterface) {
		super();
		this.queryInterface = queryInterface;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if( Object.class == method.getDeclaringClass() ){
            return method.invoke(this, args) ;
        }
		
		QueryDefinition queryDefinition = QueryDefinitionManager.getQueryDefinition(queryInterface, method);
		
		QueryExecutionContext context = new QueryExecutionContext(queryDefinition, args);
		
		try {
			
			//从try开始到finally结束的query都使用同一个connection
			
			ConnectionHolder.remote();
			
			Object result = ServiceRegistry.INSTANCE.getQueryExecutor().execute(context);
			
			executeAfter(context);
			
			return result;
			
		} catch (Throwable t) {
			throw ExceptionUtil.unwrapThrowable(t);
		} finally {
			closeConnection(ConnectionHolder.get());
			ConnectionHolder.remote();
		}
	}
	
	private void closeConnection(Connection conn) {
		if( conn != null ) {
			try {
				ServiceRegistry.INSTANCE.getConnectionFactory().closeConnection(conn);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 执行在“execute”方法执行后需要执行的任务
	 */
	private void executeAfter(QueryExecutionContext context) {
		Task task;
		while( (task = context.getAfterExecutionTasks().poll()) != null ) {
			task.execute();
		}
	}
	
}
