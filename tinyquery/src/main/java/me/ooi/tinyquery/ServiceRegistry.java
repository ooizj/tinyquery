package me.ooi.tinyquery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import me.ooi.tinyquery.dbutils.InputTypeConvertor;
import me.ooi.tinyquery.interceptor.base.IdGenerator;
import me.ooi.tinyquery.interceptor.base.MysqlIdGenerator;
import me.ooi.tinyquery.interceptor.base.MysqlPaging;
import me.ooi.tinyquery.interceptor.base.MysqlTotalGenerator;
import me.ooi.tinyquery.interceptor.base.OracleIdGenerator;
import me.ooi.tinyquery.interceptor.base.OraclePaging;
import me.ooi.tinyquery.interceptor.base.OracleTotalGenerator;
import me.ooi.tinyquery.interceptor.base.Paging;
import me.ooi.tinyquery.interceptor.base.TotalGenerator;

/**
 * @author jun.zhao
 */
public class ServiceRegistry {
	
	public static final ServiceRegistry INSTANCE = new ServiceRegistry();
	
	private QueryExecutor queryExecutor;
	private Configuration configuration;
	private QueryProxyManager queryProxyManager;
	private ConnectionFactory connectionFactory;
	private IdGenerator idGenerator;
	private Paging paging;
	private TotalGenerator totalGenerator;
	private List<Interceptor> interceptors;
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
		
		totalGenerator = getService(TotalGenerator.class, cl);
		if( totalGenerator == null ) {
			if( Configuration.DBTYPE_MYSQL.equalsIgnoreCase(cfg.getDbtype()) ) {
				totalGenerator = new MysqlTotalGenerator();
			}else if( Configuration.DBTYPE_ORACLE.equalsIgnoreCase(cfg.getDbtype()) ) {
				totalGenerator = new OracleTotalGenerator();
			}else {
				throw new ServiceNotConfiguredException(TotalGenerator.class.getName());
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
		
		//init interceptors
		Iterable<Interceptor> interceptorIt = ServiceLoader.load(Interceptor.class, cl);
		interceptors = new ArrayList<Interceptor>();
		for (Interceptor interceptor : interceptorIt) {
			interceptors.add(interceptor);
		}
		interceptors = Collections.unmodifiableList(interceptors);
		
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

	public List<Interceptor> getInterceptors() {
		return interceptors;
	}

	public InputTypeConvertor getInputTypeConvertor() {
		return inputTypeConvertor;
	}

	public TotalGenerator getTotalGenerator() {
		return totalGenerator;
	}

}
