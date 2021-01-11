package me.ooi.tinyquery;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author jun.zhao
 */
public class DefaultConnectionFactory implements ConnectionFactory {

	@Override
	public Connection getConnection(DataSource dataSource) throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}

}
