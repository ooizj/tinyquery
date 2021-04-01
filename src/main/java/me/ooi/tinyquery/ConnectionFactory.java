package me.ooi.tinyquery;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author jun.zhao
 */
public interface ConnectionFactory {
	
	Connection getConnection(DataSource dataSource) throws SQLException;

	void closeConnection(Connection connection) throws SQLException;
	
}
