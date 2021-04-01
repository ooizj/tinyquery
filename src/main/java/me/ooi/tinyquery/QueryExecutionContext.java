package me.ooi.tinyquery;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jun.zhao
 */
public class QueryExecutionContext {
	
	@Getter @Setter private QueryDefinition queryDefinition;
	@Getter @Setter private String query;
	@Getter @Setter private Object[] args;
	
	private Map<String, Object> additionals = new HashMap<String, Object>();
	
	public QueryExecutionContext() {
	}
	
	public QueryExecutionContext(QueryDefinition queryDefinition, Object[] args) {
		this.queryDefinition = queryDefinition;
		this.args = args;
	}

	public void put(String key, Object value) {
		additionals.put(key, value);
	}
	
	public Object get(String key) {
		return additionals.get(key);
	}
	
}
