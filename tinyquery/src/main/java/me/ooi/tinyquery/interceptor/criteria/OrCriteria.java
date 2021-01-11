package me.ooi.tinyquery.interceptor.criteria;

import java.util.List;

/**
 * @author jun.zhao
 */
public class OrCriteria extends AbstractCriteria {
	
	private AbstractCriteria criteria;
	
	public OrCriteria(AbstractCriteria criteria) {
		super();
		this.criteria = criteria;
	}

	@Override
	public String getQuery() {
		if( criteria.size() == 0 ) {
			return "";
		}else if( criteria.size() == 1 ) {
			return " or " + String.format(" %s ", criteria.getQuery());
		}else {
			return " or " + String.format(" (%s) ", criteria.getQuery());
		}
	}

	@Override
	public void addArguments(List<Object> arguments) {
		criteria.addArguments(arguments);
	}
	
	@Override
	public int size() {
		return criteria.size();
	}

}
