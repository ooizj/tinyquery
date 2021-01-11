package me.ooi.tinyquery;


import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import me.ooi.tinyquery.testdao.TestDao;

/**
 * @author jun.zhao
 */
public class TestTinyquery2 {
	
	@Test
	public void t1() throws SQLException {
		
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
		ds.setUser("root");
		ds.setPassword("root");
		
		TinyQuerySetup setup = new TinyQuerySetup();
		setup.setup(ds);
		
		TestDao testDao = ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class);
		TestQuerys.assertResult(testDao);
	}
	
	@Test
	public void t2() throws SQLException {
		
//		OracleDataSource ds = new OracleDataSource();
//		ds.setURL("jdbc:oracle:thin:@localhost:1521:ORCL");
//		ds.setUser("user");
//		ds.setPassword("pwd");
//		
//		TinyQuerySetup setup = new TinyQuerySetup();
//		setup.setup(ds);
//		
//		TestDao testDao = ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class);
//		TestQuerys.assertResult(testDao);
	}
	
	@Test
	public void t3() throws Exception {
		
		final MysqlDataSource ds = new MysqlDataSource();
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
		ds.setUser("root");
		ds.setPassword("root");
		
		int count=20;
		final CountDownLatch cdl = new CountDownLatch(count);
		
		TinyQuerySetup setup = new TinyQuerySetup();
		setup.setup(ds);
		
		TestDao testDao = ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class);
		testDao.deleteAll();
		
		for (int i = 0; i < count; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						TestQuerys.assertResultInMultiThread(ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class));
					} catch (SQLException e) {
						e.printStackTrace();
					}
					cdl.countDown();
				}
			}).start();
		}
		cdl.await();
	}
	
	
}
