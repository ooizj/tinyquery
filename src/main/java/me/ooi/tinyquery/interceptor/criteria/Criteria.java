package me.ooi.tinyquery.interceptor.criteria;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author jun.zhao
 */
public class Criteria extends AbstractCriteria {
	
	private List<AbstractCriteria> criterias = new ArrayList<AbstractCriteria>();
	private List<Object> arguments = new ArrayList<Object>();
	private List<String> orderByClauses = new ArrayList<String>();
	
	public static Criteria newCriteria() {
		return new Criteria();
	}
	
	public Criteria eq(String columnName, Object value) {
		AbstractCriteria c = new EqualsCriteria(columnName, value);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria ne(String columnName, Object value) {
		AbstractCriteria c = new NotEqualsCriteria(columnName, value);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria gt(String columnName, Object value) {
		AbstractCriteria c = new GreaterThanCriteria(columnName, value);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria ge(String columnName, Object value) {
		AbstractCriteria c = new GreaterThanOrEqualsCriteria(columnName, value);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria lt(String columnName, Object value) {
		AbstractCriteria c = new LessThanCriteria(columnName, value);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria le(String columnName, Object value) {
		AbstractCriteria c = new LessThanOrEqualsCriteria(columnName, value);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria like(String columnName, Object value) {
		AbstractCriteria c = new LikeCriteria(columnName, value);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria in(String columnName, List<?> values) {
		AbstractCriteria c = new InCriteria(columnName, values);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria notIn(String columnName, List<?> values) {
		AbstractCriteria c = new NotInCriteria(columnName, values);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria isNull(String columnName) {
		AbstractCriteria c = new IsNullCriteria(columnName);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria isNotNull(String columnName) {
		AbstractCriteria c = new IsNotNullCriteria(columnName);
		if( size() > 0 ) {
			c = new AndCriteria(c);
		}
		criterias.add(c);
		return this;
	}
	
	public Criteria and(AbstractCriteria criteria) {
		if( size() == 0 ) {
			throw new CriteriaBuildException("other conditions must be included before 'and'.");
		}
		this.criterias.add(new AndCriteria(new AbstractCriteria[]{criteria}));
		return this;
	}
	
	public Criteria or(AbstractCriteria criteria) {
		if( size() == 0 ) {
			throw new CriteriaBuildException("other conditions must be included before 'or'.");
		}
		criterias.add(new OrCriteria(criteria));
		return this;
	}
	
	public Criteria orderBy(String orderByClause) {
		orderByClauses.add(orderByClause);
		return this;
	}
	
	public String getQuery(String prefix) {
		return (prefix==null?"":prefix) + getQuery();
	}

	@Override
	public String getQuery() {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < criterias.size(); i++) {
			sb.append(criterias.get(i).getQuery());
		}
		
		return sb.toString();
	}

	@Override
	public void addArguments(List<Object> arguments) {
		for (AbstractCriteria criteria : criterias) {
			criteria.addArguments(arguments);
		}
	}

	/**
	 * {@link #getArgumentList()}
	 * @return
	 */
	public Object[] getArguments() {
		arguments.clear();
		addArguments(arguments);
		
		return arguments.toArray(new Object[arguments.size()]);
	}
	
	/**
	 * {@link #getArguments()}
	 * @return
	 */
	public List<Object> getArgumentList() {
		arguments.clear();
		addArguments(arguments);
		
		return arguments;
	}
	
	public String getOrderByClause() {
		return StringUtils.join(orderByClauses.iterator(), ", ");
	}
	
	public boolean hasOrderBy() {
		return (!orderByClauses.isEmpty());
	}
	
	@Override
	public int size() {
		int size = 0;
		for (AbstractCriteria criteria : criterias) {
			size += criteria.size();
		}
		return size;
	}
	
}
