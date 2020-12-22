package me.ooi.tinyquery.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * <code>ResultSetHandler</code> implementation that converts a
 * <code>ResultSet</code> into an <code>T</code>. This class is
 * thread safe.
 *
 * @see org.apache.commons.dbutils.ResultSetHandler
 */
public class SingleColumnResultHandler<T> implements ResultSetHandler<T> {
	
	/**
	 * 列对应的java类型
	 */
	private Class<T> columnMappingType;
    public SingleColumnResultHandler(Class<T> columnMappingType) {
		super();
		this.columnMappingType = columnMappingType;
	}


    @Override
    public T handle(ResultSet rs) throws SQLException {
    	return rs.next() ? convert(rs.getObject(1)) : null;
    }
    
    /**
	 * 类型转换
	 * @param source
	 * @param targetType
	 * @return
	 * @throws ClassCastException
	 */
	@SuppressWarnings("unchecked")
	private T convert(Object source) {
		if( source == null ) {
			return null;
		}
		
		if( columnMappingType.isAssignableFrom(source.getClass()) ) {
			return (T) source;
		}
		
		return (T) TypeConvertUtils.convert(source, columnMappingType); //ClassCastException
	}

}
