package me.ooi.tinyquery.criteria;

import java.util.List;

/**
 * @author jun.zhao
 */
public abstract class AbstractCriteria {
	
	protected String query;
	
	public abstract String getQuery(boolean appendPrefix);
	
	public abstract void addArguments(List<Object> arguments);
	
	public abstract int size();

}
