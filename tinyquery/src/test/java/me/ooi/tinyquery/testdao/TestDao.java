package me.ooi.tinyquery.testdao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import me.ooi.tinyquery.QuerySource;
import me.ooi.tinyquery.annotation.CriteriaParam;
import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.annotation.Update;
import me.ooi.tinyquery.base.BaseQuery;
import me.ooi.tinyquery.base.Page;
import me.ooi.tinyquery.base.PageResult;
import me.ooi.tinyquery.criteria.Criteria;
import me.ooi.tinyquery.po.T1;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface TestDao extends BaseQuery<T1>{
	
	@Select("select * from t1 where id = ?")
	T1 getT1(Integer id);
	
	@Select("select * from t1 where id = ?")
	Map getT1Map(Integer id);
	
	@Select("select name from t1 where id = ?")
	String getT1Name(Integer id);
	
	@Select("select * from t1 order by id desc")
	List<T1> getT1s(Page page);
	
	@Select("select * from t1 order by id desc")
	List<Map> getT1sMap(Page page);
	
//	//error demo
//	@Select("select name from t1")
//	Object[] getT1NameArray();
	
	@Select("select name from t1 where id = ?")
	Object[] getT1NameArray(int id);
	
	@Select("select name from t1")
	List<Object[]> getT1NamesArray();
	
	@Select("select name from t1 order by id desc ")
	List<String> getT1Names(Page page);
	
	@Select(source = QuerySource.XML)
	List<T1> getT1s2(Date startDate, Date endDate);
	
	@Update("delete from t1 where id = ?")
	int deleteT1(Integer id);
	
	@Select("select * from t1 order by id ")
	PageResult<T1> getT1sPageResult(Page page);
	
	@Select("select * from t1 ")
	PageResult<T1> getT1sByCriteria(Criteria criteria, Page page);
	
	@Select("select * from t1 where id not in(3) ")
	PageResult<T1> getT1sByCriteria2(@CriteriaParam(prefix = "and") Criteria criteria, Page page);
	
}