package me.ooi.tinyquery;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.annotation.Update;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class QueryDefinitionProvider {
	
	protected Class<?> queryInterface;
	protected Method method;
	protected Map<String, String> xmlQueryMap;
	public QueryDefinitionProvider(Class<?> queryInterface, Method method) {
		super();
		this.queryInterface = queryInterface;
		this.method = method;
		
		try {
			xmlQueryMap = new XmlQuerySourceReader(queryInterface).read();
		} catch (IOException e) {
			throw new QueryBuildException(e);
		}
	}
	
	public void init(QueryDefinition queryDefinition) {
		queryDefinition.setKey(QueryDefinitionManager.getKey(queryInterface, method));
		queryDefinition.setMethodName(method.getName());
		queryDefinition.setQuery(getCommand(method));
		queryDefinition.setType(getCommandType(method));
		queryDefinition.setReturnType(method.getReturnType());
		queryDefinition.setGenericReturnType(method.getGenericReturnType());
		queryDefinition.setParameterTypes(method.getParameterTypes());
		queryDefinition.setMethodDeclaringClass(method.getDeclaringClass());
		queryDefinition.setQueryInterface(queryInterface);
	}
	
	protected String getCommand(Method method) {
		if( method.isAnnotationPresent(Select.class) ) {
			Select query = method.getAnnotation(Select.class);
			if( query.source() == QuerySource.ANNOTATION ) {
				return query.value();
			}else if( query.source() == QuerySource.XML ) {
				return xmlQueryMap.get(method.getName());
			}else {
				throw new NotFoundCommandException();
			}
		}else if( method.isAnnotationPresent(Update.class) ) { //same as select
			Update query = method.getAnnotation(Update.class);
			if( query.source() == QuerySource.ANNOTATION ) {
				return query.value();
			}else if( query.source() == QuerySource.XML ) {
				return xmlQueryMap.get(method.getName());
			}else {
				throw new NotFoundCommandException();
			}
		}else {
			throw new NotFoundCommandException();
		}
	}
	
	protected QueryDefinition.Type getCommandType(Method method) {
		if( method.isAnnotationPresent(Select.class) ) {
			return QueryDefinition.Type.SELECT;
		}else if( method.isAnnotationPresent(Update.class) ) {
			return QueryDefinition.Type.UPDATE;
		}else {
			throw new NotFoundCommandException();
		}
	}

}
