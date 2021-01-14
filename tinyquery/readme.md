
tinyquery 是一个持久层工具  

依赖的commons-dbutils为[https://github.com/ooizj/commons-dbutils.git](https://github.com/ooizj/commons-dbutils.git)  
branch: DBUTILS_1_7_fixbug

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

### 在classpath新增tinyquery.properties
```
# database type: mysql/oracle
app.dbtype=mysql
```

### setup
```java
// create datasource
MysqlDataSource ds = new MysqlDataSource();
ds.setUrl("jdbc:mysql://127.0.0.1:3306/mytest");
ds.setUser("root");
ds.setPassword("root");

// setup tinyquery
TinyQuerySetup setup = new TinyQuerySetup();
setup.setup(ds);
```

### 使用DAO操作数据库
```java
TestDao testDao = ServiceRegistry.INSTANCE.getQueryProxyManager().getProxy(TestDao.class);
...
PageResult<T1> pr = testDao.getT1sByCriteria(new Criteria(), new Page(1, 100));
System.out.println(pr);
T1 t1 = testDao.getT1(2908);
System.out.println(t1);
```

