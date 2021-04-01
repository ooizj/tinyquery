package me.ooi.tinyquery.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.ooi.tinyquery.QuerySource;

/**
 * @author jun.zhao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Update {
	
	/**
	 * 来源
	 * @return
	 */
	QuerySource source() default QuerySource.ANNOTATION;

	/**
	 * SQL
	 * @return
	 */
	String value() default "";

}
