package me.ooi.tinyquery.spring.test;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import me.ooi.tinyquery.spring.test.po.T1;
import me.ooi.tinyquery.spring.testdao.TestDao;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class TestService {
	
	@Autowired
	private TestDao testDao;
	
	@Transactional
	public void testTrans() {
		System.out.println("getCurrentTransactionName="+TransactionSynchronizationManager.getCurrentTransactionName());
		
		T1 t1 = new T1();
		t1.setName("xiaoming");
		t1.setUserBirthday(new Date());
		t1.setCreateTime(new Date());
		testDao.insert(t1);
		System.out.println(t1);
		
		int a = 3/0;
	}

}
