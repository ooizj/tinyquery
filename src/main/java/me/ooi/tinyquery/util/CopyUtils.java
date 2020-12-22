package me.ooi.tinyquery.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;

import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class CopyUtils {
	
	/**
	 * <p>Description: 拷贝（注意：当<code>source</code>为Map时包含部分类型的自动转换）</p>
	 * @param source
	 * @param target
	 */
	public static void copy(Object source, Object target){
		copy(source, target, false);
	}
	
	/**
	 * <p>Description: 拷贝（注意：当<code>source</code>为Map时包含部分类型的自动转换）</p>
	 * @param source
	 * @param target
	 */
	public static void copy(Object source, Object target, boolean copyDifferentTypeProperty){
		doCopy(source, target);
		
		if( copyDifferentTypeProperty ){
			copyDefferentTypeAttrs(source, target);
		}
	}
	
	/**
	 * <p>Description: 深度拷贝，如果是数组和集合且类型相同泛型相同也进行拷贝（注意：当<code>source</code>为Map时包含部分类型的自动转换）</p>
	 * @param source
	 * @param target
	 */
	public static void deepCopy(Object source, Object target, boolean copyDifferentTypeProperty){
		doCopy(source, target);
		
		if( copyDifferentTypeProperty ){
			
			PropertyDescriptor[] pds = getPropertyDescriptors(source.getClass()) ;
			Map<String, PropertyDescriptor> targetPropertyDescriptorMap = getPropertyDescriptorMap(target);
			for (PropertyDescriptor sourcePd : pds) {
				PropertyDescriptor targetPd = targetPropertyDescriptorMap.get(sourcePd.getName());
				if( targetPd == null ){
					continue;
				}
				
				//数组
				doDeepCopyArray(source, target, sourcePd, targetPd, copyDifferentTypeProperty);
				
				//集合
				doDeepCopyCollection(source, target, sourcePd, targetPd, copyDifferentTypeProperty);
			}
			
			copyDefferentTypeAttrs(source, target);
		}
	}
	
	/**
	 * <p>Description: 拷贝，支持map类型的拷贝（注意：当<code>source</code>为Map时包含部分类型的自动转换）</p>
	 * @param source
	 * @param target
	 */
	public static void copy(Object source, Object target, String ...ignoreProperties){
		doCopy(source, target, ignoreProperties);
	}
	
	/**
	 * <p>Description: 拷贝数组</p>
	 * @param source
	 * @param target
	 * @param sourcePd
	 * @param targetPd
	 */
	private static void doDeepCopyArray(Object source, Object target, PropertyDescriptor sourcePd, PropertyDescriptor targetPd, boolean copyDifferentTypeProperty){
		if( sourcePd.getPropertyType().isArray() && targetPd.getPropertyType().isArray() ){
			
			try {
				Object val = sourcePd.getReadMethod().invoke(source, new Object[]{});
				
				if( val != null ){
					int length = Array.getLength(val);
					Object targetArray = Array.newInstance(targetPd.getPropertyType().getComponentType(), length);
					for (int i = 0; i < length; i++) {
						Object sourceItem = Array.get(val, i);
						Object targetItem = targetPd.getPropertyType().getComponentType().newInstance();
						deepCopy(sourceItem, targetItem, copyDifferentTypeProperty);
						Array.set(targetArray, i, targetItem);
					}
					targetPd.getWriteMethod().invoke(target, new Object[]{targetArray});
				}
				
			} catch (Exception e) {
				throw new CopyException(e);
			}
		}
	}
	
	/**
	 * <p>Description: 拷贝集合，必须类型相同，泛型相同</p>
	 * @param source
	 * @param target
	 * @param sourcePd
	 * @param targetPd
	 * @param copyDifferentTypeProperty
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void doDeepCopyCollection(Object source, Object target, PropertyDescriptor sourcePd, PropertyDescriptor targetPd, boolean copyDifferentTypeProperty){
		
		if( Collection.class.isAssignableFrom(sourcePd.getPropertyType()) && Collection.class.isAssignableFrom(targetPd.getPropertyType()) ){
			
			Collection targetCollection = createSupportCollection(targetPd.getPropertyType());
			if( targetCollection == null ){
				return;
			}
			
			Type targetGenericParameterType = null;
			if( hasGenericParameterType(targetPd.getWriteMethod()) ){
				targetGenericParameterType = targetPd.getWriteMethod().getGenericParameterTypes()[0];
				if( !(targetGenericParameterType instanceof Class) ){
					return;
				}
			}
			
			try {
				Object val = sourcePd.getReadMethod().invoke(source, new Object[]{});
				if( val != null ){
					
					
					Collection sourceCollection = (Collection) val;
					for (Object sourceItem : sourceCollection) {
						if( sourceItem != null ){
							Type parameterType = targetGenericParameterType;
							if( parameterType == null ){
								parameterType = sourceItem.getClass();
							}
							Object targetItem = ((Class)parameterType).newInstance();
							deepCopy(sourceItem, targetItem, copyDifferentTypeProperty);
							targetCollection.add(targetItem);
						}
					}
					targetPd.getWriteMethod().invoke(target, new Object[]{targetCollection});
				}
			} catch (Exception e) {
				throw new CopyException(e);
			}
		}
	}
	
	private static boolean hasGenericParameterType(Method method){
		if( method.getGenericParameterTypes() == null || method.getGenericParameterTypes().length == 0 ){
			return false;
		}
		Type genericParameterType = method.getGenericParameterTypes()[0];
		if( method.getParameterTypes() != null && method.getParameterTypes().length >0 && method.getParameterTypes()[0] == genericParameterType ){
			return false;
		}
		
		return true;
	}
	
	/**
	 * <p>Description: 根据class创建集合类（如果支持的话，目前支持List、AbstractList、Queue、Deque、Set、AbstractSet、SortedSet）</p>
	 * @param clzz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static Collection createSupportCollection(Class clzz){
		if( !Collection.class.isAssignableFrom(clzz) ){
			return null;
		}
		
		if( (!clzz.isInterface()) && (!Modifier.isAbstract(clzz.getModifiers())) && (!clzz.isEnum()) ){
			try {
				return (Collection) clzz.newInstance();
			} catch (Exception e) {
				throw new CopyException(e);
			}
		}
		
		if( clzz == List.class || clzz == AbstractList.class ){
			return new ArrayList();
		}else if( clzz == Queue.class || clzz == Deque.class ){
			return new LinkedList();
		}else if( clzz == Set.class || clzz == AbstractSet.class ){
			return new HashSet();
		}else if( clzz == SortedSet.class ){
			return new TreeSet();
		}
		
		return null;
	}
	
	/**
	 * <p>Description: 拷贝，忽略NULL属性（注意：当<code>source</code>为Map时包含部分类型的自动转换）</p>
	 * @param source
	 * @param target
	 */
	public static void copyIgnoreNullProperty(Object source, Object target){
		copyIgnoreNullProperty(source, target, false);
	}
	
	/**
	 * <p>Description: 拷贝，忽略NULL属性（注意：当<code>source</code>为Map时包含部分类型的自动转换）</p>
	 * @param source
	 * @param target
	 */
	public static void copyIgnoreNullProperty(Object source, Object target, boolean copyDifferentTypeProperty){
		doCopy(source, target, getNullProperties(source));
		
		if( copyDifferentTypeProperty ){
			copyDefferentTypeAttrs(source, target);
		}
	}
	
	/**
	 * <p>Description: 拷贝不同类型的字段</p>
	 * @param source
	 * @param target
	 */
	public static void copyDefferentTypeAttrs(Object source, Object target){
		PropertyDescriptor[] pds = getPropertyDescriptors(source.getClass()) ;
		Map<String, PropertyDescriptor> targetPropertyDescriptorMap = getPropertyDescriptorMap(target);
		for (PropertyDescriptor sourcePd : pds) {
			PropertyDescriptor targetPd = targetPropertyDescriptorMap.get(sourcePd.getName());
			if( targetPd != null ){
				if( targetPd.getPropertyType() != sourcePd.getPropertyType() ){
					try {
						Object sourceAttrValue = sourcePd.getReadMethod().invoke(source, new Object[]{});
						Object targetAttrValue = convert(sourceAttrValue, sourcePd.getPropertyType(), targetPd.getPropertyType());
						if( targetAttrValue != null ){
							targetPd.getWriteMethod().invoke(target, new Object[]{targetAttrValue});
						}
					} catch (Exception e) {
						throw new CopyException(e);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T convert(Object sourceValue, Class<?> sourceType, Class<T> targetType){
		return (T) TypeConvertUtils.convert(sourceValue, targetType);
	}
	
	private static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Object obj){
		Map<String, PropertyDescriptor> ret = new HashMap<String, PropertyDescriptor>();
		PropertyDescriptor[] pds = getPropertyDescriptors(obj.getClass()) ;
		for (PropertyDescriptor pd : pds) {
			ret.put(pd.getName(), pd);
		}
		return ret;
	}
	
	private static String[] getNullProperties(Object obj){
		List<String> nullProperties = new ArrayList<String>();
		PropertyDescriptor[] pds = getPropertyDescriptors(obj.getClass()) ;
		for (PropertyDescriptor pd : pds) {
			Method readMethod = pd.getReadMethod();
			if( readMethod != null ){
				Object val;
				try {
					val = readMethod.invoke(obj, new Object[]{});
					if( val == null ){
						nullProperties.add(pd.getName());
					}
				} catch (Exception e) {
					throw new CopyException(e);
				}
			}
		}
		String[] ret = new String[nullProperties.size()];
		nullProperties.toArray(ret);
		return ret;
	}
	
	/**
	 * <p>Description: 拷贝，支持map类型的拷贝（注意：当<code>source</code>为Map时包含部分类型的自动转换）</p>
	 * @param source
	 * @param target
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void doCopy(Object source, Object target, String ...ignoreProperties){
		if( source == null || target == null ){
			return;
		}
		
		if( Map.class.isAssignableFrom(target.getClass()) ){ //target is map
			Map targetMap = (Map) target;
			
			if( Map.class.isAssignableFrom(source.getClass()) ){ //source is map
				Map sourceMap = (Map) source;
				putMap(sourceMap, targetMap, ignoreProperties);
			}else { //source is not map
				targetMap.putAll(beanToMap(source, ignoreProperties));
			}
		}else { //target is not map
			
			if( Map.class.isAssignableFrom(source.getClass()) ){ //source is map
				Map sourceMap = (Map) source;
				mapToBean(sourceMap, target);
			}else { //source is not map
				doCopy0(source, target, ignoreProperties);
			}
		}
	}
	
	private static void doCopy0(Object source, Object target, String ...ignoreProperties){
		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null &&
					(ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						Method writeMethod = targetPd.getWriteMethod();
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}
						writeMethod.invoke(target, value);
					}
					catch (Throwable ex) {
						throw new CopyException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void putMap(Map sourceMap, Map targetMap, String ...ignoreProperties) {
		List<String> ignorePropertieList = (ignoreProperties == null)?new ArrayList<String>():Arrays.asList(ignoreProperties);
		for (Object key : sourceMap.keySet()) {
			if( ignorePropertieList.contains(key) ) {
				continue;
			}
			
			targetMap.put(key, sourceMap.get(key));
		}
	}
	
	/**
	 * <p>Description: 将Object转换为Map</p>
	 * @param source
	 * @param target
	 */
	public static Map<String, Object> beanToMap(Object source, String ...ignoreProperties){
		List<String> ignorePropertieList = (ignoreProperties == null)?new ArrayList<String>():Arrays.asList(ignoreProperties);
		
		Map<String, Object> map = new HashMap<String, Object>();
		PropertyDescriptor[] pds = getPropertyDescriptors(source.getClass()) ;
		for (PropertyDescriptor sourcePd : pds) {
			
			if( ignorePropertieList.contains(sourcePd.getName()) ){
				continue;
			}
			
			Method readMethod = sourcePd.getReadMethod();
			if( readMethod != null ){
				if(readMethod.getDeclaringClass() == Object.class ){
					continue;
				}
				
				try {
					Object value = readMethod.invoke(source, new Object[]{});
					if( value != null ){
						map.put(sourcePd.getName(), value);
					}
				} catch (Exception e) {
					throw new CopyException(e);
				}
			}
		}
		return map;
	}
	
	/**
	 * <p>Description: 将Map转换为Object（注意：包含部分类型的自动转换）</p>
	 * @param source
	 * @param target
	 */
	@SuppressWarnings("rawtypes")
	public static void mapToBean(Map source, Object target){
		Map<String, PropertyDescriptor> targetPropertyDescriptorMap = getPropertyDescriptorMap(target);
		for (Object keyObj : source.keySet()) {
			if( keyObj == null ){
				continue;
			}
			Object value = source.get(keyObj);
			if( value == null ){
				continue;
			}
			
			PropertyDescriptor pd = targetPropertyDescriptorMap.get(keyObj.toString());
			if( pd == null ){
				continue;
			}
			
			if( pd.getWriteMethod() != null ){
				if( pd.getWriteMethod().getDeclaringClass() != Object.class ){
					
					if( pd.getPropertyType() != value.getClass() ){
						value = convert(value, value.getClass(), pd.getPropertyType());
					}
					if( value == null ){
						continue;
					}
					
					try {
						pd.getWriteMethod().invoke(target, new Object[]{value});
					} catch (Exception e) {
						throw new CopyException(e);
					}
				}
			}
		}
	}
	
	
	private static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String name) {
		try {
			return PropertyUtils.getPropertyDescriptor(clazz, name);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
		return PropertyUtils.getPropertyDescriptors(clazz);
	}
	
}
