package me.ooi.tinyquery.interceptor.criteria;

import java.util.List;

/**
 * @author jun.zhao
 */
public class IsNotNullCriteria extends AbstractCriteria {
	
	private String columnName;
	
	public IsNotNullCriteria(String columnName) {
		super();
		this.columnName = columnName;
	}

	@Override
	public String getQuery() {
		return String.format(" %s is not null ", columnName);
	}

	@Override
	public void addArguments(List<Object> arguments) {
	}
	
	@Override
	public int size() {
		return 1;
	}

}
