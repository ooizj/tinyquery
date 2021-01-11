package me.ooi.tinyquery.testdao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import me.ooi.tinyquery.QuerySource;
import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.annotation.Update;
import me.ooi.tinyquery.interceptor.base.BaseQuery;
import me.ooi.tinyquery.interceptor.base.Page;
import me.ooi.tinyquery.interceptor.base.PageResult;
import me.ooi.tinyquery.interceptor.criteria.Criteria;
import me.ooi.tinyquery.interceptor.criteria.CriteriaParam;
import me.ooi.tinyquery.po.T1;

/**
 * @author jun.zhao
 */
@SuppressWarnings("rawtypes")
public interface TestDao extends BaseQuery<T1>{
	
	@Update("delete from t1 where id = ?")
	int deleteT1(Integer id);
	
	@Update("delete from t1")
	int deleteAll();
	
	//at most one record will be returned
	@Select("select * from t1 where id = ?")
	T1 getT1(Integer id);
	
	//at most one record will be returned
	@Select("select * from t1 where id = ?")
	Map getT1Map(Integer id);
	
	//at most one record will be returned
	@Select("select name from t1 where id = ?")
	String getT1Name(Integer id);
	
	//at most one record will be returned
	@Select("select name, user_Birthday from t1 where id = ?")
	Object[] getT1NameArray(int id);

	//err usage
	//only one will return
	@Select("select name from t1")
	Object[] getT1NameArray2();
	
	@Select("select * from t1 order by id desc")
	List<T1> getT1s(Page page);
	
	@Select("select * from t1 order by id desc")
	List<Map> getT1sMap(Page page);
	
	@Select("select name, user_Birthday from t1")
	List<Object[]> getT1NamesArray();
	
	@Select("select name from t1 order by id desc ")
	List<String> getT1Names(Page page);
	
	@Select(source = QuerySource.XML)
	List<T1> getT1sByCreateTime(Date startDate, Date endDate);
	
	@Select("select * from t1 order by id ")
	PageResult<T1> getT1sPageResult(Page page);
	
	@Select("select * from t1 ")
	PageResult<T1> getT1sByCriteria(Criteria criteria, Page page);
	
	@Select("select * from t1 where 1=1 ")
	PageResult<T1> getT1sByCriteria2(@CriteriaParam(prefix = "and") Criteria criteria, Page page);
	
	//This is not recommended
	@Select("select * from t1 where id not in(?) ")
	PageResult<T1> getT1sByCriteria3(Integer id, @CriteriaParam(prefix = "and") Criteria criteria, Page page);
	
}