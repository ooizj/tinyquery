package me.ooi.tinyquery;

import java.util.Iterator;
import java.util.ServiceLoader;

import me.ooi.tinyquery.base.IdGenerator;
import me.ooi.tinyquery.base.MysqlIdGenerator;
import me.ooi.tinyquery.base.MysqlPaging;
import me.ooi.tinyquery.base.MysqlRecordCountGenerator;
import me.ooi.tinyquery.base.OracleIdGenerator;
import me.ooi.tinyquery.base.OraclePaging;
import me.ooi.tinyquery.base.OracleRecordCountGenerator;
import me.ooi.tinyquery.base.Paging;
import me.ooi.tinyquery.base.RecordCountGenerator;
import me.ooi.tinyquery.dbutils.InputTypeConvertor;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class ServiceRegistry {
	
	public static final ServiceRegistry INSTANCE = new ServiceRegistry();
	
	private QueryExecutor queryExecutor;
	private Configuration configuration;
	private QueryProxyManager queryProxyManager;
	private ConnectionFactory connectionFactory;
	private IdGenerator idGenerator;
	private Paging paging;
	private RecordCountGenerator recordCountGenerator;
	private Iterable<Interceptor> interceptors;
	private InputTypeConvertor inputTypeConvertor;
	
	public void init(Configuration cfg) {
		this.configuration = cfg;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		paging = getService(Paging.class, cl);
		if( paging == null ) {
			if( Configuration.DBTYPE_MYSQL.equalsIgnoreCase(cfg.getDbtype()) ) {
				paging = new MysqlPaging();
			}else if( Configuration.DBTYPE_ORACLE.equalsIgnoreCase(cfg.getDbtype()) ) {
				paging = new OraclePaging();
			}else {
				throw new ServiceNotConfiguredException(Paging.class.getName());
			}
		}
		
		idGenerator = getService(IdGenerator.class, cl);
		if( idGenerator == null ) {
			if( Configuration.DBTYPE_MYSQL.equalsIgnoreCase(cfg.getDbtype()) ) {
				idGenerator = new MysqlIdGenerator();
			}else if( Configuration.DBTYPE_ORACLE.equalsIgnoreCase(cfg.getDbtype()) ) {
				idGenerator = new OracleIdGenerator();
			}else {
				throw new ServiceNotConfiguredException(IdGenerator.class.getName());
			}
		}
		
		recordCountGenerator = getService(RecordCountGenerator.class, cl);
		if( recordCountGenerator == null ) {
			if( Configuration.DBTYPE_MYSQL.equalsIgnoreCase(cfg.getDbtype()) ) {
				recordCountGenerator = new MysqlRecordCountGenerator();
			}else if( Configuration.DBTYPE_ORACLE.equalsIgnoreCase(cfg.getDbtype()) ) {
				recordCountGenerator = new OracleRecordCountGenerator();
			}else {
				throw new ServiceNotConfiguredException(RecordCountGenerator.class.getName());
			}
		}
		
		connectionFactory = getObject(cfg, "app.connectionFactory", ConnectionFactory.class);
		if( connectionFactory == null ) {
			connectionFactory = new DefaultConnectionFactory();
		}
		
		queryExecutor = getService(QueryExecutor.class, cl);
		if( queryExecutor == null ) {
			queryExecutor = new DefaultQueryExecutor();
		}
		
		interceptors = ServiceLoader.load(Interceptor.class, cl);
		
		queryProxyManager = new QueryProxyManager();
		
		inputTypeConvertor = new InputTypeConvertor();
		
		//init others
		queryExecutor.init();
	}
	
	private <S> S getService(Class<S> clazz, ClassLoader cl) {
		ServiceLoader<S> sl = ServiceLoader.load(clazz, cl);
		Iterator<S> it = sl.iterator();
		if( it.hasNext() ) {
			return it.next();
		}else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getObject(Configuration cfg, String key, Class<T> parentClass){
		String className = null;
		if( key == null || (className = (String) cfg.get(key)) == null ) {
			return null;
		}
		try {
			Class<?> clazz = Class.forName(className);
			Object obj = clazz.newInstance();
			if( !parentClass.isInstance(obj) ) {
				throw new ServiceNotConfiguredException(ConnectionFactory.class.getName());
			}
			return (T) obj;
		} catch (Exception e) {
			throw new ServiceNotConfiguredException(ConnectionFactory.class.getName(), e);
		}
	}

	public QueryExecutor getQueryExecutor() {
		return queryExecutor;
	}
	
	public Paging getPaging() {
		return paging;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public QueryProxyManager getQueryProxyManager() {
		return queryProxyManager;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public IdGenerator getIdGenerator() {
		return idGenerator;
	}

	public Iterable<Interceptor> getInterceptors() {
		return interceptors;
	}

	public InputTypeConvertor getInputTypeConvertor() {
		return inputTypeConvertor;
	}

	public RecordCountGenerator getRecordCountGenerator() {
		return recordCountGenerator;
	}

}
