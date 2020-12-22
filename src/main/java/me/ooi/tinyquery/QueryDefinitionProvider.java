package me.ooi.tinyquery;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.ooi.tinyquery.annotation.Interceptors;
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
		queryDefinition.setMethod(method);
		queryDefinition.setMethodName(method.getName());
		queryDefinition.setQuery(getCommand());
		queryDefinition.setType(getCommandType());
		queryDefinition.setReturnType(method.getReturnType());
		queryDefinition.setGenericReturnType(method.getGenericReturnType());
		queryDefinition.setParameterTypes(method.getParameterTypes());
		queryDefinition.setMethodDeclaringClass(method.getDeclaringClass());
		queryDefinition.setQueryInterface(queryInterface);
		queryDefinition.setInterceptors(getInterceptors(queryDefinition));
	}
	
	protected String getCommand() {
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
	
	protected QueryDefinition.Type getCommandType() {
		if( method.isAnnotationPresent(Select.class) ) {
			return QueryDefinition.Type.SELECT;
		}else if( method.isAnnotationPresent(Update.class) ) {
			return QueryDefinition.Type.UPDATE;
		}else {
			throw new NotFoundCommandException();
		}
	}
	
	/**
	 * get all of Interceptors and prepare
	 * @param queryDefinition
	 * @return
	 */
	protected Interceptor[] getInterceptors(QueryDefinition queryDefinition) {
		
		List<Interceptor> allInterceptor = new ArrayList<Interceptor>();
		
		List<Class<? extends Interceptor>> annotationInterceptorClasses = new ArrayList<Class<? extends Interceptor>>();
		if( method.isAnnotationPresent(Interceptors.class) ) {
			Interceptors interceptors = method.getAnnotation(Interceptors.class);
			annotationInterceptorClasses.addAll(Arrays.asList(interceptors.value()));
		}
		
		//add interceptors order by META-INF/services/me.ooi.tinyquery.Interceptor 
		for (Interceptor interceptor : ServiceRegistry.INSTANCE.getInterceptors()) {
			
			//add annotation interceptors
			for (Class<? extends Interceptor> annotationInterceptorClass : annotationInterceptorClasses) {
				if( annotationInterceptorClass.isInstance(interceptor) ) {
					interceptor.prepare(queryDefinition);
					allInterceptor.add(interceptor);
				}
			}
			
			if( (!allInterceptor.contains(interceptor)) && interceptor.accept(queryDefinition) ) {
				interceptor.prepare(queryDefinition);
				allInterceptor.add(interceptor);
			}
		}
		
		Interceptor[] ret = new Interceptor[allInterceptor.size()];
		allInterceptor.toArray(ret);
		return ret;
	}
	
}
