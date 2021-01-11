
tinyquery-spring 是tinyquery集成到spring的插件

## 使用示例

### create test table
```sql
drop table if exists t1 ; 
CREATE TABLE t1 (
	id int(11) NOT NULL AUTO_INCREMENT,
	name varchar(255),
	user_sex int(4),
	user_birthday date,
	create_time timestamp(6),
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```

### create PO和DAO
```java
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import me.ooi.tinyquery.interceptor.base.Id;
import me.ooi.tinyquery.interceptor.base.Table;

@Data
@Table(name = "t1")
public class T1 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id(sequence = "t1_seq")
    private Integer id;
    private String name;
    private Integer userSex;
    private Date userBirthday;
    private Date createTime;
}
```

```java
import me.ooi.tinyquery.annotation.Select;
import me.ooi.tinyquery.interceptor.base.Page;
import me.ooi.tinyquery.interceptor.base.PageResult;
import me.ooi.tinyquery.interceptor.criteria.Criteria;
import me.ooi.tinyquery.po.T1;

public interface TestDao {
	//at most one record will be returned
	@Select("select * from t1 where id = ?")
	T1 getT1(Integer id);
	
	@Select("select * from t1 ")
	PageResult<T1> getT1sByCriteria(Criteria criteria, Page page);
}
```

### 配置spring
```xml

<bean id="dataSource" .../>

<bean id="tinyqueryConfiguration" class="me.ooi.tinyquery.Configuration" init-method="init" scope="singleton">
	<property name="dataSource" ref="dataSource" />
	<property name="dbtype" value="mysql" />
	<property name="props">
		<map>
			<entry key="app.connectionFactory" value="me.ooi.tinyquery.spring.SpringJdbcConnectionFactory"/>
		</map>
	</property>
</bean>

<bean class="me.ooi.tinyquery.spring.TinyQuerySetup" scope="singleton" depends-on="tinyqueryConfiguration">
	<property name="sacanPackage" value="me.ooi.tinyquery.testdao,me.ooi.tinyquery.testdao2,me.ooi.tinyquery.testdao3" />
</bean>
```

### 使用DAO操作数据库
```java
@Autowired
private TestDao testDao;
...
PageResult<T1> pr = testDao.getT1sByCriteria(new Criteria(), new Page(1, 100));
System.out.println(pr);
T1 t1 = testDao.getT1(2908);
System.out.println(t1);
```

