# yang-sql SQL转换器  
功能详解：实现了对数据库表通用的增删改查，以及批量的增删改，仅是对单表。目前数据库做了sql server和MySQL的适配，
对于oracle有实现，但是并未做相关测试，至于其它数据库系统默认采用的是MySQL的语法。有不正确的地方再行修改。
欢迎加入QQ群：623337780，获取帮助和提出宝贵建议
### 1. 安装
对于SpringBoot项目，可以使用完整的功能,引入Maven
```xml
<dependency>
    <groupId>top.sanguohf.egg</groupId>
    <artifactId>yang-sql-starter</artifactId>
    <version>1.0.4-release</version>
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
`@Field`作用在属性上，用户定义列对应的数据库字段的名字  

5、通用的增删改查类`CommonService`  
使用方式，通过`@Autowire`注入，然后根据注入的对象操作方法。