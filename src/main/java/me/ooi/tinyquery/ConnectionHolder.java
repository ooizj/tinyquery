package me.ooi.tinyquery;

import java.sql.Connection;

/**
 * @author jun.zhao
 */
public class ConnectionHolder {
	
	private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<Connection>();
	
	public static void set(Connection conn) {
		connectionThreadLocal.set(conn);
	}
	
	public static void remote() {
		connectionThreadLocal.remove();
	}
	
	public static Connection get() {
		return connectionThreadLocal.get();
	}

}
