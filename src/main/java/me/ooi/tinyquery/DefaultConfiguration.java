package me.ooi.tinyquery;

import javax.sql.DataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class DefaultConfiguration implements Configuration {
	
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
