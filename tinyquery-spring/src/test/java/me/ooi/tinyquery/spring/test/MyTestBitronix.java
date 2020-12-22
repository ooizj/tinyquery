package me.ooi.tinyquery.spring.test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import me.ooi.tinyquery.base.Page;
import me.ooi.tinyquery.criteria.Criteria;
import me.ooi.tinyquery.spring.test.po.T1;
import me.ooi.tinyquery.spring.testdao.TestDao;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context-bitronix.xml")
public class MyTestBitronix {
	
	@Autowired
	private TestDao testDao;
	
	@Autowired
	private TestService testService;
	
	@Test
	public void t1() throws SQLException {
		
		T1 t1 = new T1();
		t1.setName("xiaoming");
		t1.setUserBirthday(new Date());
		t1.setCreateTime(new Date());
		testDao.insert(t1);
		System.out.println(t1);
		
		Integer id = t1.getId();
		
		System.out.println("getT1="+testDao.getT1(id));
		System.out.println("getT1Name="+testDao.getT1Name(id));
		System.out.println("getT1s="+ToStringBuilder.reflectionToString(testDao.getT1s(new Page(1, 3))));
		System.out.println("getT1NameArray="+ToStringBuilder.reflectionToString(testDao.getT1NameArray()));
		System.out.println("getT1NamesArray="+ToStringBuilder.reflectionToString(testDao.getT1NamesArray()));
		System.out.println("getT1Names="+ToStringBuilder.reflectionToString(testDao.getT1Names(new Page(1, 3))));
//		System.out.println("getT1s="+testDao.getT1s(startDate, endDate));
		
		t1 = new T1();
		t1.setName("xiaoming2");
		t1.setUserBirthday(new Date());
		testDao.update(t1, Criteria.newCriteria().eq("id", id));
		System.out.println("getT1="+testDao.getT1(id));
		
		System.out.println("selectList="+ToStringBuilder.reflectionToString(testDao.selectList(Criteria.newCriteria().in("id", Arrays.asList(9, 10, 11)))));
		
		testDao.deleteT1(id);
		
		System.out.println("getT1="+testDao.getT1(id));
	}
	
	@Test
	public void testTrans() {
		testService.testTrans();
	}

}
