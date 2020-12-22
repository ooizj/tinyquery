package me.ooi.tinyquery.base.idgenerator;

import java.lang.reflect.Field;
import java.sql.SQLException;

import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.Task;
import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class MysqlAfterInsertTask implements Task{
	
	private Object entity;
	private Field idField;

	public MysqlAfterInsertTask(Object entity, Field idField) {
		super();
		this.entity = entity;
		this.idField = idField;
	}

	@Override
	public void execute() {
		
		//generate id for entity
		Object id;
		try {
			id = ServiceRegistry.INSTANCE.getQueryExecutor().execute(idGeneratorExecutionContext(idField));
		} catch (SQLException e) {
			throw new QueryExecutionException(e);
		}
		
		idField.setAccessible(true);
		try {
			idField.set(entity, TypeConvertUtils.convert(id, idField.getType()));
		} catch (Exception e) {
			throw new QueryExecutionException(e);
		}
	}
	
	private QueryExecutionContext idGeneratorExecutionContext(Field idField) {
		QueryExecutionContext idGeneratorExecutionContext = new QueryExecutionContext();
		Class<?> idFieldType = idField.getType();
		
		QueryDefinition def = new QueryDefinition();
		idGeneratorExecutionContext.setQueryDefinition(def);
		Object[] arguments = new Object[0];
		idGeneratorExecutionContext.setArgs(arguments);
		
		def.setKey("last_insert_id");
		def.setType(QueryDefinition.Type.SELECT);
		def.setQuery("SELECT LAST_INSERT_ID()");
		def.setReturnType(idFieldType);
		def.setGenericReturnType(idFieldType);
		
		return idGeneratorExecutionContext;
	}

}
