package me.ooi.tinyquery;


import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import me.ooi.tinyquery.interceptor.base.BaseQuery;
import me.ooi.tinyquery.interceptor.base.Page;
import me.ooi.tinyquery.interceptor.base.PageResult;
import me.ooi.tinyquery.interceptor.criteria.Criteria;
import me.ooi.tinyquery.interceptor.criteria.CriteriaParam;
import me.ooi.tinyquery.interceptor.namedparam.Param;
import me.ooi.tinyquery.util.ClassUtils;
import me.ooi.tinyquery.util.ReflectUtils;

/**
 * @author jun.zhao
 */
@SuppressWarnings("rawtypes")
public class TestTinyquery {
	
	
	@Test
	public void t4() {
		// c0 = ?  and  ( c1 = ?  and  c2 in (?,?) )  and  c3 != ?  and  c4 not in (?,?)  or  c5 = ?  
		Criteria criteria = Criteria.newCriteria()
				.eq("c0", "000").and(	Criteria.newCriteria().eq("c1", "111").in("c2", Arrays.asList("222", "2220"))	)
				.ne("c3", "333")
				.notIn("c4", Arrays.asList("ni444", "ni4440"))
				.or(	Criteria.newCriteria().eq("c5", "555").in("c6", Arrays.asList("666", "6660"))	)
				;
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		criteria.eq("c7", "777");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c0", "000");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.isNull("c8");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		criteria = Criteria.newCriteria()
				.isNotNull("c8");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.isNull("c8").or(new Criteria().eq("c8", ""));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111").and(new Criteria().isNotNull("c8").ne("c8", ""));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111").and(new Criteria().isNull("c8").or(new Criteria().eq("c8", "")));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111").and(new Criteria().eq("c2", 1));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111").or(new Criteria().eq("c2", 1));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
//		criteria = Criteria.newCriteria()
//				.or(new Criteria().eq("c2", 1));
//		System.out.println(criteria.getQuery());
//		System.out.println(criteria.getArgumentList());
		
//		criteria = Criteria.newCriteria()
//				.and(new Criteria().eq("c2", 1)).eq("cc", 10);
//		System.out.println(criteria.getQuery());
//		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria().eq("1", 1).and(
				new me.ooi.tinyquery.interceptor.criteria.Criteria().isNull("cc")
				.or(new me.ooi.tinyquery.interceptor.criteria.Criteria().ne("cc", 0))
			);
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
	}
	
	@Test
	public void t5() throws NoSuchMethodException, SecurityException {
		Method m1 = (new Object(){
			@SuppressWarnings("unused")
			public PageResult<String> m1(){
				return null;
			}
		}).getClass().getMethod("m1");
		System.out.println(m1.getReturnType());
		System.out.println(m1.getGenericReturnType());
	}
	
	@Test
	public void t6() throws NoSuchMethodException, SecurityException {
		Method method = BaseQuery.class.getMethod("selectList", Criteria.class);
		System.out.println(ClassUtils.hasAnnotation(method, CriteriaParam.class));
		System.out.println(ClassUtils.hasParamType(method, Criteria.class));
		System.out.println(ClassUtils.getParamTypeIndex(method, Criteria.class));
		System.out.println(ClassUtils.getAnnotationByIndex(method, 0, CriteriaParam.class));
		System.out.println(ClassUtils.getAnnotationByIndex(method, 1, CriteriaParam.class));
		method = BaseQuery.class.getMethod("selectOne", Criteria.class);
		System.out.println(ClassUtils.hasAnnotation(method, CriteriaParam.class));
		System.out.println(ClassUtils.hasParamType(method, Criteria.class));
		method = BaseQuery.class.getMethod("insert", Object.class);
		System.out.println(method);
		System.out.println(ClassUtils.hasAnnotation(method, CriteriaParam.class));
		System.out.println(ClassUtils.hasParamType(method, Criteria.class));
	}
	
	@Test
	public void t7() throws Exception {
		PageResult pr = new PageResult();
		pr.setTotal(10L);
		System.out.println(ReflectUtils.getPropertyDescriptor(PageResult.class, "total").getReadMethod().invoke(pr));
		System.out.println(pr);
		System.out.println(ReflectUtils.getPropertyDescriptor(PageResult.class, "records"));
		System.out.println(ReflectUtils.getNotNullField(pr));
	}
	
	@Test
	public void t8() throws IOException {
		ImmutableSet<ClassInfo> is;
		
		System.out.println("---------------------------------------------------");
		is = ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("");
		System.out.println(is);
		
		System.out.println("---------------------------------------------------");
		is = ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClasses("me.ooi.tinyquery");
		System.out.println(is);
		for (ClassInfo ci : is) {
			System.out.println(ci);
		}
		
		System.out.println("---------------------------------------------------");
		is = ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("me.ooi.tinyquery");
		System.out.println(is);
		for (ClassInfo ci : is) {
			System.out.println(ci);
		}
		
	}
	
	@Test
	public void t9() {
		PropertyDescriptor[] pds = ReflectUtils.getPropertyDescriptors(Number.class);
		System.out.println(pds[0]);
	}
	
	@Test
	public void t10() {
		System.out.println(Array.newInstance(Object.class, 0).getClass());
		System.out.println(Object[].class);
		System.out.println(ClassUtils.getClass("java.lang.Object[]"));
	}
	
	@Test
	public void t11() throws Exception {
		System.out.println(ClassUtils.hasParamType(BaseQuery.class.getMethod("selectPage", Criteria.class, Page.class), Criteria.class));
	}
	
	@Test
	public void t12() {
		//e.g. add(year, month, criteria, userId)
		int criteriaParamIndex = 2;
		Object[] originArguments = new Object[]{"year", "month", "userId"};
		Object[] criteriaArguments = new Object[]{"criteria_1", "criteria_2"};
		Object[] useArguments = new Object[originArguments.length+criteriaArguments.length];
		int destPos = 0;
		System.arraycopy(originArguments, 0, useArguments, destPos, criteriaParamIndex);
		destPos += criteriaParamIndex;
		System.arraycopy(criteriaArguments, 0, useArguments, destPos, criteriaArguments.length);
		destPos += criteriaArguments.length;
		System.arraycopy(originArguments, criteriaParamIndex, useArguments, destPos, originArguments.length-criteriaParamIndex);
		System.out.println(ToStringBuilder.reflectionToString(useArguments));
		
		//e.g. add(year, month, criteria)
		criteriaParamIndex = 2;
		originArguments = new Object[]{"year", "month"};
		criteriaArguments = new Object[]{"criteria_1", "criteria_2"};
		useArguments = new Object[originArguments.length+criteriaArguments.length];
		destPos = 0;
		System.arraycopy(originArguments, 0, useArguments, destPos, criteriaParamIndex);
		destPos += criteriaParamIndex;
		System.arraycopy(criteriaArguments, 0, useArguments, destPos, criteriaArguments.length);
		destPos += criteriaArguments.length;
		System.arraycopy(originArguments, criteriaParamIndex, useArguments, destPos, originArguments.length-criteriaParamIndex);
		System.out.println(ToStringBuilder.reflectionToString(useArguments));
		
		//e.g. add(criteria)
		criteriaParamIndex = 0;
		originArguments = new Object[]{};
		criteriaArguments = new Object[]{"criteria_1", "criteria_2"};
		useArguments = new Object[originArguments.length+criteriaArguments.length];
		destPos = 0;
		System.arraycopy(originArguments, 0, useArguments, destPos, criteriaParamIndex);
		destPos += criteriaParamIndex;
		System.arraycopy(criteriaArguments, 0, useArguments, destPos, criteriaArguments.length);
		destPos += criteriaArguments.length;
		System.arraycopy(originArguments, criteriaParamIndex, useArguments, destPos, originArguments.length-criteriaParamIndex);
		System.out.println(ToStringBuilder.reflectionToString(useArguments));
		
		//e.g. add(criteria, year, month)
		criteriaParamIndex = 0;
		originArguments = new Object[]{"year", "month"};
		criteriaArguments = new Object[]{"criteria_1", "criteria_2"};
		useArguments = new Object[originArguments.length+criteriaArguments.length];
		destPos = 0;
		System.arraycopy(originArguments, 0, useArguments, destPos, criteriaParamIndex);
		destPos += criteriaParamIndex;
		System.arraycopy(criteriaArguments, 0, useArguments, destPos, criteriaArguments.length);
		destPos += criteriaArguments.length;
		System.arraycopy(originArguments, criteriaParamIndex, useArguments, destPos, originArguments.length-criteriaParamIndex);
		System.out.println(ToStringBuilder.reflectionToString(useArguments));
		
		//e.g. add()
		criteriaParamIndex = 0;
		originArguments = new Object[]{};
		criteriaArguments = new Object[]{};
		useArguments = new Object[originArguments.length+criteriaArguments.length];
		destPos = 0;
		System.arraycopy(originArguments, 0, useArguments, destPos, criteriaParamIndex);
		destPos += criteriaParamIndex;
		System.arraycopy(criteriaArguments, 0, useArguments, destPos, criteriaArguments.length);
		destPos += criteriaArguments.length;
		System.arraycopy(originArguments, criteriaParamIndex, useArguments, destPos, originArguments.length-criteriaParamIndex);
		System.out.println(ToStringBuilder.reflectionToString(useArguments));
	}
	
	@Test
	public void t13() {
		Criteria criteria = new Criteria();
		Object[] originArguments = new Object[]{"year", criteria, "userId"};
		int index = ArrayUtils.indexOf(originArguments, criteria);
		System.out.println(index);
		
		originArguments = new Object[]{"year", criteria, "userId"};
		index = ArrayUtils.indexOf(originArguments, Criteria.class);
		System.out.println(index);
	}
	
	@Test
	public void t14() {
		//#\{([a-zA-Z][a-zA-Z_0-9]*)\}
		Pattern pattern = Pattern.compile("#\\{([a-zA-Z][a-zA-Z_0-9]*)\\}");
		String str = "select * from t1 where name= #{name} and id = #{id} order by id desc ";
		Matcher matcher = pattern.matcher(str);
		StringBuffer stringBuffer = new StringBuffer();
		while( matcher.find() ) {
			System.out.println(matcher.group(1));
			matcher.appendReplacement(stringBuffer, "?");
		}
		matcher.appendTail(stringBuffer);
		System.out.println(stringBuffer);
	}
	
	@Test
	public void t15() {
		
		Method method = (new Object() {
			@SuppressWarnings("unused")
			public void m(String p0, @Param("name") String name, @Param("sex") Integer sex, @Param("id") Integer id, Page page) {}
		}).getClass().getDeclaredMethods()[0];
		Object[] args = new Object[] {11,22,33,44,55};
		System.out.println("args="+Arrays.asList(args));
		
		List<String> methodParamNameList = new ArrayList<String>();
		//#\{([a-zA-Z][a-zA-Z_0-9]*)\}
		Pattern pattern = Pattern.compile("#\\{([a-zA-Z][a-zA-Z_0-9]*)\\}");
		String str = "select * from t1 where name= #{name} and 1=1 and id = #{id} and 1!=2 and first_name=#{name} order by id desc";
		Matcher matcher = pattern.matcher(str);
		StringBuffer stringBuffer = new StringBuffer();
		while( matcher.find() ) {
			methodParamNameList.add(matcher.group(1));
			matcher.appendReplacement(stringBuffer, "?");
		}
		matcher.appendTail(stringBuffer);
		System.out.println(stringBuffer);
		
		int paramCount = method.getParameterTypes().length;
		
		//Continuity check
		int lastIndex = -1;
		int namedParamStartIndex = -1; //first entry index
		for (int i = 0; i < paramCount; i++) {
			Param param = ClassUtils.getAnnotationByIndex(method, i, Param.class);
			System.out.println(i+":"+lastIndex);
			if( param != null ) {
				if( lastIndex == -1 ) {
					namedParamStartIndex = i;
				}else {
					if( lastIndex+1 != i ) {
						System.out.println("error");
						break;
					}
				}
				lastIndex = i ; 
			}
		}
		int namedParamEndIndex = lastIndex;
		System.out.println("in="+namedParamStartIndex+",out="+namedParamEndIndex);
		
		//key: methodParamName; value: methodParamIndex
		Map<String, Integer> methodParamName2IndexMap = new HashMap<String, Integer>();
		
		for (int i = 0; i < paramCount; i++) {
			Param param = ClassUtils.getAnnotationByIndex(method, i, Param.class);
			if( param != null ) {
				String paramName = param.value();
				methodParamName2IndexMap.put(paramName, i);
			}
		}
		
		List<Integer> namedParamIndexList = new ArrayList<Integer>();
		for (String methodParamName : methodParamNameList) {
			Integer methodParamIndex = methodParamName2IndexMap.get(methodParamName);
			namedParamIndexList.add(methodParamIndex);
		}
		System.out.println("namedParamIndexList="+namedParamIndexList);
		
		System.out.print("args in namedParamIndexList=");
		for (Integer namedParamIndex : namedParamIndexList) {
			System.out.print(args[namedParamIndex]+",");
		}
		System.out.println();
		
		
		int lengthOfSegment1 = namedParamStartIndex;
		int lengthOfSegment2 = namedParamIndexList.size();
		int lengthOfSegment3 = args.length-namedParamEndIndex-1;
		System.out.println(lengthOfSegment1+":"+lengthOfSegment2+":"+lengthOfSegment3);
		Object[] newArgs = new Object[lengthOfSegment1+lengthOfSegment2+lengthOfSegment3];
		if( lengthOfSegment1 > 0 ) {
			System.arraycopy(args, 0, newArgs, 0, lengthOfSegment1);
		}
		for (int i = 0; i < namedParamIndexList.size(); i++) {
			newArgs[namedParamStartIndex+i] = args[namedParamIndexList.get(i)];
		}
		if( lengthOfSegment3 > 0 ) {
			System.arraycopy(args, namedParamEndIndex+1, newArgs, lengthOfSegment1+lengthOfSegment2, lengthOfSegment3);
		}
		
		System.out.println("newArgs="+Arrays.asList(newArgs));
	}
	
}
