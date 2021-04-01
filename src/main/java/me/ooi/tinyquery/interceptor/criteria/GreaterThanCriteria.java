package me.ooi.tinyquery.interceptor.criteria;

import java.util.List;

/**
 * @author jun.zhao
 */
public class GreaterThanCriteria extends AbstractCriteria {
	
	private String columnName;
	private Object value;
	
	public GreaterThanCriteria(String columnName, Object value) {
		super();
		this.columnName = columnName;
		this.value = value;
	}

	@Override
	public String getQuery() {
		return String.format(" %s > ? ", columnName);
	}

	@Override
	public void addArguments(List<Object> arguments) {
		arguments.add(value);
	}
	
	@Override
	public int size() {
		return 1;
	}

}
