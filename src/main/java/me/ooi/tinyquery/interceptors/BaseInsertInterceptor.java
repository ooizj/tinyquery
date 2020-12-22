package me.ooi.tinyquery.interceptors;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import lombok.Data;
import me.ooi.tinyquery.Invocation;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.QueryExecutionException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.base.EntityUtils;
import me.ooi.tinyquery.util.ReflectUtils;
import me.ooi.typeconvertor.TypeConvertUtils;

/**
 * @author jun.zhao
 */
public class BaseInsertInterceptor extends AbstractBaseQueryInterceptor{
	
	private static final String PLACE_HOLDER = "?";
	
	private static Map<String, BaseInsertDefinition> baseInsertDefinitionCache = new HashMap<String, BaseInsertDefinition>();
	
	@Data
	public static class BaseInsertDefinition{
		private Field idField;
		private String idColumnName;
	}
	
	@Override
	public boolean accept(QueryDefinition queryDefinition) {
		return false; //add by annotation
	}
	
	@Override
	public void prepare(QueryDefinition queryDefinition) {
		super.prepare(queryDefinition);
		
		BaseQueryDefinition baseQueryDefinition = getBaseQueryDefinition(queryDefinition);
		
		BaseInsertDefinition baseInsertDefinition = new BaseInsertDefinition();
		Field idField = EntityUtils.getIdField(baseQueryDefinition.getEntityClass());
		baseInsertDefinition.setIdField(idField);
		baseInsertDefinition.setIdColumnName(EntityUtils.beanFieldToDbField(idField.getName()));
		baseInsertDefinitionCache.put(queryDefinition.getKey(), baseInsertDefinition);
	}

	@Override
	public Object intercept(Invocation invocation) throws SQLException {
		Object ret = null;
		QueryExecutionContext context = invocation.getQueryExecutionContext();
		QueryDefinition queryDefinition = context.getQueryDefinition();
		BaseQueryDefinition baseQueryDefinition = getBaseQueryDefinition(queryDefinition);
		BaseInsertDefinition baseInsertDefinition = baseInsertDefinitionCache.get(queryDefinition.getKey());
		
		//是否在新增之前生成ID
		boolean isGenerateIdBeforeInsert = ServiceRegistry.INSTANCE.getIdGenerator().isGenerateIdBeforeInsert();
		
		//新增的数据主键ID值
		Object id = null;
		
		//insert entity
		Object entity = context.getArgs()[0]; 
		if( entity == null ) {
			throw new QueryExecutionContextBuildException("entity is null.");
		}
		if( entity.getClass() != baseQueryDefinition.getEntityClass() ) {
			throw new QueryExecutionContextBuildException("expect entity type is "+
					baseQueryDefinition.getEntityClass()+" but found "+entity.getClass()+" .");
		}
		
		//check id field
		Field idField = baseInsertDefinition.getIdField();
		if( ReflectUtils.getFieldValue(entity, idField) != null ) {
			throw new QueryExecutionContextBuildException("id is not null.");
		}
		
		//insert into field name list
		List<String> insertFieldNames = new ArrayList<String>();
		
		//insert into value literal list
		List<String> insertValueLiterals = new ArrayList<String>();
		
		//insert into value list
		List<Object> insertArguments = new ArrayList<Object>();
		
		//generate id for entity
		if( isGenerateIdBeforeInsert ) {
			if( idField == null ) {
				throw new QueryExecutionContextBuildException("id is unknown.");
			}
			id = ServiceRegistry.INSTANCE.getIdGenerator().generateId(entity, idField);
			
			//add id field
			insertFieldNames.add(baseInsertDefinition.getIdColumnName());
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
		
		String tableName = baseQueryDefinition.getTableName();
		String sql = String.format("insert into %s(%s) values(%s)", tableName, StringUtils.join(insertFieldNames.iterator(), ","), StringUtils.join(insertValueLiterals.iterator(), ","));
		context.setQuery(sql);
		
		Object[] useArguments = insertArguments.toArray(new Object[insertArguments.size()]);
		context.setArgs(useArguments);
		
		ret = invocation.invoke();
		
		if( !isGenerateIdBeforeInsert ) {
			id = ServiceRegistry.INSTANCE.getIdGenerator().generateId(entity, idField);
		}
		
		//设置新增的主键ID到Entity
		idField.setAccessible(true);
		try {
			idField.set(entity, TypeConvertUtils.convert(id, idField.getType()));
		} catch (Exception e) {
			throw new QueryExecutionException(e);
		}
		
		return ret;
	}

}
