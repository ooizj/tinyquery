package me.ooi.tinyquery.interceptor.criteria;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author jun.zhao
 */
public class NotInCriteria extends AbstractCriteria {
	
	private String columnName;
	private List<?> values;
	
	public NotInCriteria(String columnName, List<?> values) {
		super();
		this.columnName = columnName;
		this.values = values;
	}

	@Override
	public String getQuery() {
		return String.format(" %s not in (%s) ", columnName, StringUtils.join(Collections.nCopies(values.size(), "?").iterator(), ","));
	}

	@Override
	public void addArguments(List<Object> arguments) {
		arguments.addAll(values);
	}
	
	@Override
	public int size() {
		return 1;
	}

}
