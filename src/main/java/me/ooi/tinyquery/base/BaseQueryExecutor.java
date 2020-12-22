package me.ooi.tinyquery.base;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import me.ooi.tinyquery.DefaultQueryExecutor;
import me.ooi.tinyquery.QueryDefinition;
import me.ooi.tinyquery.QueryExecutionContext;
import me.ooi.tinyquery.QueryExecutionContextBuildException;
import me.ooi.tinyquery.ServiceRegistry;
import me.ooi.tinyquery.criteria.Criteria;
import me.ooi.tinyquery.util.ReflectUtils;

/**
 * @author jun.zhao
 */
public class BaseQueryExecutor extends DefaultQueryExecutor{
	
	private static final String PLACE_HOLDER = "?";
	
	private RecordCountGenerator recordCountGenerator = new RecordCountGenerator();
	
	@Override
	protected void beforeExecute(QueryExecutionContext context) {
		super.beforeExecute(context);
		
		QueryDefinition queryDefinition = context.getQueryDefinition();
		if( queryDefinition.getMethodDeclaringClass() == BaseQuery.class ) {
			if( BaseQuery.METHOD_SELECT_LIST.equals(queryDefinition.getMethodName()) || BaseQuery.METHOD_SELECT_ONE.equals(queryDefinition.getMethodName()) ) {
				selectList(context);
			}else if( BaseQuery.METHOD_SELECT_PAGE.equals(queryDefinition.getMethodName()) ) {
				selectPage(context);
			}else if( BaseQuery.METHOD_UPDATE.equals(queryDefinition.getMethodName()) ) {
				update0(context);
			}else if( BaseQuery.METHOD_INSERT.equals(queryDefinition.getMethodName()) ) {
				insert(context);
			}
		}
		
		//如果有分页参数，在这里先把分页参数放到context中暂存，把context.args中的分页参数删除
		if( queryDefinition.isHasPagingParam() ) {
			Object[] args = context.getArgs();
			
			//最后一个参数就是分页参数
			Page page = (Page) args[args.length-1];
			
			//去掉分页参数，添加分页参数
			Object[] useArgs = new Object[args.length-1];
			System.arraycopy(args, 0, useArgs, 0, args.length-1);
			
			context.setArgs(useArgs);
			context.setPage(page);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object select(QueryExecutionContext context) throws SQLException {
		
		//返回结果为“PageResult”，则先查询总记录数，然后查询记录，生成“PageResult”
		QueryDefinition queryDefinition = context.getQueryDefinition();
		if( queryDefinition.isHasPagingParam() ) {
			
			if( queryDefinition.isHasPagingResult() ) { //需要查询总记录
				Long recordCount = recordCountGenerator.getRecordCount(context);
				
				PageResult pageResult = new PageResult();
				pageResult.setPage(context.getPage());
				pageResult.setTotal(recordCount);
				if( recordCount > 0 ) {
					setPagingQueryAndArgs(context);
					List list = (List) super.select(context);
					pageResult.setRecords(list);
				}else {
					pageResult.setRecords(new ArrayList());
				}
				return pageResult;
			}else { //不需要查询总记录
				setPagingQueryAndArgs(context);
			}
		}
		
		
		Object ret = super.select(context);
		
		if( BaseQuery.METHOD_SELECT_ONE.equals(queryDefinition.getMethodName()) ) {
			List list = (List) ret;
			if (list.size() == 1) {
				return list.get(0);
			} else if (list.size() > 1) {
				throw new TooManyResultsException(
						"Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
			} else {
				return null;
			}
		}else {
			return ret;
		}
	}
	
	private void setPagingQueryAndArgs(QueryExecutionContext context) {
		String pagingQuery = ServiceRegistry.INSTANCE.getPaging().getPagingQuery(context.getUseQuery());
		context.setUseQuery(pagingQuery);
		ServiceRegistry.INSTANCE.getPaging().setPagingParams(context);
	}
	
	private void setSelectListQuery(QueryExecutionContext context) {
		
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		String tableName = EntityUtils.tableName(QueryInterfaceUtils.getEntityClass(queryDefinition.getQueryInterface()));
		Criteria criteria = (Criteria) context.getArgs()[0];
		String condition = "";
		if( criteria != null && criteria.size() > 0 ) {
			condition = criteria.getQuery(" where ");
		}
		String orderByClause = "";
		if( criteria != null && criteria.hasOrderBy() ) {
			orderByClause = " order by " + criteria.getOrderByClause();
		}
		String sql = String.format("select * from  %s %s %s", tableName, condition, orderByClause);
		context.setUseQuery(sql);
	}
	
	/**
	 * 处理{@link BaseQuery#selectList(Criteria)}
	 * @param context
	 */
	private void selectList(QueryExecutionContext context) {
		
		setSelectListQuery(context);
		
		Criteria criteria = (Criteria) context.getArgs()[0];
		if( criteria != null && criteria.size() > 0 ) {
			Object[] useArguments = criteria.getArguments();
			context.setArgs(useArguments);
		}else {
			context.setArgs(new Object[0]);
		}
	}
	
	/**
	 * 处理{@link BaseQuery#selectPage(Criteria, Page)}
	 * @param context
	 */
	private void selectPage(QueryExecutionContext context) {

		setSelectListQuery(context);
		
		Criteria criteria = (Criteria) context.getArgs()[0];
		Page page = (Page) context.getArgs()[1];
		Object[] criteriaArgs = null;
		if( criteria != null && criteria.size() > 0 ) {
			criteriaArgs = criteria.getArguments(); 
		}else {
			criteriaArgs = new Object[0];
		}
		Object[] useArguments = new Object[criteriaArgs.length+1];
		System.arraycopy(criteriaArgs, 0, useArguments, 0, criteriaArgs.length);
		useArguments[useArguments.length-1] = page; //加上分页参数，等待后续进一步处理
		context.setArgs(useArguments);
	}
	
	private void update0(QueryExecutionContext context) {
		
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		//update entity
		Object entity = context.getArgs()[0];
		if( entity == null ) {
			throw new QueryExecutionContextBuildException("entity is null.");
		}
		
		//update语句修改字段列表（如：name=?）
		List<String> updateFieldSegments = new ArrayList<String>();
		
		//update value list
		List<Object> updateArguments = new ArrayList<Object>();
		
		//add update fields
		Map<String, Object> fields = EntityUtils.getNotNullField(entity);
		for (Entry<String, Object> entry : fields.entrySet()) {
			updateFieldSegments.add(String.format(" %s = ? ", EntityUtils.beanFieldToDbField(entry.getKey()))); //e.g. age = ?, sex = ? 
			updateArguments.add(entry.getValue());
		}
		
		//add query criteria
		Criteria criteria = (Criteria) context.getArgs()[1];
		if( criteria != null && criteria.size() > 0 ) {
			updateArguments.addAll(criteria.getArgumentList());
		}
		
		String tableName = EntityUtils.tableName(QueryInterfaceUtils.getEntityClass(queryDefinition.getQueryInterface()));
		String updateFields = StringUtils.join(updateFieldSegments.iterator(), ",");
		String condition = "";
		if( criteria != null && criteria.size() > 0 ) {
			condition = criteria.getQuery(" where ");
		}
		String sql = String.format("update %s set %s %s", tableName, updateFields, condition);
		context.setUseQuery(sql);
		
		Object[] useArguments = updateArguments.toArray(new Object[updateArguments.size()]);
		context.setArgs(useArguments);
	}
	
	private void insert(QueryExecutionContext context) {
		
		QueryDefinition queryDefinition = context.getQueryDefinition();
		
		//insert entity
		Object entity = context.getArgs()[0]; 
		if( entity == null ) {
			throw new QueryExecutionContextBuildException("entity is null.");
		}
		
		//check id field
		Field idField = EntityUtils.getIdField(entity.getClass());
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
		Object id = ServiceRegistry.INSTANCE.getIdGenerator().generateId(context);
		if( id != null ) {
			
			if( idField == null ) {
				throw new QueryExecutionContextBuildException("id is unknown.");
			}
			
			//add id field
			insertFieldNames.add(EntityUtils.beanFieldToDbField(idField.getName()));
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
		
		String tableName = EntityUtils.tableName(QueryInterfaceUtils.getEntityClass(queryDefinition.getQueryInterface()));
		String sql = String.format("insert into %s(%s) values(%s)", tableName, StringUtils.join(insertFieldNames.iterator(), ","), StringUtils.join(insertValueLiterals.iterator(), ","));
		context.setUseQuery(sql);
		
		Object[] useArguments = insertArguments.toArray(new Object[insertArguments.size()]);
		context.setArgs(useArguments);
	}
	
}
