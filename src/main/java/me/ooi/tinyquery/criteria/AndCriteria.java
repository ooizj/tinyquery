package me.ooi.tinyquery.criteria;

import java.util.List;

/**
 * @author jun.zhao
 */
public class AndCriteria extends AbstractCriteria {
	
	private AbstractCriteria criteria;
	
	public AndCriteria(AbstractCriteria criteria) {
		super();
		this.criteria = criteria;
	}

	@Override
	public String getQuery(boolean appendPrefix) {
		return (appendPrefix ? " and " : "") + 
				String.format(" (%s) ", criteria.getQuery(false));
	}

	@Override
	public void addArguments(List<Object> arguments) {
		criteria.addArguments(arguments);
	}

	@Override
	public int size() {
		return 1;
	}

}
