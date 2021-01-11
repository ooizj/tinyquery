package me.ooi.tinyquery.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ColumnHandler;

/**
 * @author jun.zhao
 */
public class UtilDateColumnHandler implements ColumnHandler {
	
    @Override
    public boolean match(Class<?> propType) {
        return propType.equals(java.util.Date.class);
    }

    @Override
    public Object apply(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getTimestamp(columnIndex);
    }
}
