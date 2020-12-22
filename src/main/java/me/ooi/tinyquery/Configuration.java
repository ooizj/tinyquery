package me.ooi.tinyquery;

import java.util.Map;

import javax.sql.DataSource;

/**
 * @author jun.zhao
 */
public class Configuration {
	
	public static final String DBTYPE_MYSQL = "mysql";
	public static final String DBTYPE_ORACLE = "oracle";
	
	private DataSource dataSource;
	private String dbtype;
	private Map<String, Object> props;
	
	public void init() {
		ServiceRegistry.INSTANCE.init(this);
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public Map<String, Object> getProps() {
		return props;
	}
	public void setProps(Map<String, Object> props) {
		this.props = props;
	}
	
	public Object get(String key) {
		if( props != null ) {
			return props.get(key);
		}else {
			return null;
		}
	}

}
