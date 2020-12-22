package me.ooi.tinyquery.base.idgenerator;

import java.lang.reflect.Field;

import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.base.EntityUtils;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class MysqlIdGenerator implements IdGenerator {

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
		
		//添加一个在“execute”方法执行后之后的设置ID的任务，关于异常回滚ID为null暂时不考虑
		context.getAfterExecutionTasks().offer(new MysqlAfterInsertTask(entity, idField));
		
		return null;
	}
	

}
