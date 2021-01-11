package me.ooi.tinyquery.interceptor.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.util.ReflectUtils;
import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 */
public class BaseInsertInterceptor extends AbstractBaseQueryInterceptor{
	
	public static final String DEF_KEY_IDFIELD = "idField";
	public static final String DEF_KEY_IDCOLUMN_NAME = "idColumnName";
	private static final String PLACE_HOLDER = "?";
	
	@Override
	public boolean accept(QueryDefinition queryDefinition, Method method) {
		return false; //add by annotation
	}
	
	@Override
	public void prepare(QueryDefinition queryDefinition, Method method) {
		super.prepare(queryDefinition, method);
		
		Field idField = EntityUtils.getIdField((Class<?>) queryDefinition.get(DEF_KEY_ENTITY_CLASS));
		queryDefinition.put(DEF_KEY_IDFIELD, idField);
		queryDefinition.put(DEF_KEY_IDCOLUMN_NAME, EntityUtils.beanFieldToDbField(idField.getName()));
	}

	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		Object ret = null;
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		//insert entity
		Object entity = context.getArgs()[0]; 
		if( entity == null ) {
			throw new QueryExecutionContextBuildException("entity is null.");
		}
		if( entity.getClass() != queryDefinition.get(DEF_KEY_ENTITY_CLASS) ) {
			throw new QueryExecutionContextBuildException("expect entity type is "+
					queryDefinition.get(DEF_KEY_ENTITY_CLASS)+" but found "+entity.getClass()+" .");
		}
		
		//check id field
		Field idField = (Field) queryDefinition.get(DEF_KEY_IDFIELD);
		if( ReflectUtils.getFieldValue(entity, idField) != null ) {
			throw new QueryExecutionContextBuildException("id is not null.");
		}
		
		//insert into field name list
		List<String> insertFieldNames = new ArrayList<String>();
		
		//insert into value literal list
		List<String> insertValueLiterals = new ArrayList<String>();
		
		//insert into value list
		List<Object> insertArguments = new ArrayList<Object>();
		
		//new id value
		Object id = null;
		
		//is generate id before insert
		boolean isGenerateIdBeforeInsert = ServiceRegistry.INSTANCE.getIdGenerator().isGenerateIdBeforeInsert();
				
		//generate id for entity
		if( isGenerateIdBeforeInsert ) {
			if( idField == null ) {
				throw new QueryExecutionContextBuildException("id is unknown.");
			}
			id = ServiceRegistry.INSTANCE.getIdGenerator().generateId(entity, idField);
			
			//add id field
			insertFieldNames.add((String) queryDefinition.get(DEF_KEY_IDCOLUMN_NAME));
			insertValueLiterals.add(PLACE_HOLDER);
			insertArguments.add(id);
		}
		
		//add other fields
		Map<String, Object> fields = EntityUtils.getNotNullField(entity);
		for (Entry<String, Object> entry : fields.entrySet()) {
			insertFieldNames.add(EntityUtils.beanFieldToDbField(entry.getKey()));
			insertValueLiterals.add(PLACE_HOLDER);
			insertArguments.add(entry.getValue());
		}
		
		String tableName = (String) queryDefinition.get(DEF_KEY_TABLE_NAME);
		String sql = String.format("insert into %s(%s) values(%s)", tableName, StringUtils.join(insertFieldNames.iterator(), ","), StringUtils.join(insertValueLiterals.iterator(), ","));
		context.setQuery(sql);
		
		Object[] useArguments = insertArguments.toArray(new Object[insertArguments.size()]);
		context.setArgs(useArguments);
		
		ret = invocation.invoke();
		
		if( !isGenerateIdBeforeInsert ) {
			id = ServiceRegistry.INSTANCE.getIdGenerator().generateId(entity, idField);
		}
		
		//set new id to entity
		ReflectUtils.setFieldValue(entity, idField, TypeConvertUtils.convert(id, idField.getType()));
		
		return ret;
	}

}
