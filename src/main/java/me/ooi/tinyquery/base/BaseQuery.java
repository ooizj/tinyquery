package me.ooi.tinyquery.base;

import java.util.List;

import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.annotation.Update;
import me.ooi.tinyquery.criteria.Criteria;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface BaseQuery<T> {
	
	public static final String METHOD_SELECT_LIST = "selectList";
	public static final String METHOD_SELECT_ONE = "selectOne";
	public static final String METHOD_SELECT_PAGE = "selectPage";
	public static final String METHOD_INSERT = "insert";
	public static final String METHOD_UPDATE = "update";
	
	@Select
	List<T> selectList(Criteria criteria);
	
	@Select
	T selectOne(Criteria criteria);
	
	@Select
	PageResult<T> selectPage(Criteria criteria, Page page);

	@Update
	int insert(T entity);
	
	@Update
	int update(T entity, Criteria criteria);
	
}
