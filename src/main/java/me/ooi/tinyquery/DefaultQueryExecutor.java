package me.ooi.tinyquery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
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
import me.ooi.tinyquery.util.ClassUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class DefaultQueryExecutor implements QueryExecutor{
	
	public static final Class<?>[] IS_NOT_BEAN_CLASS = new Class<?>[]{
		String.class, Number.class, Boolean.class, Date.class
	};

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
			
			Class<?> genericClass = null;
			if( def.getReturnType().equals(def.getGenericReturnType())/* have not Generic type */ || 
				(genericClass = ClassUtils.getFirstGenericClass(def.getGenericReturnType())) == null ||
				Map.class.isAssignableFrom(genericClass) ) {
				
				return new MapListHandler(rowProcessor);
			}
			
			if( genericClass.isArray() ) {
				return new ArrayListHandler(rowProcessor);
			}
			
			if( isNotBean(genericClass) ) {
				return new ColumnListHandler(genericClass);
			}else {
				return new BeanListHandler(genericClass, rowProcessor);
			}
			
		} else if (def.getReturnType().isArray()) {
			
			return new ArrayHandler(rowProcessor);
			
		} else if (Map.class.isAssignableFrom(def.getReturnType())) {
			
			return new MapHandler(rowProcessor);
			
		} else {
			
			if( isNotBean(def.getReturnType()) ) {
				return new SingleColumnResultHandler(def.getReturnType());
			}else {
				return new BeanHandler(def.getReturnType(), rowProcessor);
			}
			
		}
	}
	
	private boolean isNotBean(Class<?> clazz) {
		for (Class<?> signletonClass : IS_NOT_BEAN_CLASS) {
			if( signletonClass == clazz || signletonClass.isAssignableFrom(clazz) ) {
				return true;
			}
		}
		
		return false;
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
