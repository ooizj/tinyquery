package me.ooi.tinyquery.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author jun.zhao
 */
public class ExceptionUtil {

	private ExceptionUtil() {
	}

	public static Throwable unwrapThrowable(Throwable wrapped) {
		Throwable unwrapped = wrapped;
		while (true) {
			if (unwrapped instanceof InvocationTargetException) {
				unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
			} else if (unwrapped instanceof UndeclaredThrowableException) {
				unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
			} else {
				return unwrapped;
			}
		}
	}

}