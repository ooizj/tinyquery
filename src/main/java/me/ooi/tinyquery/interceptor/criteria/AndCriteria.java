package me.ooi.tinyquery.interceptor.criteria;

import java.util.List;

/**
 * @author jun.zhao
 */
public class AndCriteria extends AbstractCriteria {
	
	private AbstractCriteria[] criterias;
	
	public AndCriteria(AbstractCriteria... criterias) {
		super();
		this.criterias = criterias;
	}

	@Override
	public String getQuery() {
		int size = size();
		if( size == 0 ) {
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		
		if( size > 1 ) {
			sb.append("(");
		}
		
		for (int i = 0; i < criterias.length; i++) {
			if( i != 0 && criterias[i].size()>0 ) {
				sb.append(" and ");
			}
			sb.append(criterias[i].getQuery());
		}
		
		if( size > 1 ) {
			sb.append(")");
		}
		
		return " and "+sb.toString();
	}

	@Override
	public void addArguments(List<Object> arguments) {
		if( criterias != null ) {
			for (AbstractCriteria criteria : criterias) {
				criteria.addArguments(arguments);
			}
		}
	}

	@Override
	public int size() {
		int size = 0;
		if( criterias != null ) {
			for (AbstractCriteria criteria : criterias) {
				size += criteria.size();
			}
		}
		return size;
	}

}
