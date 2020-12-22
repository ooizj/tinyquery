package me.ooi.tinyquery;

import java.util.LinkedList;
import java.util.Queue;

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
	private String useQuery;
	
	private Object[] args;
	private Page page;
	
	private Queue<Task> afterExecutionTasks = new LinkedList<Task>();
	
	public QueryExecutionContext(QueryDefinition queryDefinition, Object[] args) {
		super();
		this.queryDefinition = queryDefinition;
		this.args = args;
	}

}
