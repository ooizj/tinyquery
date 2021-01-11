package me.ooi.tinyquery.spring;

import org.springframework.beans.factory.FactoryBean;

import me.ooi.tinyquery.ServiceRegistry;

/**
 * @author jun.zhao
 */
public class QueryFactoryBean<T> implements FactoryBean{

	private Class<T> queryInterface;

	public QueryFactoryBean() {
		super();
	}

	public QueryFactoryBean(Class<T> queryInterface) {
		super();
		this.queryInterface = queryInterface;
	}

	@Override
	public T getObject() throws Exception {
		return ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(queryInterface) ;
	}

	@Override
	public Class<?> getObjectType() {
		return queryInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setQueryInterface(Class<T> queryInterface) {
		this.queryInterface = queryInterface;
	}

}
