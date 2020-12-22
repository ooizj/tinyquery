package me.ooi.tinyquery.base;

import java.sql.SQLException;

import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 */
public abstract class AbstractRecordCountGenerator implements RecordCountGenerator{
	
	/**
	 * 获取总记录数
	 * @param context
	 * @return
	 */
	@Override
	public Long getRecordCount(QueryExecutionContext context) {
		Object ret;
		try {
			ret = ServiceRegistry.INSTANCE.getQueryExecutor().execute(countQueryExecutionContext(context));
		} catch (SQLException e) {
			throw new QueryExecutionException(e);
		}
		return (Long) TypeConvertUtils.convert(ret, Long.class);
	}
	
	private QueryExecutionContext countQueryExecutionContext(QueryExecutionContext context) {
		String useQuery = context.getQuery();
		String countQuery = ServiceRegistry.INSTANCE.getPaging().getCountQuery(useQuery);
		
		QueryExecutionContext pageResultGeneratorExecutionContext = new QueryExecutionContext();
		
		QueryDefinition def = new QueryDefinition();
		def.setKey("countquery_"+useQuery);
		def.setType(QueryDefinition.Type.SELECT);
		def.setQuery(countQuery);
		pageResultGeneratorExecutionContext.setQuery(def.getQuery());
		def.setReturnType(Long.class);
		def.setGenericReturnType(Long.class);
		
		pageResultGeneratorExecutionContext.setQueryDefinition(def);
		pageResultGeneratorExecutionContext.setArgs(context.getArgs());
		
		return pageResultGeneratorExecutionContext;
	}
	
}
