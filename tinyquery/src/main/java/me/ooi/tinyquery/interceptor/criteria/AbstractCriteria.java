package me.ooi.tinyquery.interceptor.criteria;

import java.util.List;

/**
 * @author jun.zhao
 */
public abstract class AbstractCriteria {
	
	public abstract String getQuery();
	
	public abstract void addArguments(List<Object> arguments);
	
	public abstract int size();

}
