package me.ooi.tinyquery.base.idgenerator;

import java.lang.reflect.Field;

import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.Task;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class OracleAfterInsertTask implements Task{
	
	private Object entity;
	private Field idField;
	private Object generatedId;

	public OracleAfterInsertTask(Object entity, Field idField, Object generatedId) {
		super();
		this.entity = entity;
		this.idField = idField;
		this.generatedId = generatedId;
	}

	@Override
	public void execute() {
		idField.setAccessible(true);
		try {
			idField.set(entity, generatedId);
		} catch (Exception e) {
			throw new QueryExecutionException(e);
		}
	}

}
