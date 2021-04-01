package me.ooi.tinyquery.interceptor.criteria;

import java.lang.reflect.Method;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.util.ClassUtils;

/**
 * @author jun.zhao
 */
public class CriteriaInterceptor implements Interceptor{
	
	public static final String DEF_KEY_CRITERIAPARAM = "criteriaParam";
	public static final String DEF_KEY_CRITERIA_PARAM_INDEX = "criteriaParamIndex";
	public static final String CTX_KEY_CRITERIA_ARG_INDEX = "criteriaArgIndex";
	
	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
		if( method == null ) {
			return false;
		}
		return ClassUtils.hasParamType(method, Criteria.class);
	}
	
	@Override
	public void prepare(QueryDefinition queryDefinition, Method method) {
		int criteriaParamIndex = ClassUtils.getParamTypeIndex(method, Criteria.class);
		queryDefinition.put(DEF_KEY_CRITERIA_PARAM_INDEX, criteriaParamIndex);
		CriteriaParam criteriaParam = ClassUtils.getAnnotationByIndex(method, criteriaParamIndex, CriteriaParam.class);
		queryDefinition.put(DEF_KEY_CRITERIAPARAM, criteriaParam);
	}
	
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		Object[] args = context.getArgs();
		if( args == null ) {
			return invocation.invoke();
		}
		
		Integer criteriaArgIndex = (Integer) context.get(CTX_KEY_CRITERIA_ARG_INDEX);
		if( criteriaArgIndex == null ) {
			criteriaArgIndex = (Integer) queryDefinition.get(DEF_KEY_CRITERIA_PARAM_INDEX);
		}
		Criteria criteria = (Criteria) args[criteriaArgIndex];
		
		//remove criteria parameter anyway
		args = ArrayUtils.remove(args, criteriaArgIndex);
		context.setArgs(args);
		if( criteria == null ) {
			return invocation.invoke();
		}
		
		String condition = "";
		if( criteria.size() > 0 ) {
			CriteriaParam criteriaParam = (CriteriaParam) queryDefinition.get(DEF_KEY_CRITERIAPARAM);
			condition = criteria.getQuery(criteriaParam==null?"where":criteriaParam.prefix());
		}
		
		String orderByClause = "";
		if( criteria.hasOrderBy() ) {
			orderByClause = " order by " + criteria.getOrderByClause();
		}
		
		String sql = String.format("%s %s %s", context.getQuery(), condition, orderByClause);
		context.setQuery(sql);
		
		if( criteria.size() > 0 ) {
			Object[] originArguments = context.getArgs();
			Object[] criteriaArguments = criteria.getArguments();
			Object[] useArguments = new Object[originArguments.length+criteriaArguments.length];
			int destPos = 0;
			System.arraycopy(originArguments, 0, useArguments, destPos, criteriaArgIndex);
			destPos += criteriaArgIndex;
			System.arraycopy(criteriaArguments, 0, useArguments, destPos, criteriaArguments.length);
			destPos += criteriaArguments.length;
			System.arraycopy(originArguments, criteriaArgIndex, useArguments, destPos, originArguments.length-criteriaArgIndex);
			context.setArgs(useArguments);
		}
		
		return invocation.invoke();
	}
	
	public void setCriteriaArgIndex(QueryExecutionContext context, int criteriaArgIndex) {
		context.put(CriteriaInterceptor.CTX_KEY_CRITERIA_ARG_INDEX, criteriaArgIndex);
	}
	
}
