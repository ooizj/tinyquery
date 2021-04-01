package me.ooi.tinyquery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import me.ooi.tinyquery.dbutils.ColumnListHandler;
import me.ooi.tinyquery.dbutils.SingleColumnResultHandler;

/**
 * @author jun.zhao
 */
public class DefaultQueryExecutor implements QueryExecutor{
	
	protected DataSource dataSource;
	
	@Override
	public Object execute(QueryExecutionContext context) throws SQLException {
		if (QueryDefinition.Type.SELECT == context.getQueryDefinition().getType()) {
			return select(context);
		} else {
			return update(context);
		}
	}
	
	public Object select(QueryExecutionContext context) throws SQLException {
		return new QueryRunner(true/*true for oracle*/)
				.query(getConnection(), context.getQuery(), chooseResultSetHandler(context.getQueryDefinition()), context.getArgs());
	}
	
	public Object update(QueryExecutionContext context) throws SQLException {
		return new QueryRunner(true/*true for oracle*/)
				.update(getConnection(), context.getQuery(), context.getArgs());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ResultSetHandler<?> chooseResultSetHandler(QueryDefinition def){
		RowProcessor rowProcessor = new BasicRowProcessor(new GenerousBeanProcessor());
		
		if (Collection.class.isAssignableFrom(def.getReturnType())) {
			Class genericClass = def.getGenericReturnClass();
			
			if( genericClass == null || Map.class.isAssignableFrom(genericClass) ) {
				return new MapListHandler(rowProcessor);
			}
			
			if( genericClass.isArray() ) {
				return new ArrayListHandler(rowProcessor);
			}
			
			if( isJavaPackageClass(genericClass) ) {
				return new ColumnListHandler(genericClass);
			}else {
				return new BeanListHandler(genericClass, rowProcessor);
			}
			
		} else if (def.getReturnType().isArray()) {
			
			return new ArrayHandler(rowProcessor);
			
		} else if (Map.class.isAssignableFrom(def.getReturnType())) {
			
			return new MapHandler(rowProcessor);
			
		} else {
			
			if( isJavaPackageClass(def.getReturnType()) ) {
				return new SingleColumnResultHandler(def.getReturnType());
			}else {
				return new BeanHandler(def.getReturnType(), rowProcessor);
			}
			
		}
	}
	
	private boolean isJavaPackageClass(Class<?> clazz) {
		return clazz.getName().startsWith("java.");
	}

	private Connection getConnection() throws SQLException {
		Connection conn = ConnectionHolder.get();
		if( conn == null ) {
			conn = ServiceRegistry.INSTANCE.getConnectionFactory().getConnection(dataSource);
			ConnectionHolder.set(conn);
		}
		return conn;
	}

	@Override
	public void init() {
		this.dataSource = ServiceRegistry.INSTANCE.getConfiguration().getDataSource();
	}

}
