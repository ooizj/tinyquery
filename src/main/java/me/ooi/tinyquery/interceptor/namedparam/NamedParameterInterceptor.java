package me.ooi.tinyquery.interceptor.namedparam;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ooi.tinyquery.Interceptor;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryBuildException;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.util.ClassUtils;

/**
 * 命名参数注入（e.g. #{name}）<br>
 * 由于涉及到参数位置，所以必须放到其它<code>Interceptor</code>前面
 * @author jun.zhao
 */
public class NamedParameterInterceptor implements Interceptor {
	
	public static final String DEF_KEY_NAMED_PARAM_INDEX_LIST = "namedParamIndexList";
	public static final String DEF_KEY_NAMED_PARAM_START_INDEX = "namedParamStartIndex";
	public static final String DEF_KEY_NAMED_PARAM_END_INDEX = "namedParamEndIndex";
	
	private static final String PLACE_HOLDER = "?";
	
	//#\{([a-zA-Z][a-zA-Z_0-9]*)\}	e.g. #{name}
	private final Pattern PATTERN_NAME_PARAMETER = Pattern.compile("#\\{([a-zA-Z][a-zA-Z_0-9]*)\\}");
	
	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
		if( method == null ) {
			return false;
		}
		return ClassUtils.hasAnnotation(method, Param.class);
	}

	@Override
	public void prepare(QueryDefinition queryDefinition, Method method) {
		int paramCount = method.getParameterTypes().length;
		
		//检查被Param注解的参数是否为连续的
		int lastIndex = -1;
		int namedParamStartIndex = -1;
		for (int i = 0; i < paramCount; i++) {
			Param param = ClassUtils.getAnnotationByIndex(method, i, Param.class);
			if( param != null ) {
				if( lastIndex == -1 ) {
					namedParamStartIndex = i;
				}else {
					if( lastIndex+1 != i ) {
						throw new QueryBuildException("parameter of annotated by Param must be continuity");
					}
				}
				lastIndex = i ; 
			}
		}
		int namedParamEndIndex = lastIndex;
		
		//替换query中的参数为“?”，并保存参数名列表
		List<String> methodParamNameList = new ArrayList<String>();
		Matcher matcher = PATTERN_NAME_PARAMETER.matcher(queryDefinition.getQuery());
		StringBuffer stringBuffer = new StringBuffer();
		while( matcher.find() ) {
			methodParamNameList.add(matcher.group(1));
			matcher.appendReplacement(stringBuffer, PLACE_HOLDER);
		}
		matcher.appendTail(stringBuffer);
		queryDefinition.setQuery(stringBuffer.toString());
		
		//key: 参数名; value: 参数在方法中的位置
		Map<String, Integer> methodParamName2IndexMap = new HashMap<String, Integer>();
		for (int i = 0; i < paramCount; i++) {
			Param param = ClassUtils.getAnnotationByIndex(method, i, Param.class);
			if( param != null ) {
				String paramName = param.value();
				methodParamName2IndexMap.put(paramName, i);
			}
		}
		
		//命名参数在方法参数列表中的位置列表
		List<Integer> namedParamIndexList = new ArrayList<Integer>();
		for (String methodParamName : methodParamNameList) {
			Integer methodParamIndex = methodParamName2IndexMap.get(methodParamName);
			namedParamIndexList.add(methodParamIndex);
		}
		queryDefinition.put(DEF_KEY_NAMED_PARAM_INDEX_LIST, namedParamIndexList);
		queryDefinition.put(DEF_KEY_NAMED_PARAM_START_INDEX, namedParamStartIndex);
		queryDefinition.put(DEF_KEY_NAMED_PARAM_END_INDEX, namedParamEndIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		Object[] args = context.getArgs();
		if( args == null ) {
			return invocation.invoke();
		}
		
		QueryDefinition queryDefinition = context.getQueryDefinition();
		List<Integer> namedParamIndexList = (List<Integer>) queryDefinition.get(DEF_KEY_NAMED_PARAM_INDEX_LIST);
		Integer namedParamStartIndex = (Integer) queryDefinition.get(DEF_KEY_NAMED_PARAM_START_INDEX);
		Integer namedParamEndIndex = (Integer) queryDefinition.get(DEF_KEY_NAMED_PARAM_END_INDEX);
		int lengthOfSegment1 = namedParamStartIndex;
		int lengthOfSegment2 = namedParamIndexList.size();
		int lengthOfSegment3 = args.length-namedParamEndIndex-1;
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
		context.setArgs(newArgs);
		return invocation.invoke();
	}

}
