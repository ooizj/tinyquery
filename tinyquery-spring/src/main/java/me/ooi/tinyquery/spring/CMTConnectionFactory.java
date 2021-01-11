package me.ooi.tinyquery.spring;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import me.ooi.tinyquery.ConnectionFactory;

/**
 * @author jun.zhao
 */
public class CMTConnectionFactory implements ConnectionFactory {

	@Override
	public Connection getConnection(DataSource dataSource) throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}

}
