package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Method;
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
import me.ooi.tinyquery.interceptor.InterceptorUtils;
import me.ooi.tinyquery.interceptor.criteria.Criteria;
import me.ooi.tinyquery.interceptor.criteria.CriteriaInterceptor;

/**
 * @author jun.zhao
 */
public class BaseUpdateInterceptor extends AbstractBaseQueryInterceptor{
	
	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
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
		
		//update entity
		Object entity = context.getArgs()[0];
		if( entity == null ) {
			throw new QueryExecutionContextBuildException("entity is null.");
		}
		if( entity.getClass() != queryDefinition.get(DEF_KEY_ENTITY_CLASS) ) {
			throw new QueryExecutionContextBuildException("expect entity type is "+
					queryDefinition.get(DEF_KEY_ENTITY_CLASS)+" but found "+entity.getClass()+" .");
		}
		
		//update criteria
		Criteria criteria = (Criteria) context.getArgs()[1];
		if( criteria == null || criteria.size() == 0 ) {
			throw new QueryExecutionException("update without criteria is not allowed.");
		}
		
		//update field expression list（e.g. name=?, age=?）
		List<String> updateFieldSegments = new ArrayList<String>();
		
		//update value list
		List<Object> updateArguments = new ArrayList<Object>();
		
		//add update fields
		Map<String, Object> fields = EntityUtils.getNotNullField(entity);
		for (Entry<String, Object> entry : fields.entrySet()) {
			updateFieldSegments.add(String.format(" %s = ? ", EntityUtils.beanFieldToDbField(entry.getKey()))); //e.g. age = ?, sex = ? 
			updateArguments.add(entry.getValue());
		}
		
		String tableName = (String) queryDefinition.get(DEF_KEY_TABLE_NAME);
		String updateFields = StringUtils.join(updateFieldSegments.iterator(), ",");
		String sql = String.format("update %s set %s ", tableName, updateFields);
		context.setQuery(sql);
		
		updateArguments.add(criteria);
		Object[] useArguments = updateArguments.toArray(new Object[updateArguments.size()]);
		context.setArgs(useArguments);
		InterceptorUtils.getInterceptor(queryDefinition, CriteriaInterceptor.class).setCriteriaArgIndex(context, updateArguments.size()-1);
	}

}
