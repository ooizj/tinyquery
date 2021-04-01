package me.ooi.tinyquery.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.handlers.AbstractListHandler;

import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * <code>ResultSetHandler</code> implementation that converts one
 * <code>ResultSet</code> column into a <code>List</code> of
 * <code>Object</code>s. This class is thread safe.
 *
 * @param <T> The type of the column.
 * @see org.apache.commons.dbutils.ResultSetHandler
 * @since DbUtils 1.1
 */
public class ColumnListHandler<T> extends AbstractListHandler<T> {

	/**
	 * 列对应的java类型
	 */
	private Class<T> columnMappingType;

	/**
	 * The column number to retrieve.
	 */
	private final int columnIndex;

	/**
	 * The column name to retrieve. Either columnName or columnIndex will be used
	 * but never both.
	 */
	private final String columnName;

	/**
	 * Creates a new instance of ColumnListHandler. The first column of each row
	 * will be returned from <code>handle()</code>.
	 */
	public ColumnListHandler(Class<T> columnMappingType) {
		this(1, null, columnMappingType);
	}

	/**
	 * Creates a new instance of ColumnListHandler.
	 *
	 * @param columnIndex The index of the column to retrieve from the
	 *                    <code>ResultSet</code>.
	 */
	public ColumnListHandler(int columnIndex, Class<T> columnMappingType) {
		this(columnIndex, null, columnMappingType);
	}

	/**
	 * Creates a new instance of ColumnListHandler.
	 *
	 * @param columnName The name of the column to retrieve from the
	 *                   <code>ResultSet</code>.
	 */
	public ColumnListHandler(String columnName, Class<T> columnMappingType) {
		this(1, columnName, columnMappingType);
	}

	/**
	 * Private Helper
	 * 
	 * @param columnIndex The index of the column to retrieve from the
	 *                    <code>ResultSet</code>.
	 * @param columnName  The name of the column to retrieve from the
	 *                    <code>ResultSet</code>.
	 */
	private ColumnListHandler(int columnIndex, String columnName, Class<T> columnMappingType) {
		super();
		this.columnIndex = columnIndex;
		this.columnName = columnName;
		this.columnMappingType = columnMappingType;
	}

	/**
	 * Returns one <code>ResultSet</code> column value as <code>Object</code>.
	 * 
	 * @param rs <code>ResultSet</code> to process.
	 * @return <code>Object</code>, never <code>null</code>.
	 *
	 * @throws SQLException       if a database access error occurs
	 * @throws ClassCastException if the class datatype does not match the column
	 *                            type
	 *
	 * @see org.apache.commons.dbutils.handlers.AbstractListHandler#handle(ResultSet)
	 */
	// We assume that the user has picked the correct type to match the column
	// so getObject will return the appropriate type and the cast will succeed.
	@Override
	protected T handleRow(ResultSet rs) throws SQLException {
		if (this.columnName == null) {
			return convert(rs.getObject(this.columnIndex));
		}
		return convert(rs.getObject(this.columnName));
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
