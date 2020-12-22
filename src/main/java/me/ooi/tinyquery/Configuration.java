package me.ooi.tinyquery;

import javax.sql.DataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface Configuration {
	
	DataSource getDataSource();

}
