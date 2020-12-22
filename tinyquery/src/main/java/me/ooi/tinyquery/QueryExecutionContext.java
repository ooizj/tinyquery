package me.ooi.tinyquery;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.ooi.tinyquery.base.Page;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class QueryExecutionContext {
	
	private QueryDefinition queryDefinition;
	
	//最终使用的query，比如insert语句就是动态的
	private String query;
	
	//实参
	private Object[] args;
	
	//分页信息
	private Page page;
	
	public QueryExecutionContext(QueryDefinition queryDefinition, Object[] args) {
		super();
		this.queryDefinition = queryDefinition;
		this.args = args;
	}

}
