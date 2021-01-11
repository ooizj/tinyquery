package me.ooi.tinyquery.spring;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import me.ooi.tinyquery.ConnectionFactory;

/**
 * @author jun.zhao
 */
public class SpringJdbcConnectionFactory implements ConnectionFactory {

	@Override
	public Connection getConnection(DataSource dataSource) throws SQLException {
		return DataSourceUtils.getConnection(dataSource);
	}

	@Override
	public void closeConnection(Connection connection) throws SQLException {
	}

}
