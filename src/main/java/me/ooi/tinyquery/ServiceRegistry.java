package me.ooi.tinyquery;

import java.util.Iterator;
import java.util.ServiceLoader;

import me.ooi.tinyquery.base.BaseQueryExecutor;
import me.ooi.tinyquery.base.Paging;
import me.ooi.tinyquery.base.idgenerator.IdGenerator;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class ServiceRegistry {
	
	public static final ServiceRegistry INSTANCE = new ServiceRegistry();
	
	private QueryExecutor queryExecutor;
	private Paging paging;
	private Configuration configuration;
	private QueryProxyManager queryProxyManager;
	private ConnectionFactory connectionFactory;
	private IdGenerator idGenerator;
	
	private ServiceRegistry() {
		super();
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		ServiceLoader<QueryExecutor> queryExecutorLoader = ServiceLoader.load(QueryExecutor.class, cl);
		Iterator<QueryExecutor> queryExecutorLoaderIt = queryExecutorLoader.iterator();
		queryExecutor = queryExecutorLoaderIt.hasNext() ? queryExecutorLoaderIt.next() : new BaseQueryExecutor();
		
		ServiceLoader<Paging> pagingLoader = ServiceLoader.load(Paging.class, cl);
		Iterator<Paging> pagingLoaderIt = pagingLoader.iterator();
		if( pagingLoaderIt.hasNext() ) {
			paging = pagingLoaderIt.next();
		}else {
			throw new ServiceNotConfiguredException(Paging.class.getName());
		}
		
		ServiceLoader<ConnectionFactory> connectionFactoryLoader = ServiceLoader.load(ConnectionFactory.class, cl);
		Iterator<ConnectionFactory> connectionFactoryLoaderIt = connectionFactoryLoader.iterator();
		if( connectionFactoryLoaderIt.hasNext() ) {
			connectionFactory = connectionFactoryLoaderIt.next();
		}else {
			throw new ServiceNotConfiguredException(ConnectionFactory.class.getName());
		}
		
		ServiceLoader<IdGenerator> idGeneratorLoader = ServiceLoader.load(IdGenerator.class, cl);
		Iterator<IdGenerator> idGeneratorLoaderIt = idGeneratorLoader.iterator();
		if( idGeneratorLoaderIt.hasNext() ) {
			idGenerator = idGeneratorLoaderIt.next();
		}else {
			throw new ServiceNotConfiguredException(IdGenerator.class.getName());
		}
		
		queryProxyManager = new QueryProxyManager();
	}
	
	public void init(Configuration configuration) {
		this.configuration = configuration;
		
		queryExecutor.init();
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

}
