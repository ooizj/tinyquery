package me.ooi.tinyquery;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jun.zhao
 */
public class QueryProxyManager {
	
	private QueryInterfaceValidator queryInterfaceValidator = new QueryInterfaceValidator();
	
	private Map<String, Object> proxyCache = new ConcurrentHashMap<String, Object>() ;

    @SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> queryInterface) {
        String key = getKey(queryInterface) ;
        T obj = (T) proxyCache.get(key);
        if( obj == null ) {
        	obj = createProxyObject(queryInterface);
            proxyCache.put(key, obj) ;
        }
        return obj;
    }
    
    private String getKey(Class<?> queryInterface){
        return queryInterface.getName();
    }

    @SuppressWarnings("unchecked")
	private <T> T createProxyObject(Class<T> queryInterface) {
    	
    	queryInterfaceValidator.validate(queryInterface);
    	
		return (T) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(), 
				new Class[]{queryInterface}, 
				new QueryInvocationHandler(queryInterface));
	}
    
}
