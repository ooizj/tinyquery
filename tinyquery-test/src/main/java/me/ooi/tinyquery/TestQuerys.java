package me.ooi.tinyquery;

import static me.ooi.tinyquery.TestUtils.date;
import static me.ooi.tinyquery.TestUtils.dayEq;
import static me.ooi.tinyquery.TestUtils.timeEq;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;

import me.ooi.tinyquery.interceptor.base.Page;
import me.ooi.tinyquery.interceptor.base.PageResult;
import me.ooi.tinyquery.interceptor.criteria.Criteria;
import me.ooi.tinyquery.po.T1;
import me.ooi.tinyquery.testdao.TestDao;
import me.ooi.typeconvertor.TypeConvertUtils;


/**
 * @author jun.zhao
 */
public class TestQuerys {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void assertResult(TestDao testDao) throws SQLException {
		
		testDao.deleteAll();
		
		final String NAME = "name1"; 
		final Date BIRTHDAY = date("2020-10-09");
		final Date CREATE_DATE = date("2020-10-09 15:30:01");
		T1 t1 = new T1();
		t1.setName(NAME);
		t1.setUserBirthday(BIRTHDAY);
		t1.setCreateTime(CREATE_DATE);
		testDao.insert(t1);
		final Integer ID = t1.getId();
		assertThat(ID).isNotNull().isGreaterThan(0);
		
		assertThat(testDao.getT1(ID)).isNotNull().is(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID.equals(v.getId())
						&& NAME.equals(v.getName()) 
						&& dayEq(BIRTHDAY, v.getUserBirthday())
						&& timeEq(CREATE_DATE, v.getCreateTime());
			}
		});
		
		assertThat(testDao.getT1Map(ID)).isNotEmpty().is(new Condition<Map>() {
			@Override
			public boolean matches(Map map) {
				Object birthday = map.get("USER_BIRTHDAY");
				Object createDate = map.get("CREATE_TIME");
				
				assert dayEq(BIRTHDAY, (Date)TypeConvertUtils.convert(birthday, Date.class));
				assert dayEq(BIRTHDAY, (Date)TypeConvertUtils.convert(birthday, java.sql.Date.class));
				assert dayEq(BIRTHDAY, (Date)TypeConvertUtils.convert(birthday, java.sql.Timestamp.class));
				
				assert timeEq(CREATE_DATE, (Date)TypeConvertUtils.convert(createDate, Date.class));
				assert timeEq(CREATE_DATE, (Date)TypeConvertUtils.convert(createDate, java.sql.Date.class));
				assert timeEq(CREATE_DATE, (Date)TypeConvertUtils.convert(createDate, java.sql.Timestamp.class));
				
				return ID.equals(((Number)map.get("ID")).intValue())
						&& NAME.equals(map.get("NAME"));
			}
		});
		
		assertThat(testDao.getT1Name(ID)).isEqualTo(NAME);
		
		assertThat(testDao.getT1NameArray2()).hasSize(1).is(new Condition<Object[]>() {
			@Override
			public boolean matches(Object[] v) {
				return v.length == 1 && NAME.equals(v[0]);
			}
		});
		
		assertThat(testDao.getT1NameArray(ID)).hasSize(2).is(new Condition<Object[]>() {
			@Override
			public boolean matches(Object[] v) {
				return v.length == 2 && NAME.equals(v[0]) && dayEq(BIRTHDAY, (Date)TypeConvertUtils.convert(v[1], Date.class)) ;
			}
		});
		
		T1 t2 = new T1();
		t2.setName(NAME+"-2");
		t2.setUserBirthday(date("2020-10-10"));
		t2.setCreateTime(date("2020-10-10 15:30:01"));
		testDao.insert(t2);
		final Integer ID2 = t2.getId();
		assertThat(ID2).isNotNull().isGreaterThan(0);
		
		T1 t3 = new T1();
		t3.setName(NAME+"-3");
		t3.setUserBirthday(date("2020-10-11"));
		t3.setCreateTime(date("2020-10-11 15:30:01"));
		testDao.insert(t3);
		
		T1 t4 = new T1();
		t4.setName(NAME+"-4");
		t4.setUserBirthday(date("2020-10-12"));
		t4.setCreateTime(date("2020-10-12 15:30:01"));
		testDao.insert(t4);
		
		assertThat(testDao.getT1s(new Page(1, 3))).hasSize(3).hasOnlyElementsOfType(T1.class).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		}).areAtLeast(2, new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName()));
			}
		});
		
		assertThat(testDao.getT1sMap(new Page(1, 3))).hasSize(3).hasOnlyElementsOfType(Map.class).are(new Condition<Map>() {
			@Override
			public boolean matches(Map map) {
				
				Object birthday = map.get("USER_BIRTHDAY");
				Object createDate = map.get("CREATE_TIME");
				
				assert TypeConvertUtils.convert(birthday, Date.class) instanceof Date;
				assert TypeConvertUtils.convert(birthday, java.sql.Date.class) instanceof Date;
				assert TypeConvertUtils.convert(birthday, java.sql.Timestamp.class) instanceof Date;
				
				assert TypeConvertUtils.convert(createDate, Date.class) instanceof Date;
				assert TypeConvertUtils.convert(createDate, java.sql.Date.class) instanceof Date;
				assert TypeConvertUtils.convert(createDate, java.sql.Timestamp.class) instanceof Date;
				
				return map.get("ID") != null && birthday != null && createDate != null && ((String)map.get("NAME")).startsWith(NAME);
			}
		});
		
		assertThat(testDao.getT1NamesArray())
		.hasSize(4)
		.hasOnlyElementsOfType(Object[].class).are(new Condition<Object[]>() {
			@Override
			public boolean matches(Object[] v) {
				assert TypeConvertUtils.convert(v[1], Date.class) instanceof Date;
				return v.length == 2 && ((String)v[0]).startsWith(NAME) && v[1] != null ;
			}
		});
		
		assertThat(testDao.getT1Names(new Page(1, 3))).hasSize(3).hasOnlyElementsOfType(String.class).are(new Condition<String>() {
			@Override
			public boolean matches(String v) {
				return v.startsWith(NAME);
			}
		});
		
		final Date ST = date("2020-10-10");
		final Date ET = date("2020-10-12");
		assertThat(testDao.getT1sByCreateTime(ST, ET)).hasSize(2).hasOnlyElementsOfType(T1.class).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null
						&& (dayEq(v.getCreateTime(), ST) || v.getCreateTime().after(ST)) 
						&& ET.after(v.getCreateTime());
			}
		}).areAtLeast(2, new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName()));
			}
		});
		
		PageResult<T1> pr = testDao.getT1sPageResult(new Page(1, 3));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 3;
		assert pr.getTotal() == 4;
		assertThat(pr).isInstanceOf(PageResult.class).hasSize(3).hasOnlyElementsOfType(T1.class).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		}).areAtLeast(2, new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName()));
			}
		});
		
		pr = testDao.getT1sByCriteria(new Criteria().notIn("ID", Arrays.asList(ID)), new Page(1, 3));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 3;
		assert pr.getTotal() == 3;
		assertThat(pr).isInstanceOf(PageResult.class).hasSize(3).hasOnlyElementsOfType(T1.class)
		.are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName())) && v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		pr = testDao.getT1sByCriteria2(new Criteria().notIn("ID", Arrays.asList(ID)), new Page(1, 3));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 3;
		assert pr.getTotal() == 3;
		assertThat(pr).isInstanceOf(PageResult.class).hasSize(3).hasOnlyElementsOfType(T1.class)
		.are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName())) && v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		pr = testDao.getT1sByCriteria3(ID, new Criteria().notIn("ID", Arrays.asList(ID2)), new Page(1, 3));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 3;
		assert pr.getTotal() == 2;
		assertThat(pr).isInstanceOf(PageResult.class).hasSize(2).hasOnlyElementsOfType(T1.class)
		.are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName())) && v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		
		assertThat(testDao.selectList(new Criteria().notIn("ID", Arrays.asList(ID)).orderBy("id asc"))).isInstanceOf(List.class).hasSize(3).hasOnlyElementsOfType(T1.class)
		.is(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID2.equals(v.getId());
			}
		}, Index.atIndex(0)).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName())) && v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		pr = testDao.selectPage(new Criteria().notIn("ID", Arrays.asList(ID)).orderBy("id asc"), new Page(1, 2));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 2;
		assert pr.getTotal() == 3;
		assertThat(pr).isInstanceOf(PageResult.class).hasSize(2).hasOnlyElementsOfType(T1.class).is(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID2.equals(v.getId());
			}
		}, Index.atIndex(0)).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName())) && v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		assertThat(testDao.selectObjs("name, CREATE_TIME", new Criteria().notIn("ID", Arrays.asList(ID)).orderBy("id asc"))).hasSize(3).hasOnlyElementsOfType(Object[].class)
		.are(new Condition<Object[]>() {
			@Override
			public boolean matches(Object[] v) {
				return v.length==2 && ((String)v[0]).startsWith(NAME) && v[1] != null;
			}
		});
		
		assertThat(testDao.selectOne(new Criteria().eq("ID", ID).eq("name", NAME).orderBy("id asc"))).isNotNull().is(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID.equals(v.getId())
						&& NAME.equals(v.getName()) 
						&& dayEq(BIRTHDAY, v.getUserBirthday())
						&& timeEq(CREATE_DATE, v.getCreateTime());
			}
		});
		
		final Date NEW_CREATE_DATE = date("2020-11-12 03:30:01");
		T1 t4Update = new T1();
		t4Update.setCreateTime(NEW_CREATE_DATE);
		testDao.update(t4Update, new Criteria().eq("ID", ID));
		
		assertThat(testDao.getT1(ID)).isNotNull().is(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID.equals(v.getId())
						&& NAME.equals(v.getName()) 
						&& dayEq(BIRTHDAY, v.getUserBirthday())
						&& timeEq(NEW_CREATE_DATE, v.getCreateTime());
			}
		});
		
		assertThat(testDao.getT1s2(NAME)).isNotEmpty().are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return NAME.equals(v.getName());
			}
		});
		
		assertThat(testDao.getT1s3(ID, NAME, new Page(1, 3))).isNotEmpty().hasSize(1).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID.equals(v.getId()) && NAME.equals(v.getName());
			}
		});
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void assertResultInMultiThread(TestDao testDao) throws SQLException {
		
		final String NAME = "name1"; 
		final Date BIRTHDAY = date("2020-10-09");
		final Date CREATE_DATE = date("2020-10-09 15:30:01");
		T1 t1 = new T1();
		t1.setName(NAME);
		t1.setUserBirthday(BIRTHDAY);
		t1.setCreateTime(CREATE_DATE);
		testDao.insert(t1);
		final Integer ID = t1.getId();
		assertThat(ID).isNotNull().isGreaterThan(0);
		
		assertThat(testDao.getT1(ID)).isNotNull().is(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID.equals(v.getId())
						&& NAME.equals(v.getName()) 
						&& dayEq(BIRTHDAY, v.getUserBirthday())
						&& timeEq(CREATE_DATE, v.getCreateTime());
			}
		});
		
		assertThat(testDao.getT1Map(ID)).isNotEmpty().is(new Condition<Map>() {
			@Override
			public boolean matches(Map map) {
				Object birthday = map.get("USER_BIRTHDAY");
				Object createDate = map.get("CREATE_TIME");
				
				assert dayEq(BIRTHDAY, (Date)TypeConvertUtils.convert(birthday, Date.class));
				assert dayEq(BIRTHDAY, (Date)TypeConvertUtils.convert(birthday, java.sql.Date.class));
				assert dayEq(BIRTHDAY, (Date)TypeConvertUtils.convert(birthday, java.sql.Timestamp.class));
				
				assert timeEq(CREATE_DATE, (Date)TypeConvertUtils.convert(createDate, Date.class));
				assert timeEq(CREATE_DATE, (Date)TypeConvertUtils.convert(createDate, java.sql.Date.class));
				assert timeEq(CREATE_DATE, (Date)TypeConvertUtils.convert(createDate, java.sql.Timestamp.class));
				
				return ID.equals(((Number)map.get("ID")).intValue())
						&& NAME.equals(map.get("NAME"));
			}
		});
		
		assertThat(testDao.getT1Name(ID)).isEqualTo(NAME);
		
		assertThat(testDao.getT1NameArray2()).hasSize(1).is(new Condition<Object[]>() {
			@Override
			public boolean matches(Object[] v) {
				return v.length == 1 && NAME.equals(v[0]);
			}
		});
		
		assertThat(testDao.getT1NameArray(ID)).hasSize(2).is(new Condition<Object[]>() {
			@Override
			public boolean matches(Object[] v) {
				return v.length == 2 && NAME.equals(v[0]) && dayEq(BIRTHDAY, (Date)TypeConvertUtils.convert(v[1], Date.class)) ;
			}
		});
		
		T1 t2 = new T1();
		t2.setName(NAME+"-2");
		t2.setUserBirthday(date("2020-10-10"));
		t2.setCreateTime(date("2020-10-10 15:30:01"));
		testDao.insert(t2);
		final Integer ID2 = t2.getId();
		assertThat(ID2).isNotNull().isGreaterThan(0);
		
		T1 t3 = new T1();
		t3.setName(NAME+"-3");
		t3.setUserBirthday(date("2020-10-11"));
		t3.setCreateTime(date("2020-10-11 15:30:01"));
		testDao.insert(t3);
		
		T1 t4 = new T1();
		t4.setName(NAME+"-4");
		t4.setUserBirthday(date("2020-10-12"));
		t4.setCreateTime(date("2020-10-12 15:30:01"));
		testDao.insert(t4);
		
		assertThat(testDao.getT1s(new Page(1, 3))).hasSize(3).hasOnlyElementsOfType(T1.class).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		}).areAtLeast(2, new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName()));
			}
		});
		
		assertThat(testDao.getT1sMap(new Page(1, 3))).hasSize(3).hasOnlyElementsOfType(Map.class).are(new Condition<Map>() {
			@Override
			public boolean matches(Map map) {
				
				Object birthday = map.get("USER_BIRTHDAY");
				Object createDate = map.get("CREATE_TIME");
				
				assert TypeConvertUtils.convert(birthday, Date.class) instanceof Date;
				assert TypeConvertUtils.convert(birthday, java.sql.Date.class) instanceof Date;
				assert TypeConvertUtils.convert(birthday, java.sql.Timestamp.class) instanceof Date;
				
				assert TypeConvertUtils.convert(createDate, Date.class) instanceof Date;
				assert TypeConvertUtils.convert(createDate, java.sql.Date.class) instanceof Date;
				assert TypeConvertUtils.convert(createDate, java.sql.Timestamp.class) instanceof Date;
				
				return map.get("ID") != null && birthday != null && createDate != null && ((String)map.get("NAME")).startsWith(NAME);
			}
		});
		
		assertThat(testDao.getT1NamesArray())
		.hasOnlyElementsOfType(Object[].class).are(new Condition<Object[]>() {
			@Override
			public boolean matches(Object[] v) {
				assert TypeConvertUtils.convert(v[1], Date.class) instanceof Date;
				return v.length == 2 && ((String)v[0]).startsWith(NAME) && v[1] != null ;
			}
		});
		
		assertThat(testDao.getT1Names(new Page(1, 3))).hasSize(3).hasOnlyElementsOfType(String.class).are(new Condition<String>() {
			@Override
			public boolean matches(String v) {
				return v.startsWith(NAME);
			}
		});
		
		final Date ST = date("2020-10-10");
		final Date ET = date("2020-10-12");
		assertThat(testDao.getT1sByCreateTime(ST, ET))
		.hasOnlyElementsOfType(T1.class).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null
						&& (dayEq(v.getCreateTime(), ST) || v.getCreateTime().after(ST)) 
						&& ET.after(v.getCreateTime());
			}
		}).areAtLeast(2, new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return (!NAME.equals(v.getName()));
			}
		});
		
		PageResult<T1> pr = testDao.getT1sPageResult(new Page(1, 3));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 3;
		assertThat(pr).isInstanceOf(PageResult.class).hasSize(3).hasOnlyElementsOfType(T1.class).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		pr = testDao.getT1sByCriteria(new Criteria().notIn("ID", Arrays.asList(ID)), new Page(1, 3));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 3;
		assertThat(pr).isInstanceOf(PageResult.class).hasSize(3).hasOnlyElementsOfType(T1.class)
		.are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		pr = testDao.getT1sByCriteria2(new Criteria().notIn("ID", Arrays.asList(ID)), new Page(1, 3));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 3;
		assertThat(pr).isInstanceOf(PageResult.class).hasOnlyElementsOfType(T1.class)
		.are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getName().startsWith(NAME) && v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		pr = testDao.getT1sByCriteria3(ID, new Criteria().notIn("ID", Arrays.asList(ID2)), new Page(1, 3));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 3;
		assertThat(pr).isInstanceOf(PageResult.class).hasOnlyElementsOfType(T1.class)
		.are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		
		assertThat(testDao.selectList(new Criteria().notIn("ID", Arrays.asList(ID)).orderBy("id asc"))).isInstanceOf(List.class).hasOnlyElementsOfType(T1.class)
		.are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		pr = testDao.selectPage(new Criteria().notIn("ID", Arrays.asList(ID)).orderBy("id asc"), new Page(1, 2));
		assert pr.getPageNumber() == 1;
		assert pr.getPageSize() == 2;
		assertThat(pr).isInstanceOf(PageResult.class).hasOnlyElementsOfType(T1.class).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return v.getId() != null && v.getUserBirthday() != null && v.getCreateTime() != null;
			}
		});
		
		assertThat(testDao.selectObjs("name, CREATE_TIME", new Criteria().notIn("ID", Arrays.asList(ID)).orderBy("id asc"))).hasOnlyElementsOfType(Object[].class)
		.are(new Condition<Object[]>() {
			@Override
			public boolean matches(Object[] v) {
				return v.length==2 && (v[0] instanceof String) && v[1] != null;
			}
		});
		
		assertThat(testDao.selectOne(new Criteria().eq("ID", ID).eq("name", NAME).orderBy("id asc"))).isNotNull().is(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID.equals(v.getId())
						&& NAME.equals(v.getName()) 
						&& dayEq(BIRTHDAY, v.getUserBirthday())
						&& timeEq(CREATE_DATE, v.getCreateTime());
			}
		});
		
		final Date NEW_CREATE_DATE = date("2020-11-12 03:30:01");
		T1 t4Update = new T1();
		t4Update.setCreateTime(NEW_CREATE_DATE);
		testDao.update(t4Update, new Criteria().eq("ID", ID));
		
		assertThat(testDao.getT1(ID)).isNotNull().is(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID.equals(v.getId())
						&& NAME.equals(v.getName()) 
						&& dayEq(BIRTHDAY, v.getUserBirthday())
						&& timeEq(NEW_CREATE_DATE, v.getCreateTime());
			}
		});
		
		assertThat(testDao.getT1s2(NAME)).isNotEmpty().are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return NAME.equals(v.getName());
			}
		});
		
		assertThat(testDao.getT1s3(ID, NAME, new Page(1, 3))).isNotEmpty().hasSize(1).are(new Condition<T1>() {
			@Override
			public boolean matches(T1 v) {
				return ID.equals(v.getId()) && NAME.equals(v.getName());
			}
		});
		
	}

}
