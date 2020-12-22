package me.ooi.tinyquery.spring.testdao;

import java.util.Date;
import java.util.List;

import me.ooi.tinyquery.QuerySource;
import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.annotation.Update;
import me.ooi.tinyquery.base.BaseQuery;
import me.ooi.tinyquery.base.Page;
import me.ooi.tinyquery.spring.test.po.T1;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface TestDao extends BaseQuery<T1>{
	
	@Select("select * from t1 where id = ?")
	T1 getT1(Integer id);
	
	@Select("select name from t1 where id = ?")
	String getT1Name(Integer id);
	
	@Select("select * from t1 order by id desc")
	List<T1> getT1s(Page page);
	
	@Select("select name from t1")
	Object[] getT1NameArray();
	
	@Select("select name from t1")
	List<Object[]> getT1NamesArray();
	
	@Select("select name from t1 order by id desc ")
	List<String> getT1Names(Page page);
	
	@Select(source = QuerySource.XML)
	List<T1> getT1s2(Date startDate, Date endDate);
	
	@Update("delete from t1 where id = ?")
	int deleteT1(Integer id);
	
}