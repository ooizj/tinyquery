package me.ooi.tinyquery.base;

import java.lang.reflect.Method;
import java.util.List;

import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryDefinitionProvider;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class BaseQueryDefinitionProvider extends QueryDefinitionProvider{
	
	public BaseQueryDefinitionProvider(Class<?> queryInterface, Method method) {
		super(queryInterface, method);
	}
	
	@Override
	public void init(QueryDefinition queryDefinition) {
		super.init(queryDefinition);
		
		selectList(queryDefinition);
		
		paging(queryDefinition);
		
	}
	
	//在调用方法为“selectList”的时候，将返回的泛型设置为Entity的类型
	private void selectList(QueryDefinition queryDefinition) {
		if( method.getDeclaringClass() == BaseQuery.class && (
				BaseQuery.METHOD_SELECT_LIST.equals(method.getName()) 
				|| BaseQuery.METHOD_SELECT_ONE.equals(method.getName()) 
				|| BaseQuery.METHOD_SELECT_PAGE.equals(method.getName()) ) ) {
			Class<?> entityClass = QueryInterfaceUtils.getEntityClass(queryInterface);
			queryDefinition.setGenericReturnType(entityClass);
			
			if( BaseQuery.METHOD_SELECT_ONE.equals(method.getName()) ) {
				queryDefinition.setReturnType(List.class);
			}
		}
	}
	
	//处理分页
	private void paging(QueryDefinition queryDefinition) {
		if( QueryDefinition.Type.SELECT == queryDefinition.getType() && hasPagingParam(method.getParameterTypes()) ){ 
			queryDefinition.setHasPagingParam(true);
			
			//如果返回是“PageResult”
			if( PageResult.class.isAssignableFrom(queryDefinition.getReturnType()) ) {
				queryDefinition.setHasPagingResult(true);
			}
		}
	}
	
	/**
	 * 判断最后一个参数是否为Page
	 * @param paramTypes
	 * @return
	 */
	private boolean hasPagingParam(Class<?>[] paramTypes) {
		if( paramTypes != null && paramTypes.length > 0 ) {
			Class<?> lastType = paramTypes[paramTypes.length-1];
			if( lastType != null && Page.class.isAssignableFrom(lastType) ) {
				return true;
			}
		}
		
		return false;
	}
	
}
