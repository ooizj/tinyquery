package me.ooi.tinyquery.interceptor.base;

import java.util.List;

import me.ooi.tinyquery.annotation.Interceptors;
import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.annotation.Update;
import me.ooi.tinyquery.interceptor.criteria.Criteria;

/**
 * @author jun.zhao
 */
public interface BaseQuery<T> {
	
	@Select
	@Interceptors({BaseSelectListInterceptor.class})
	List<T> selectList(Criteria criteria);
	
	@Select
	@Interceptors({BaseSelectListInterceptor.class})
	PageResult<T> selectPage(Criteria criteria, Page page);
	
	@Select
	@Interceptors({BaseSelectObjsInterceptor.class})
	List<Object[]> selectObjs(String selectFields,  Criteria criteria);
	
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
