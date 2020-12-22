package me.ooi.tinyquery.base;

import java.util.List;

import me.ooi.tinyquery.annotation.Interceptors;
import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.annotation.Update;
import me.ooi.tinyquery.criteria.Criteria;
import me.ooi.tinyquery.interceptors.BaseInsertInterceptor;
import me.ooi.tinyquery.interceptors.BaseSelectListInterceptor;
import me.ooi.tinyquery.interceptors.BaseSelectOneInterceptor;
import me.ooi.tinyquery.interceptors.BaseUpdateInterceptor;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface BaseQuery<T> {
	
	@Select
	@Interceptors({BaseSelectListInterceptor.class})
	List<T> selectList(Criteria criteria);
	
	@Select
	@Interceptors({BaseSelectListInterceptor.class})
	PageResult<T> selectPage(Criteria criteria, Page page);
	
	@Select
	@Interceptors({BaseSelectOneInterceptor.class})
	T selectOne(Criteria criteria);
	
	@Update
	@Interceptors({BaseInsertInterceptor.class})
	int insert(T entity);
	
	@Update
	@Interceptors({BaseUpdateInterceptor.class})
	int update(T entity, Criteria criteria);
	
}
