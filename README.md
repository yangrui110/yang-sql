# yang-sql 简易SQL转换器
### 1. 安装
对于SpringBoot项目，可以使用完整的功能,引入Maven
```xml
<dependency>
    <groupId>top.sanguohf.egg</groupId>
    <artifactId>yang-sql-starter</artifactId>
    <version>1.0-release</version>
</dependency>
```
### 2. 使用
1. 配置数据源（Datasource）
2. 使用注解`@ScanEntity({"待扫描的实体类路径"})`
3. 注入`CommonService`实现通用的增删改查以及批量增删该

### 3. 参数说明
1. 传入的参数`EntityParams`，是从前端传递而来的参数，需要被后端解析成SQL语句，其参数样式.具体的`EntityParams`的参数详解
```java
//定义实体的类名，比如User类，其值为User
private String tableClassName;
//定义查询的条件，格式如下
private JSONObject condition;
//定义排序字段以及排序方式
private List<EntityOrderBy> orderBy;
//在调用保存或者更新时会使用该字段，存储更新的数据
private JSONObject data;
```
- `tableClassName` 类名  
        例如在`top.sanguohf.egg.test.entity.UserOne`路径下有`UserOne`类，此时`top.sanguohf.egg.test.entity`包被`@ScanEntity`配置
        ，所以只需要把`tableClassName`的值置为`UserOne`
- `condition` 查询条件
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
       ]
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
    其它更复杂的条件，也可以参照以上逻辑  
- `orderBy`排序字段  
````json
[
  {"column": "userName","direct": "desc"},
  {"column": "password","direct": "asc"}
]
````
- `data`更新/插入的数据
```json
{
  "userName": "张三",
  "password": "45677",
  "id": "812738239"
}
```
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
