package me.ooi.tinyquery.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.annotation.Interceptors;

/**
 * @author jun.zhao
 */
public class InterceptorUtils {
	
	@SuppressWarnings("unchecked")
	public static <T extends Interceptor> T getInterceptor(QueryDefinition queryDefinition, Class<T> interceptorClass) {
		for (Interceptor interceptor : queryDefinition.getInterceptors()) {
			if( interceptorClass == interceptor.getClass() ) {
				return (T) interceptor;
			}
		}
		return null;
	}
	
	/**
	 * get all of Interceptors and prepare
	 * @param queryDefinition
	 * @return
	 */
	public static Interceptor[] getInterceptors(QueryDefinition queryDefinition, Method method) {
		List<Interceptor> allInterceptor = new ArrayList<Interceptor>();
		List<Class<? extends Interceptor>> annotationInterceptorClasses = new ArrayList<Class<? extends Interceptor>>();
		
		if( method != null ) {
			if( method.isAnnotationPresent(Interceptors.class) ) {
				Interceptors interceptors = method.getAnnotation(Interceptors.class);
				annotationInterceptorClasses.addAll(Arrays.asList(interceptors.value()));
			}
		}
		
		//add interceptors order by META-INF/services/me.ooi.tinyquery.Interceptor 
		for (Interceptor interceptor : ServiceRegistry.INSTANCE.getInterceptors()) {
			
			//add annotation interceptors
			for (Class<? extends Interceptor> annotationInterceptorClass : annotationInterceptorClasses) {
				if( annotationInterceptorClass.isInstance(interceptor) ) {
					interceptor.prepare(queryDefinition, method);
					allInterceptor.add(interceptor);
				}
			}
			
			if( (!allInterceptor.contains(interceptor)) && interceptor.accept(queryDefinition, method) ) {
				interceptor.prepare(queryDefinition, method);
				allInterceptor.add(interceptor);
			}
		}
		
		Interceptor[] ret = new Interceptor[allInterceptor.size()];
		allInterceptor.toArray(ret);
		return ret;
	}

}
