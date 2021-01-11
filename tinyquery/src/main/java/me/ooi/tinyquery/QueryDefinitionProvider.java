package me.ooi.tinyquery;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.annotation.Update;
import me.ooi.tinyquery.interceptor.InterceptorUtils;

/**
 * @author jun.zhao
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
	
	@SuppressWarnings("rawtypes")
	public void init(QueryDefinition queryDefinition) {
		queryDefinition.setMethodName(method.getName());
		queryDefinition.setQuery(getCommand());
		queryDefinition.setType(getCommandType());
		queryDefinition.setReturnType(method.getReturnType());
		Type genericReturnType = getGenericReturnType(method.getGenericReturnType());
		queryDefinition.setGenericReturnClass((genericReturnType instanceof Class) ? ((Class)genericReturnType) : null);
		queryDefinition.setParameterTypes(method.getParameterTypes());
		queryDefinition.setMethodDeclaringClass(method.getDeclaringClass());
		queryDefinition.setQueryInterface(queryInterface);
		queryDefinition.setInterceptors(InterceptorUtils.getInterceptors(queryDefinition, method));
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
	 * is support generic return type.<br>
	 * support return type: e.g. List&lt;User>, List&lt;T>, List&lt;?>, List&lt;Object[]>, List, T, User
	 * @param type
	 * @return
	 */
	private boolean isSupportGenericReturnType(Type genericReturnType) {
		if( genericReturnType instanceof Class<?> ) { //e.g. List, User
			return true;
		}
		
		if( genericReturnType instanceof TypeVariable ) {//e.g. T
			return true;
		}
		
		if( !(genericReturnType instanceof ParameterizedType) ) {
			return false;
		}
		
		ParameterizedType pt = (ParameterizedType) genericReturnType;
		Type[] argTypes = pt.getActualTypeArguments();
		if( argTypes == null || argTypes.length != 1 ) {
			return false;
		}
		
		Type argType = argTypes[0];
		if( argType instanceof Class ) { //e.g. List<User>
			return true;
		}else if( argType instanceof TypeVariable ){ //e.g. List<T>
			return true;
		}else if( argType instanceof WildcardType ){ //e.g. List<?>
			return true;
		}else if( argType instanceof GenericArrayType ){ //e.g. List<Object[]>
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * get generic return type.<br>
	 * support return type: e.g. List&lt;User>, List&lt;T>, List&lt;?>, List&lt;Object[]>, List, T, User
	 * @param genericReturnType
	 * @return e.g. User, T, Object[], null
	 */
	protected Type getGenericReturnType(Type genericReturnType) {
		if( !isSupportGenericReturnType(genericReturnType) ) {
			throw new QueryBuildException("the generic return type["+genericReturnType+"] is not supported.");
		}
		
		if( !(genericReturnType instanceof ParameterizedType) ) {
			return null;
		}
		
		ParameterizedType pt = (ParameterizedType) genericReturnType;
		Type[] argTypes = pt.getActualTypeArguments();
		if( argTypes == null || argTypes.length != 1 ) {
			return null;
		}
		
		Type argType = argTypes[0];
		if( argType instanceof Class ) { //e.g. List<User>
			return argType;
		}else if( argType instanceof TypeVariable ){ //e.g. List<T>
			return argType;
		}else if( argType instanceof GenericArrayType ){ //e.g. List<Object[]>
			GenericArrayType gat = (GenericArrayType) argType;
			if( !(gat.getGenericComponentType() instanceof Class) ) {
				return null;
			}
			return Array.newInstance((Class<?>)gat.getGenericComponentType(), 0).getClass();
		}else {
			return null;
		}
	}
	
}
