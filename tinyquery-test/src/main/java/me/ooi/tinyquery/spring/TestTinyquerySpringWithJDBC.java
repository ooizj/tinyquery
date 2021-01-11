package me.ooi.tinyquery.spring;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import me.ooi.tinyquery.TestQuerys;
import me.ooi.tinyquery.testdao.TestDao;

/**
 * @author jun.zhao
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context-jdbc.xml")
public class TestTinyquerySpringWithJDBC {
	
	@Autowired
	private TestDao testDao;
	
	@Autowired
	private TestService testService;
	
	@Test
	public void t1() throws SQLException {
		TestQuerys.assertResult(testDao);
	}
	
	@Test
	public void testTrans() {
		testService.testTrans();
	}

}
