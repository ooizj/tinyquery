package me.ooi.tinyquery.base.idgenerator;

import java.lang.reflect.Field;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.annotation.Id;
import me.ooi.tinyquery.base.EntityUtils;
import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class OracleIdGenerator implements IdGenerator {
	
	@Override
	public Object generateId(QueryExecutionContext context) {
		
		//insert entity
		Object entity = context.getArgs()[0]; 
		if( entity == null ) {
			throw new QueryExecutionContextBuildException("entity is null.");
		}
		
		//get id field
		Field idField = EntityUtils.getIdField(entity.getClass());
		if( idField == null ) {
			throw new QueryExecutionContextBuildException("id is unknown.");
		}
		
		//generate id for entity
		Object id;
		try {
			id = ServiceRegistry.INSTANCE.getQueryExecutor().execute(idGeneratorExecutionContext(idField));
		} catch (SQLException e) {
			throw new QueryExecutionException(e);
		}
		//添加一个在“execute”方法执行后之后的设置ID的任务，关于异常回滚ID为null暂时不考虑
		context.getAfterExecutionTasks().offer(new OracleAfterInsertTask(entity, idField, TypeConvertUtils.convert(id, idField.getType())));
		
		return id;
	}
	
	private QueryExecutionContext idGeneratorExecutionContext(Field idField) {
		String sequence = idField.getAnnotation(Id.class).sequence();
		Class<?> idFieldType = idField.getType();
		
		if( StringUtils.isEmpty(sequence) ) {
			throw new QueryExecutionContextBuildException("sequence is null");
		}
		
		QueryExecutionContext idGeneratorExecutionContext = new QueryExecutionContext();
		
		QueryDefinition def = new QueryDefinition();
		idGeneratorExecutionContext.setQueryDefinition(def);
		Object[] arguments = new Object[0];
		idGeneratorExecutionContext.setArgs(arguments);
		
		def.setKey("sequence_"+sequence);
		def.setType(QueryDefinition.Type.SELECT);
		def.setQuery("select "+sequence+".nextval from dual");
		def.setReturnType(idFieldType);
		def.setGenericReturnType(idFieldType);
		
		return idGeneratorExecutionContext;
	}

}
