package me.ooi.tinyquery;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jun.zhao
 */
public class QueryInterfaceValidator {
	
	public void validate(Class<?> queryInterface) {
		validateRepeatMethodName(queryInterface);
	}
	
	public void validateRepeatMethodName(Class<?> queryInterface) {
		List<String> methodNames = new ArrayList<String>();
		for (Method method : queryInterface.getMethods()) {
			if( methodNames.contains(method.getName()) ) {
				throw new QueryBuildException("queryInterface's method name["+method.getName()+"] is not allowed to be repeated.");
			}else {
				methodNames.add(method.getName());
			}
		}
	}

}
