package me.ooi.tinyquery.interceptors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.base.EntityUtils;
import me.ooi.tinyquery.criteria.Criteria;

/**
 * @author jun.zhao
 */
public class BaseUpdateInterceptor extends AbstractBaseQueryInterceptor{
	
	@Override
	public boolean accept(QueryDefinition queryDefinition) {
		return false; //add by annotation
	}

	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		update0(context);
		
		return invocation.invoke();
	}

	private void update0(QueryExecutionContext context) {
		QueryDefinition queryDefinition = context.getQueryDefinition();
		BaseQueryDefinition baseQueryDefinition = getBaseQueryDefinition(queryDefinition);
		
		//update entity
		Object entity = context.getArgs()[0];
		if( entity == null ) {
			throw new QueryExecutionContextBuildException("entity is null.");
		}
		if( entity.getClass() != baseQueryDefinition.getEntityClass() ) {
			throw new QueryExecutionContextBuildException("expect entity type is "+
					baseQueryDefinition.getEntityClass()+" but found "+entity.getClass()+" .");
		}
		
		//update语句修改字段列表（如：name=?）
		List<String> updateFieldSegments = new ArrayList<String>();
		
		//update value list
		List<Object> updateArguments = new ArrayList<Object>();
		
		//add update fields
		Map<String, Object> fields = EntityUtils.getNotNullField(entity);
		for (Entry<String, Object> entry : fields.entrySet()) {
			updateFieldSegments.add(String.format(" %s = ? ", EntityUtils.beanFieldToDbField(entry.getKey()))); //e.g. age = ?, sex = ? 
			updateArguments.add(entry.getValue());
		}
		
		String tableName = baseQueryDefinition.getTableName();
		String updateFields = StringUtils.join(updateFieldSegments.iterator(), ",");
		String sql = String.format("update %s set %s ", tableName, updateFields);
		context.setQuery(sql);
		
		Criteria criteria = (Criteria) context.getArgs()[1];
		if( criteria == null || criteria.size() == 0 ) {
			throw new QueryExecutionException("禁止无条件更新");
		}
		
		updateArguments.add(criteria);
		Object[] useArguments = updateArguments.toArray(new Object[updateArguments.size()]);
		context.setArgs(useArguments);
	}

}
