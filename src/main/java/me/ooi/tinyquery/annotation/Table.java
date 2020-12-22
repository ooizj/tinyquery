package me.ooi.tinyquery.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
	
	/**
	 * (Optional) The name of the table.
	 * <p/>
	 * Defaults to the entity name.
	 */
	String name();

}
