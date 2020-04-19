# yang-sql SQL转换器  
功能详解：实现了对数据库表通用的增删改查，以及批量的增删改，囊括了关联查询以及子查询。目前数据库做了sql server和MySQL的适配，
对于oracle有实现，但是并未做相关测试，至于其它数据库系统默认采用的是MySQL的语法。有不正确的地方再行修改。
欢迎加入QQ群：623337780，获取帮助和提出宝贵建议
csdn教程地址：[教程一](https://blog.csdn.net/yr_sky/article/details/104335020) [教程二](https://blog.csdn.net/yr_sky/article/details/104402341)
[教程总览](https://blog.csdn.net/yr_sky/article/details/104441531)
### 1. 安装
对于SpringBoot项目，可以使用完整的功能,引入Maven
```xml
<dependency>
    <groupId>top.sanguohf.egg</groupId>
    <artifactId>yang-sql-starter</artifactId>
    <version>1.0.19-release</version>
</dependency>
```
### 2. 使用
1. 配置数据源（`Datasource`）
2. 使用注解`@ScanEntity({"待扫描的实体类路径"})`
3. 注入`CommonService`实现通用的增删改查以及批量增删改

### 3. 参数说明
1. 传入的参数`EntityParams`，是从前端传递而来的参数，需要被后端解析成SQL语句，其参数样式.具体的`EntityParams`的参数详解
```java
//定义实体的类名，比如User类，其值为User
private String tableClassName;
//定义查询或更新的条件，格式如下
private JSONObject condition;
//定义排序字段以及排序方式
private List<EntityOrderBy> orderBy;
```
- `tableClassName` 类名  
        例如在`top.sanguohf.egg.test.entity.UserOne`路径下有`UserOne`类，此时`top.sanguohf.egg.test.entity`包被`@ScanEntity`配置
        ，所以只需要把`tableClassName`的值置为`UserOne`
- `condition` 查询条件、更新的数据、插入的数据
```json
{
  "left":"userName",
   "relation":"=",
   "right": "admin"
}
```  
    或者是更复杂的格式：  
```json
{
   "left": {
       "condition": [
           {"left": "userName","relation": "=","right": "admin"},
           {"left": "password","relation": "=","right": "123456"}
       ],"combine": "and"
   },
   "relation": "and",
   "right": {
       "condition": [
           {"left": "userName","relation": "like","right": "%admin%"},
           {"left": "password","relation": "=","right": "6547199"}
       ],"combine": "and"
  }
}
```  
    又或者是List格式  
```json
{
   "condition": [
        {"left": "userName","relation": "like","right": "%admin%"},
        {"left": "password","relation": "=","right": "6547199"}
   ],
   "combine": "and"
}
```  
    若是更新数据或者插入数据，则需要的数据格式为
```json
{
  "userName": "张三",
  "password": "王五",
  "id": "10001"
}
```
    其它更复杂的条件，也可以参照以上逻辑  
- `orderBy`排序字段  
````json
[
  {"column": "userName","direct": "desc"},
  {"column": "password","direct": "asc"}
]
````
2、分页查询，在查询的基础上多了一个分页参数，调用逻辑都是一样  
3、测试代码
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestOne {

    @Autowired
    private CommonService commonService;

    @Test
    public void test1() throws NoSuchFieldException, IOException, ClassNotFoundException {
        EntityParams params = new EntityParams();
        params.setTableClassName("UserOne");
        //设置查询条件
        JSONObject con1 = new JSONObject();
        con1.put("left","userName");
        con1.put("right","%admin%");
        con1.put("relation","like");
        params.setCondition(con1);
        List list = commonService.findList(params);
        System.out.println(list);
        System.out.println("-----------------------");
        Page page = new Page();
        page.setPage(1);page.setSize(10);
        CommonPageResp pageList = commonService.findPageList(params, page);
        System.out.println(pageList);
    }

}
```
    以及主类上的注解
```java
@EnableTransactionManagement //保证批量操作的事务一致性
@ScanEntity("top.sanguohf.egg.test.entity")
@SpringBootApplication
public class YangSqlTest {
    public static void main(String[] args) {
        SpringApplication.run(YangSqlTest.class);
    }
}
```
4、实体类的注解详解  
`@TableName()` 作用在类上，用于定义这个实体类对应的数据库表名称  
`@Id`作用在属性上，用户标识此属性为主键，默认是名为ID的属性作为主键  
`@IgnoreSelectReback`作用在属性上，用户在查询数据时，不返回此字段  
`@Field`作用在属性上，用户定义列对应的数据库字段的名字以及定义列的别名 
 
*以下注解可以用在视图实体上也可用在普通的实体上*  
`@Condition`作用在属性上，定义查询条件  
`@OrderBy`作用在属性上，定义排序字段，默认倒序  

*以下注解用在视图实体属性上*  
`@Conditions`,作用在视图实体的属性上，是`@condition`注解的复数形式，可同时定义多个条件  
`@OrderBys`作用在视图实体的属性上，可同时定义多个排序字段  
`@ViewTable`作用在类上，标识当前类是视图实体类
`@MainTable`作用在视图实体属性上，定义主表信息
`@ReferTable`作用在视图实体属性上，定义关联表的信息

5、通用的增删改查类`CommonService`  
使用方式，通过`@Autowire`注入，然后根据注入的对象操作方法。

6、视图实体的定义实例
```java
//视图实体类，只是定义不同，使用方法和普通的实体类无差别
@ViewTable //标识当前类是视图实体类
public class UserClassesView {

    @Condition(column = "teacherId",value = "1") //定义查询条件
    @MainTable(tableAlias = "userOne") //定义视图查询的主表信息（其也可是视图实体）
    private TeacherView teacherView;
    
    //定义join查询的关系
    @ReferTable(tableAlias = "aliasClass",relation = "left join",condition = "userOne.id = aliasClass.classesId and userOne.userName = aliasClass.name")
    private Classes classes;

    @OrderBys({
            @OrderBy(column = "teacherId",direct = "desc")
    })  //定义排序字段
    @Conditions({
            @Condition(column = "teacherId",value = "1"),
            @Condition(column = "teacherName",value = "8")
    })//定义多个查询条件
    //定义join查询关系
    @ReferTable(tableAlias = "teacher",relation = "left join",condition = "userOne.id = teacher.teacherId",includeColumns = {"teacherName"})
    private Teacher teacher;

}
```  
上面这个视图实体类，对应的SQL语句是：
```mysql
SELECT
	userOne.teacherId,
	userOne.teacherName,
	userOne.userName,
	userOne.id,
	aliasClass.CLASSES_ID AS classesId,
	aliasClass.NAME AS classesName,
	teacher.TEACHER_NAME AS teacherName 
FROM
	(
	SELECT
		oneOs.TEACHER_ID AS teacherId,
		oneOs.TEACHER_NAME AS teacherName,
		op.user_name AS userName,
		op.ID AS id 
	FROM
		TEACHER oneOs
		INNER JOIN USER op ON oneOs.TEACHER_ID = op.ID 
	) userOne
	LEFT JOIN CLASSES aliasClass ON userOne.id = aliasClass.CLASSES_ID 
	AND userOne.userName = aliasClass.
	NAME LEFT JOIN TEACHER teacher ON userOne.id = teacher.TEACHER_ID 
WHERE
	( userOne.teacherId = '1' AND teacher.TEACHER_ID = '1' AND teacher.TEACHER_NAME = '8' ) 
ORDER BY
	teacher.TEACHER_ID DESC
```