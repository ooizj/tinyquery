package me.ooi.tinyquery;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface ConnectionFactory {
	
	Connection getConnection(DataSource dataSource) throws SQLException;

	void closeConnection(Connection connection) throws SQLException;
	
}
