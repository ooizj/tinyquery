package me.ooi.tinyquery.interceptors;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import lombok.Data;
import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.annotation.CriteriaParam;
import me.ooi.tinyquery.base.Page;
import me.ooi.tinyquery.criteria.Criteria;
import me.ooi.tinyquery.util.ClassUtils;

/**
 * @author jun.zhao
 */
public class CriteriaInterceptor implements Interceptor{
	
	private static Map<String, CriteriaDefinition> criteriaDefinitionCache = new HashMap<String, CriteriaDefinition>();
	
	@Data
	public static class CriteriaDefinition{
		
		//是否有“Criteria”参数
		private boolean hasCriteriaParam;
		
		//是否有“Page”参数
		private boolean hasPageParam;
		
		//CriteriaParam
		private CriteriaParam criteriaParam;
	}
	
	@Override
	public boolean accept(QueryDefinition queryDefinition) {
		Method method = queryDefinition.getMethod();
		if( method == null ) {
			return false;
		}
		return ClassUtils.hasParamType(method, Criteria.class);
	}
	
	@Override
	public void prepare(QueryDefinition queryDefinition) {
		Method method = queryDefinition.getMethod();
		
		CriteriaDefinition criteriaDefinition = new CriteriaDefinition();
		criteriaDefinition.setHasCriteriaParam(ClassUtils.hasParamType(method, Criteria.class));
		if( criteriaDefinition.isHasCriteriaParam() ) {
			int index = ClassUtils.getParamTypeIndex(method, Criteria.class);
			CriteriaParam criteriaParam = ClassUtils.getAnnotationByIndex(method, index, CriteriaParam.class);
			criteriaDefinition.setCriteriaParam(criteriaParam);
		}
		criteriaDefinition.setHasPageParam(ClassUtils.hasParamType(method, Page.class));
		
		criteriaDefinitionCache.put(queryDefinition.getKey(), criteriaDefinition);
	}
	
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		CriteriaDefinition criteriaDefinition = criteriaDefinitionCache.get(queryDefinition.getKey());
		
		Object[] args = context.getArgs();
		if( args == null ) {
			return invocation.invoke();
		}
		
		Criteria criteria = getCriteria(args);
		if( criteria != null ) {
			args = ArrayUtils.removeElement(args, criteria);
			context.setArgs(args);
		}
		
		String condition = "";
		if( criteria != null && criteria.size() > 0 ) {
			CriteriaParam criteriaParam = criteriaDefinition.getCriteriaParam();
			condition = criteria.getQuery(criteriaParam==null?"where":criteriaParam.prefix());
		}
		
		String orderByClause = "";
		if( criteria != null && criteria.hasOrderBy() ) {
			orderByClause = " order by " + criteria.getOrderByClause();
		}
		
		String sql = String.format("%s %s %s", context.getQuery(), condition, orderByClause);
		context.setQuery(sql);
		
		if( criteria != null && criteria.size() > 0 ) {
			Object[] originArguments = context.getArgs();
			Object[] criteriaArguments = criteria.getArguments();
			Object[] useArguments = new Object[originArguments.length+criteriaArguments.length];
			System.arraycopy(originArguments, 0, useArguments, 0, originArguments.length);
			System.arraycopy(criteriaArguments, 0, useArguments, originArguments.length, criteriaArguments.length);
			context.setArgs(useArguments);
		}
		
		return invocation.invoke();
	}
	
	/**
	 * 获取参数中的“Criteria”
	 * @param args
	 * @return
	 */
	private Criteria getCriteria(Object[] args) {
		if( args != null ) {
			for (Object arg : args) {
				if( Criteria.class.isInstance(arg) ) {
					return (Criteria) arg;
				}
			}
		}
		return null;
	}
	
}
