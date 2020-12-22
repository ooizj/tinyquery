package me.ooi.tinyquery;

import static org.assertj.core.api.Assertions.assertThat;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import me.ooi.tinyquery.annotation.CriteriaParam;
import me.ooi.tinyquery.base.BaseQuery;
import me.ooi.tinyquery.base.Page;
import me.ooi.tinyquery.base.PageResult;
import me.ooi.tinyquery.criteria.Criteria;
import me.ooi.tinyquery.po.T1;
import me.ooi.tinyquery.testdao.TestDao;
import me.ooi.tinyquery.util.ClassUtils;
import me.ooi.tinyquery.util.ReflectUtils;
import me.ooi.typeconvertor.TypeConvertUtils;
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
		
		TinyQuerySetup setup = new TinyQuerySetup();
		setup.setup(ds);
		
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
		System.out.println("getT1NameArray="+ToStringBuilder.reflectionToString(testDao.getT1NameArray(id)));
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
		
		System.out.println("getT1sByCriteria="+testDao.getT1sByCriteria(Criteria.newCriteria().notIn("id", Arrays.asList(4)), new Page(1, 3)));
		System.out.println("getT1sByCriteria2="+testDao.getT1sByCriteria2(Criteria.newCriteria().notIn("id", Arrays.asList(4)), new Page(1, 3)));
		
		testDao.deleteT1(id);
		
		System.out.println("getT1="+testDao.getT1(id));
	}
	
	private void test1_2(DataSource ds) throws SQLException {
		
		TinyQuerySetup setup = new TinyQuerySetup();
		setup.setup(ds);
		
		TestDao testDao = ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class);
		
		final String name = "xiaoqiang";
		Date current = new Date();
		T1 t1 = new T1();
		t1.setName(name);
		t1.setUserBirthday(current);
		t1.setCreateTime(current);
		testDao.insert(t1);
		
		Integer id = t1.getId();
		assertThat(id).isNotNull();
		
		assertThat(testDao.getT1(id)).isNotNull();
		assertThat(testDao.getT1(id).getName()).isEqualTo(name);
		assertThat(testDao.getT1(id).getCreateTime().getTime()).isEqualTo(current.getTime());
		assertThat(testDao.getT1(id).getUserBirthday().getDate()).isEqualTo(current.getDate());
		
		Map map = testDao.getT1Map(id);
		assertThat(map).isNotEmpty();
		assertThat(map.get("NAME")).isEqualTo(name);
		if( map.get("CREATE_TIME").getClass().getName().equals("oracle.sql.TIMESTAMP") ) {
			
			System.out.println("----------------------------->1");
			System.out.println(map.get("CREATE_TIME").getClass());
			System.out.println(TypeConvertUtils.convert(map.get("CREATE_TIME"), Date.class));
			System.out.println(TypeConvertUtils.convert(map.get("CREATE_TIME"), java.sql.Date.class));
			System.out.println(TypeConvertUtils.convert(map.get("CREATE_TIME"), java.sql.Timestamp.class));
			System.out.println("----------------------------->2");
			System.out.println(map.get("USER_BIRTHDAY").getClass());
			System.out.println(TypeConvertUtils.convert(map.get("USER_BIRTHDAY"), Date.class));
			System.out.println(TypeConvertUtils.convert(map.get("USER_BIRTHDAY"), java.sql.Date.class));
			System.out.println(TypeConvertUtils.convert(map.get("USER_BIRTHDAY"), ClassUtils.getClass("oracle.sql.TIMESTAMP")));
			
			Date createTime = (Date) TypeConvertUtils.convert(map.get("CREATE_TIME"), Date.class);
			Date userBirthday = (Date) TypeConvertUtils.convert(map.get("USER_BIRTHDAY"), Date.class);
			assertThat(createTime.getTime()).isEqualTo(current.getTime());
			assertThat(userBirthday.getDate()).isEqualTo(current.getDate());
		}else {
			System.out.println("----------------------------->1");
			System.out.println(map.get("CREATE_TIME").getClass());
			System.out.println(TypeConvertUtils.convert(map.get("CREATE_TIME"), Date.class));
			System.out.println(TypeConvertUtils.convert(map.get("CREATE_TIME"), java.sql.Date.class));
			System.out.println(TypeConvertUtils.convert(map.get("CREATE_TIME"), ClassUtils.getClass("oracle.sql.TIMESTAMP")));
			System.out.println("----------------------------->2");
			System.out.println(map.get("USER_BIRTHDAY").getClass());
			System.out.println(TypeConvertUtils.convert(map.get("USER_BIRTHDAY"), Date.class));
			
			assertThat(((Date)testDao.getT1Map(id).get("CREATE_TIME")).getTime()).isEqualTo(current.getTime());
			assertThat(((Date)testDao.getT1Map(id).get("USER_BIRTHDAY")).getDate()).isEqualTo(current.getDate());
		}
		
		assertThat(testDao.getT1Name(id)).isEqualTo(name);
		
		assertThat(testDao.getT1s(new Page(1, 3))).hasSize(3);
		
		assertThat(testDao.getT1sMap(new Page(1, 3))).hasSize(3).hasOnlyElementsOfType(Map.class);
		
		assertThat(testDao.getT1NameArray(id)).isNotEmpty();
		assertThat(testDao.getT1NameArray(id).length==1).isTrue();
		assertThat(testDao.getT1NameArray(id)[0]).isEqualTo(name);
		
		assertThat(testDao.getT1NamesArray()).anyMatch(new Predicate<Object[]>() {
			@Override
			public boolean test(Object[] t) {
				return name.equals(t[0]);
			}
		});
		
		assertThat(testDao.getT1Names(new Page(1, 3))).hasSize(3);
		
		assertThat(testDao.getT1s2(new Date(current.getTime()), new Date(current.getTime()))).isEmpty();
		assertThat(testDao.getT1s2(new Date(current.getTime()), new Date(current.getTime()+1))).hasSize(1);
		
		PageResult<T1> pr = testDao.getT1sPageResult(new Page(1, 4));
		assertThat(pr).isNotNull();
		assertThat(pr.getPageSize()).isEqualTo(4);
		assertThat(pr.getPageNumber()).isEqualTo(1);
		assertThat(pr).hasSize(4).hasOnlyElementsOfType(T1.class);
		
		PageResult<T1> pr2 = testDao.getT1sPageResult(new Page(2, 3));
		assertThat(pr2.getPageNumber()).isEqualTo(2);
		assertThat(pr2.get(0).getId()).isNotEqualTo(pr.get(0).getId());
		assertThat(pr2.get(1).getId()).isNotEqualTo(pr.get(1).getId());
		assertThat(pr2.get(2).getId()).isNotEqualTo(pr.get(2).getId());
		assertThat(pr2.get(0).getId()).isEqualTo(pr.get(3).getId()); //最后一个和第一个应该是一样的
		
		current = new Date();
		final String name2 = "a1234";
		t1 = new T1();
		t1.setName(name2);
		t1.setUserBirthday(current);
		testDao.update(t1, Criteria.newCriteria().eq("id", id));
		assertThat(testDao.getT1(id)).isNotNull();
		assertThat(testDao.getT1(id).getId()).isEqualTo(id);
		assertThat(testDao.getT1(id).getName()).isEqualTo(name2);
		
		final List<Integer> ids = Arrays.asList(3, 4, 11);
		assertThat(testDao.selectList(Criteria.newCriteria().in("id", ids)))
			.hasSizeLessThanOrEqualTo(3)
			.allMatch(new Predicate<T1>() {
				@Override
				public boolean test(T1 t) {
					return (ids.contains(t.getId()));
				}
			});
		
		assertThat(testDao.selectList(Criteria.newCriteria().notIn("id", ids)))
			.hasSizeGreaterThan(10)
			.allMatch(new Predicate<T1>() {
				@Override
				public boolean test(T1 t) {
					return (!ids.contains(t.getId()));
				}
			});
		
		PageResult<T1> pr3 = testDao.selectPage(Criteria.newCriteria().notIn("id", ids), new Page(1, 3));
		assertThat(pr3)
		.hasSize(3)
		.allMatch(new Predicate<T1>() {
			@Override
			public boolean test(T1 t) {
				return (!ids.contains(t.getId()));
			}
		});
		
		assertThat(testDao.selectList(Criteria.newCriteria().ne("id", id).orderBy("id asc")).get(0).getId()).isIn(3, 21); //mysql目前最小的是3，oracle是21
		assertThat(testDao.selectList(Criteria.newCriteria().ne("id", id).orderBy("id asc"))).hasSizeGreaterThan(10);
		assertThat(testDao.selectList(Criteria.newCriteria().orderBy("id desc")).get(0).getId())
			.isEqualTo(id)
			.isNotEqualTo(3);
		
		PageResult<T1> pr4 = testDao.selectPage(Criteria.newCriteria().orderBy("name asc").orderBy("id desc"), new Page(1, 3));
		assertThat(pr4).hasSize(3);
		assertThat(pr4.get(0).getName()).isEqualTo(pr4.get(1).getName());
		assertThat(pr4.getRecords().get(0).getName()).isEqualTo(pr4.getRecords().get(1).getName());
		
		assertThat(testDao.selectPage(Criteria.newCriteria().like("name", "%123%").orderBy("id desc"), new Page(1, 3))).hasSize(3).allMatch(new Predicate<T1>() {
			@Override
			public boolean test(T1 t) {
				return t.getName().contains("123");
			}
		});
		
		assertThat(testDao.selectOne(Criteria.newCriteria().eq("id", id))).isNotNull();
		assertThat(testDao.selectOne(Criteria.newCriteria().eq("id", id)).getId()).isEqualTo(id);
		
		assertThat(testDao.getT1sByCriteria(Criteria.newCriteria().notIn("id", Arrays.asList(4)), new Page(1, 3)))
			.hasSize(3)
			.allMatch(new Predicate<T1>() {
				@Override
				public boolean test(T1 t) {
					return (!t.getId().equals(4));
				}
			});
		assertThat(testDao.getT1sByCriteria2(Criteria.newCriteria().notIn("id", Arrays.asList(4)), new Page(1, 3)))
		.hasSize(3)
		.allMatch(new Predicate<T1>() {
			@Override
			public boolean test(T1 t) {
				return ((!t.getId().equals(4)) && (!t.getId().equals(3)));
			}
		});
		
		testDao.deleteT1(id);
		assertThat(testDao.getT1(id)).isNull();
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
	public void t1_2() throws SQLException {
		
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
		ds.setUser("root");
		ds.setPassword("root");
		test1_2(ds);
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
	public void t2_2() throws SQLException {
		
		OracleDataSource ds = new OracleDataSource();
		ds.setURL("jdbc:oracle:thin:@localhost:1521:ORCL");
		ds.setUser("user");
		ds.setPassword("pwd");
		
		test1_2(ds);
	}
	
	@Test
	public void t3() throws SQLException {
		try {
			MysqlDataSource ds = new MysqlDataSource();
			ds.setUrl("jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
			ds.setUser("root");
			ds.setPassword("root");
			
			TinyQuerySetup setup = new TinyQuerySetup();
			setup.setup(ds);
			
			TestDao testDao = ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class);
			
			System.out.println("getT1s2="+testDao.getT1s2(day("2020-10-01"), day("2020-11-01")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void t4() {
		// c0 = ?  and  ( c1 = ?  and  c2 in (?,?) )  and  c3 != ?  and  c4 not in (?,?)  or  c5 = ?  
		Criteria criteria = Criteria.newCriteria()
				.eq("c0", "000").and(	Criteria.newCriteria().eq("c1", "111").in("c2", Arrays.asList("222", "2220"))	)
				.ne("c3", "333")
				.notIn("c4", Arrays.asList("ni444", "ni4440"))
				.or(	Criteria.newCriteria().eq("c5", "555").in("c6", Arrays.asList("666", "6660"))	)
				;
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		criteria.eq("c7", "777");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c0", "000");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.isNull("c8");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		criteria = Criteria.newCriteria()
				.isNotNull("c8");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.isNull("c8").or(new Criteria().eq("c8", ""));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111").and(new Criteria().isNotNull("c8").ne("c8", ""));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111").and(new Criteria().isNull("c8").or(new Criteria().eq("c8", "")));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111");
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111").and(new Criteria().eq("c2", 1));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria()
				.eq("c1", "111").or(new Criteria().eq("c2", 1));
		System.out.println(criteria.getQuery());
		System.out.println(criteria.getArgumentList());
		
//		criteria = Criteria.newCriteria()
//				.or(new Criteria().eq("c2", 1));
//		System.out.println(criteria.getQuery());
//		System.out.println(criteria.getArgumentList());
		
//		criteria = Criteria.newCriteria()
//				.and(new Criteria().eq("c2", 1)).eq("cc", 10);
//		System.out.println(criteria.getQuery());
//		System.out.println(criteria.getArgumentList());
		
		criteria = Criteria.newCriteria().eq("1", 1).and(
				new me.ooi.tinyquery.criteria.Criteria().isNull("cc")
				.or(new me.ooi.tinyquery.criteria.Criteria().ne("cc", 0))
			);
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
	
	@Test
	public void t6() throws NoSuchMethodException, SecurityException {
		Method method = BaseQuery.class.getMethod("selectList", Criteria.class);
		System.out.println(ClassUtils.hasAnnotation(method, CriteriaParam.class));
		System.out.println(ClassUtils.hasParamType(method, Criteria.class));
		System.out.println(ClassUtils.getParamTypeIndex(method, Criteria.class));
		System.out.println(ClassUtils.getAnnotationByIndex(method, 0, CriteriaParam.class));
		System.out.println(ClassUtils.getAnnotationByIndex(method, 1, CriteriaParam.class));
		method = BaseQuery.class.getMethod("selectOne", Criteria.class);
		System.out.println(ClassUtils.hasAnnotation(method, CriteriaParam.class));
		System.out.println(ClassUtils.hasParamType(method, Criteria.class));
		method = BaseQuery.class.getMethod("insert", Object.class);
		System.out.println(method);
		System.out.println(ClassUtils.hasAnnotation(method, CriteriaParam.class));
		System.out.println(ClassUtils.hasParamType(method, Criteria.class));
	}
	
	@Test
	public void t7() throws Exception {
		PageResult pr = new PageResult();
		pr.setTotal(10L);
		System.out.println(ReflectUtils.getPropertyDescriptor(PageResult.class, "total").getReadMethod().invoke(pr));
		System.out.println(pr);
		System.out.println(ReflectUtils.getPropertyDescriptor(PageResult.class, "records"));
		System.out.println(ReflectUtils.getNotNullField(pr));
	}
	
	@Test
	public void t8() throws IOException {
		ImmutableSet<ClassInfo> is;
		
		System.out.println("---------------------------------------------------");
		is = ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("");
		System.out.println(is);
		
		System.out.println("---------------------------------------------------");
		is = ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClasses("me.ooi.tinyquery");
		System.out.println(is);
		for (ClassInfo ci : is) {
			System.out.println(ci);
		}
		
		System.out.println("---------------------------------------------------");
		is = ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("me.ooi.tinyquery");
		System.out.println(is);
		for (ClassInfo ci : is) {
			System.out.println(ci);
		}
		
	}
	
	@Test
	public void t9() {
		PropertyDescriptor[] pds = ReflectUtils.getPropertyDescriptors(Number.class);
		System.out.println(pds[0]);
	}

}
