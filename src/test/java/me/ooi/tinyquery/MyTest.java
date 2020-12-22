package me.ooi.tinyquery;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import me.ooi.tinyquery.base.Page;
import me.ooi.tinyquery.base.PageResult;
import me.ooi.tinyquery.criteria.Criteria;
import me.ooi.tinyquery.po.T1;
import me.ooi.tinyquery.testdao.TestDao;
import oracle.jdbc.pool.OracleDataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class MyTest {
	
	public static Date day(String dateStr) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}
	
	private static String toString(List<Object[]> list) {
		String str = "[";
		
		for (int i = 0; i < list.size(); i++) {
			str += StringUtils.join(list.get(i), ",");
			if( i != list.size()-1 ) {
				str += ", ";
			}
		}
		
		return str += "]";
	}
	
	private void test1(DataSource ds) throws SQLException {
		
		DefaultConfiguration cfg = new DefaultConfiguration();
		cfg.setDataSource(ds);
		
		ServiceRegistry.INSTANCE.init(cfg);
		TestDao testDao = ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class);
		
		T1 t1 = new T1();
		t1.setName("xiaoming");
		t1.setUserBirthday(new Date());
		t1.setCreateTime(new Date());
		testDao.insert(t1);
		System.out.println(t1);
		
		Integer id = t1.getId();
		
		System.out.println("getT1="+testDao.getT1(id));
		System.out.println("getT1Map="+testDao.getT1Map(id));
		System.out.println("getT1Name="+testDao.getT1Name(id));
		System.out.println("getT1s="+testDao.getT1s(new Page(1, 3)));
		System.out.println("getT1sMap="+testDao.getT1sMap(new Page(1, 3)));
		System.out.println("getT1NameArray="+ToStringBuilder.reflectionToString(testDao.getT1NameArray()));
		System.out.println("getT1NamesArray="+toString(testDao.getT1NamesArray()));
		System.out.println("getT1Names="+testDao.getT1Names(new Page(1, 3)));
		System.out.println("getT1s2="+testDao.getT1s2(day("2020-10-01"), day("2020-11-01")));
		System.out.println("getT1sPageResult="+testDao.getT1sPageResult(new Page(1, 3)));
		System.out.println("getT1sPageResult="+testDao.getT1sPageResult(new Page(2, 3)));
		
		t1 = new T1();
		t1.setName("a1234");
		t1.setUserBirthday(new Date());
		testDao.update(t1, Criteria.newCriteria().eq("id", id));
		System.out.println("getT1="+testDao.getT1(id));
		
		System.out.println("selectList="+testDao.selectList(Criteria.newCriteria().in("id", Arrays.asList(3, 4, 11))));
		
		System.out.println("selectPage="+testDao.selectPage(Criteria.newCriteria().notIn("id", Arrays.asList(3)), new Page(1, 3)));
		System.out.println("selectPage="+testDao.selectPage(Criteria.newCriteria().notIn("id", Arrays.asList(3)), new Page(2, 3)));
		
		System.out.println("orderBy="+testDao.selectList(Criteria.newCriteria().ne("id", id).orderBy("id desc")));
		System.out.println("orderBy2="+testDao.selectList(Criteria.newCriteria().orderBy("name asc").orderBy("id desc")));
		System.out.println("orderBy3="+testDao.selectPage(Criteria.newCriteria().orderBy("name asc").orderBy("id desc"), new Page(1, 3)));
		
		System.out.println("like="+testDao.selectPage(Criteria.newCriteria().like("name", "%123%").orderBy("id desc"), new Page(1, 3)));
		
//		System.out.println("selectOne="+testDao.selectOne(Criteria.newCriteria().like("name", "%123%")));
		System.out.println("selectOne="+testDao.selectOne(Criteria.newCriteria().eq("id", id)));
		
		testDao.deleteT1(id);
		
		System.out.println("getT1="+testDao.getT1(id));
	}
	
	@Test
	public void t1() throws SQLException {
		
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
		ds.setUser("root");
		ds.setPassword("root");
		test1(ds);
	}
	
	@Test
	public void t2() throws SQLException {
		
		OracleDataSource ds = new OracleDataSource();
		ds.setURL("jdbc:oracle:thin:@localhost:1521:ORCL");
		ds.setUser("user");
		ds.setPassword("pwd");
		
		test1(ds);
	}
	
	@Test
	public void t3() throws SQLException {
		try {
			MysqlDataSource ds = new MysqlDataSource();
			ds.setUrl("jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
			ds.setUser("root");
			ds.setPassword("root");
			
			DefaultConfiguration cfg = new DefaultConfiguration();
			cfg.setDataSource(ds);
			
			ServiceRegistry.INSTANCE.init(cfg);
			TestDao testDao = ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class);
			
			System.out.println("getT1s2="+testDao.getT1s2(day("2020-10-01"), day("2020-11-01")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void t4() {
		Criteria criteria = Criteria.newCriteria()
				.eq("c0", "000").and(	Criteria.newCriteria().eq("c1", "111").in("c2", Arrays.asList("222", "2220"))	)
				.ne("c3", "333")
				.notIn("c4", Arrays.asList("ni444", "ni4440"))
				.or(	Criteria.newCriteria().eq("c5", "555").in("c6", Arrays.asList("666", "6660"))	)
				;
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		System.out.println(criteria.getArgumentList());
		criteria.eq("c7", "777");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c0", "000");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
	}
	
	
	@Test
	public void t5() throws NoSuchMethodException, SecurityException {
		Method m1 = (new Object(){
			public PageResult<String> m1(){
				return null;
			}
		}).getClass().getMethod("m1");
		System.out.println(m1.getReturnType());
		System.out.println(m1.getGenericReturnType());
	}

}
