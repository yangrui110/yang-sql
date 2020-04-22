# 前言

  这个插件是个人开发的，功能测试相对不会太完善。目前已经在我就职的公司中开始使用，mybatis已经被逐步移除。这个插件的思想是借助于我上一家就职的公司，有很多值得参考和借鉴之处。

  我写这个插件的理由：

1、一直以来都被增删改查所累，哪怕是有了mybatis以及mybatis plus，也还是需要做一些无用的配置，包括查询条件的书写、排序字段，后期如果需要更改一个小字段，也需要牵动整个后台，十分的重复机械。

2、针对mybatis和mybatis plus，其中难免需要书写一些简单的sql关联语句。极大的浪费时间，消磨人的精力

3、我喜欢一个插件具有简约的配置以及良好的使用方式，易于理解，容易上手。

欢迎加入QQ群：623337780，获取帮助和提出宝贵建议

# 1、安装

引入maven包：

```xml
<dependency>
    <groupId>top.sanguohf.egg</groupId>
    <artifactId>yang-sql-starter</artifactId>
    <version>1.0.20-release</version>
</dependency>
```

# 2、初次使用

1、在SpringBoot项目中引入Maven包后，你就可以进行测试了。在SpringBoot的主运行类上加上`@ScanEntity`注解,如下配置：

```java
@EnableTransactionManagement
@ScanEntity({"top.sanguohf.egg.test.entity"})
@SpringBootApplication
public class YangSqlTest {
    public static void main(String[] args) {
        SpringApplication.run(YangSqlTest.class);
    }
}
```

2、创建实体类和数据库表，字段一一对应，采用驼峰命名规则。如下所示

```java
@Data
@TableName("user")
public class User {
    @Id
    @Field("user_name")
    private String userName;
    private String password;
    protected String id;
    private BigDecimal price;
}
```

![1582302341327](C:\Users\杨\AppData\Roaming\Typora\typora-user-images\1582302341327.png)

3、配置数据源，连接远程数据库，如下配置

```xml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      url: jdbc:mysql://xxx:3306/springsys?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull&&allowPublicKeyRetrieval=true
      username: 账号
      password: 密码
```

4、编写Test测试文件，代码如下

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestOne {

    @Autowired
    private CommonService commonService;
    @Test
    public void test3() throws NoSuchFieldException, IOException, ClassNotFoundException 		{
        EntityParams params = new EntityParams();
        params.setTableClassName("User");
        List list = commonService.findList(params);
    }
}
```

# 3、服务说明

## 3.1前端传参

可以查看`top.sanguohf.egg.param.EntityParams`类所对应的属性。源码如下

```java
@Data
public class EntityParams {
    private String tableClassName;

    private JSONObject condition;
    private List<EntityOrderBy> orderBy;

    public List<EntityOrderBy> getOrderBy() {
        if(orderBy==null)
            orderBy=new LinkedList<>();
        return orderBy;
    }
}
```

属性详解

`tableClassName`这个属性标识当前操作的实体类名，比如User类，对应的值就是User，和**类名**一致

`condition`这个属性用来确定查询的条件，或者是更新的数据，又或者是插入的数据，又或者是删除的数据，它的格式较为复杂，我会单独开一小节进行讲解。

`orderBy`定义排序字段，前端传递的格式是形如下

```json
[
  {"column": "userName","direct": "desc"},
  {"column": "password","direct": "asc"}
]
```

## 3.2通用增删改查类

可以查看`top.sanguohf.top.bootcon.service.impl.CommonServiceImpl`实现类。方法的使用方式和mybatis的操作基本相同，都是以实体为基础。下面对方法说明

`findByEntityPageList`根据传入的条件，分页查询数据

`findEntityList`根据传入条件，查询所有数据

`countEntity`统计符合查询条件的数目

`insertEntity`向数据库中插入单个数据

`updateEntity`更新单个数据

`deleteEntity`删除单条数据

`batchEntityInsert`批量插入数据

`batchEntityUpdate`批量更新数据

`batchEntityDelete`批量删除数据

`findByPrimaryKey`根据主键查找记录（适用于表的主键是单个的情况）

`findByPrimaryKeys`根据主键的集合，查找记录值

`batchSaveEntity`批量插入或者是更新值



## 3.3condition的格式定义

### 3.3.1插入、更新、删除数据格式

这三种方式传递的数据格式都是一样的，如下示例：

```json
{
  "userName": "张三",
  "password": "王五",
  "id": "10001"
}
```

简单的JSON对象，列名是key,值是value

### 3.3.2 查询数据格式

1、基础的查询格式有两种

第一种

```json
{
  "left":"userName",
   "relation":"=",
   "right": "admin"
}
```

第二种

```json
{
   "condition": [
        {"left": "userName","relation": "like","right": "%admin%"},
        {"left": "password","relation": "=","right": "6547199"}
   ],
   "combine": "and"
}
```

2、演变出稍微复杂的格式

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

3、演变出更为复杂的格式，实现两种基础模式的多层嵌套

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
       "condition": [{
           "left": {
               "condition": [
                   {"left": "userName","relation": "=","right": "admin"},
                   {"left": "password","relation": "=","right": "123456"}
               ],"combine": "and"},
            "relation": "and",
            "right": {"left": "password","relation": "=","right": "6547199"}
           },
           {"left": "password","relation": "=","right": "6547199"}
       ],"combine": "and"
  }
}
```

### 3.3.3 条件构造器

查看类`top.sanguohf.egg.util.EntityConditionBuilder`，使用样式可以参考以下的使用案例

```java
public ResponseResult findPageList(@RequestBody BatisPage<XfSalestype> xfSalestype) throws Exception {
        XfSalestype xf = xfSalestype.getCondition() == null ? new XfSalestype() : xfSalestype.getCondition();
        EntityConditionBuilder builder = EntityConditionBuilder.getInstance().eq("dr", DelState.NORMAL);
        if (!StringUtils.isEmpty(xf.getScode())) {
            builder.like("scode", xf.getScode());
        }
        if (!StringUtils.isEmpty(xf.getStypename())) {
            builder.like("stypename", xf.getStypename());
        }
        if (!StringUtils.isEmpty(xf.getCompname())) {
            builder.like("compname", xf.getCompname());
        }
    	JSONObject jsonObject = builder.combineAnd();//最终的查询条件
        CommonPageResp<List<XfSalestype>> entityPageList = commonService.findEntityPageList(XfSalestype.class, XfSalestype.class, jsonObject, new Page(xfSalestype.getPageNo(), xfSalestype.getPageSize()));
        return new ResponseResult(CommonCode.SUCCESS, entityPageList);
    }
```



## 3.4、实体类的定义

### 3.4.1基础实体的定义

先看一个定义实例吧

```java
@TableName("user")
public class User {
    @Id
    @Field("user_name")
    private String userName;
    private String password;
    protected String id;
    @IgnoreSelectReback
    private BigDecimal price;
}
```

我们详细说明一下：

首先创建一个实体，这个实体的类名默认是采用驼峰命名规则和数据库表名对应，如User类，默认对应user表

类中的实体也是采用驼峰命名规则，和数据库列一一对应

#### 3.4.1.1 @TableName

作用：定义实体对应的数据库表名称

| 属性名 |   类型   |        作用        |     默认值     |
| :----: | :------: | :----------------: | :------------: |
| value  | String[] | 对应数据库表的名称 | 类名的驼峰转换 |

#### 3.4.1.2 @Id

作用：定义表的主键，更新会使用，默认的主键是名为Id的属性

| 属性名 | 类型 | 作用 |
| :----: | :--: | :--: |
|        |      |      |

#### 3.4.1.3 @Field

作用：定义属性对应的数据库列名称

| 属性名 |  类型  |        作用        |      默认值      |
| :----: | :----: | :----------------: | :--------------: |
| value  | String |   对应的表的列名   | 类属性的驼峰转换 |
| alias  | String | 查询列时返回的别名 |     类属性名     |

#### 3.4.1.4 @IgnoreSelectReback

作用：标识这个字段在做查询的时候不会返回

无属性

#### 3.4.1.5 @Condition

作用：定义默认的查询条件

|   属性   |  类型  |     作用     | 默认值 |
| :------: | :----: | :----------: | :----: |
|  value   | String | 定义查询的值 |        |
|   type   | String | 定义值的类型 |        |
| relation | String | 列和值的关系 |   =    |
|          | String |   列的名字   |        |

#### 3.4.1.6 @OrderBy

作用： 定义排序字段

|  属性  |  类型  |   作用   | 默认值 |
| :----: | :----: | :------: | :----: |
| column | String | 定义列名 |        |
| direct | String | 排序方向 |  desc  |



### 3.4.2视图实体的定义

我们也先看两个实例

```java
@ViewTable
public class TeacherView {

    @MainTable(tableAlias = "oneOs")
    private Teacher teacher;

    @ReferTable(tableAlias = "op",relation = "inner join",condition = "oneOs.teacherId = op.id")
    private User user;
}
```

另一个更复杂的带子查询的例子

```java
@ViewTable
public class UserClassesView {

    @Condition(column = "teacherId",value = "1")
    @MainTable(tableAlias = "userOne")
    private TeacherView teacherView;

    @ReferTable(tableAlias = "aliasClass",relation = "left join",condition = "userOne.id = aliasClass.classesId and userOne.userName = aliasClass.name")
    private Classes classes;

    @OrderBys({
            @OrderBy(column = "teacherId",direct = "desc")
    })
    @Conditions({
            @Condition(column = "teacherId",value = "1"),
            @Condition(column = "teacherName",value = "8")
    })
    @ReferTable(tableAlias = "teacher",relation = "left join",condition = "userOne.id = teacher.teacherId",includeColumns = {"teacherName"})
    private Teacher teacher;

}
```

现在我对其中的注解进行详解

#### 3.4.2.1 @ViewTable

作用：标识当前的实体是视图实体

#### 3.4.2.2 @MainTable

作用：标识关联的主表

|     属性名     |   类型   |                   作用                   |
| :------------: | :------: | :--------------------------------------: |
|   tableAlias   |  String  |                主表的别名                |
| includeColumns | String[] |   需要查出的列（优先于excludeColumns）   |
| excludeColumns | String[] | 排除掉不需要查出的列（默认是全部都查出） |

#### 3.4.2.3 @ReferTable

作用：标识关联查询的表

|     属性名     |   类型   |                   作用                   |  默认值   |
| :------------: | :------: | :--------------------------------------: | :-------: |
|   tableAlias   |  String  |                  表别名                  |           |
|    relation    |  String  |                   关系                   | left join |
|   condition    |  String  |        标识关联的条件（on后面的）        |           |
| includeColumns | String[] |   需要查出的列（优先于excludeColumns）   |           |
| excludeColumns | String[] | 排除掉不需要查出的列（默认是全部都查出） |           |

#### 3.4.2.4 @Conditions

作用：是`@Condition`注解的复数形式

| 属性名 |    类型     |     作用     |
| :----: | :---------: | :----------: |
| value  | Condition[] | 定义多个条件 |

#### 3.4.2.5 @OrderBys

作用：是`@OrderBy`注解的复数形式

| 属性名 |   类型    |       作用       |
| :----: | :-------: | :--------------: |
| value  | OrderBy[] | 定义多个排序字段 |

## 3.5 配置包扫描

`@ScanEntity`注解，可以配置多个实体包的扫描路径，如下示例

```java
@EnableTransactionManagement
@ScanEntity({"top.sanguohf.egg.test.entity","top.sanguohf.egg.configure.entity"})
@SpringBootApplication
public class YangSqlTest {
    public static void main(String[] args) {
        SpringApplication.run(YangSqlTest.class);
    }
}
```

## 3.6 开启事务

事务采用SpringBoot自带的事务控制，如下示例

```java
@EnableTransactionManagement //开启事务
@ScanEntity({"top.sanguohf.egg.test.entity","top.sanguohf.egg.configure.entity"})
@SpringBootApplication
public class YangSqlTest {
    public static void main(String[] args) {
        SpringApplication.run(YangSqlTest.class);
    }
}
```

开启事务，用于保证批量操作时的原子性

# 4、完整示例

## 4.1 基础示例

基础示例只需要三个步骤，无需编写SQL语句，直接实现数据库查询。当然数据库还是需要在yml中配置

### 4.1.1 实体类

```java
package com.kc.app.xfjl.entity;
/**这是一个具体项目中的实体，不需要知道每个字段多的含义。我为了完整性，将项目中的整个代码都精简出来*/
@Data
@TableName("xf_rMaterial")
public class XfRMaterial {

    @Id //定义此表的主键
    @Field("pk_material")
    private String pk_material;

    @Field("rMeCode")
    private String rMeCode;

    @Field("rMeCodeName")
    private String rMeCodeName;

    @Field("rMeSpecName")
    private String rMeSpecName;

    @Field("mMenicCode")
    private String mMenicCode;

    @Field("cMeMemo")
    private String cMeMemo;

    @Field("cMaker")
    private String cMaker;
    
    //@OrderBy(direct = "desc")
    @Field("dnMaketime")
    private Date dnMaketime;

    @Field("dr")
    private Integer dr;

    @Field("pk_matertype")
    private String pk_matertype;

    @Field("pk_corp")
    private String pk_corp;
}
```

### 4.1.2 controller编写

```java
package com.kc.app.xfjl.controller;

@Controller
@RequestMapping("xfRMaterial")
public class XfRMaterialController {
    //直接注入通用的service
    @Autowired
    private CommonService commonService;

    @ApiOperation("分页获取参数")
    @PostMapping("getPageList")
    @ResponseBody
    public ResponseResult findPageList(@RequestBody BatisPage<XfRMaterial> xfRMaterial) throws Exception {
        XfRMaterial xf = xfRMaterial.getCondition()==null?new XfRMaterial():xfRMaterial.getCondition();
        EntityConditionBuilder builder = EntityConditionBuilder.getInstance().eq("dr", 1);
        if(!StringUtils.isEmpty(xf.getPk_material())){
            builder.like("pk_material",xf.getPk_material());
        }
        if(!StringUtils.isEmpty(xf.getRMeCode())){
            builder.like("rMeCode",xf.getRMeCode());
        }
        if(!StringUtils.isEmpty(xf.getRMeCodeName())){
            builder.like("rMeCodeName",xf.getRMeCodeName());
        }
        if(!StringUtils.isEmpty(xf.getRMeSpecName())){
            builder.like("rMeSpecName",xf.getRMeSpecName());
        }
        if(!StringUtils.isEmpty(xf.getMMenicCode())){
            builder.like("mMenicCode",xf.getMMenicCode());
        }
        if(!StringUtils.isEmpty(xf.getTypename())){
            builder.like("typename",xf.getTypename());
        }
        if(!StringUtils.isEmpty(xf.getCMeMemo())){
            builder.like("cMeMemo",xf.getCMeMemo());
        }
        //定义查询的排序字段
        ArrayList sorts = new ArrayList<EntityOrderBy>();
        EntityOrderBy order = new EntityOrderBy();
        order.setDirect("desc");
        order.setColumn("dnMaketime");
        sorts.add(order);

        CommonPageResp<List<XfRMaterial>> entityPageList = commonService.findEntityPageList(XfRMaterialView.class, XfRMaterialVo.class,builder.combineAnd(),sorts, new Page(xfRMaterial.getPageNo(), xfRMaterial.getPageSize()));
        PageResult result = new PageResult(entityPageList.getCount(),entityPageList.data);
        return new ResponseResult(CommonCode.SUCCESS,result);
    }
    @ApiOperation("查询所有记录")
    @PostMapping("getList")
    @ResponseBody
    public ResponseResult findList(@RequestBody XfRMaterial xfRMaterial) throws Exception {
        EntityConditionBuilder builder = EntityConditionBuilder.getInstance().eq("dr", 1);
        List<XfRMaterial> list = commonService.findEntityList(XfRMaterial.class, XfRMaterial.class,builder.combineAnd());
        return new ResponseResult(CommonCode.SUCCESS,list);
    }
    @ApiOperation("插入一条记录")
    @PostMapping("save")
    @ResponseBody
    public ResponseResult save(@RequestBody XfRMaterial xfRMaterial) throws Exception {
        if(StringUtils.isEmpty(xfRMaterial.getPk_material()))
            xfRMaterial.setPk_material(UUIDUtils.createUUID());
        xfRMaterial.setDr(1);
        xfRMaterial.setDnMaketime(new Date());
        xfRMaterial.setDnModifytime(new Date());
        commonService.insertEntity(xfRMaterial);
        return new ResponseResult(CommonCode.SUCCESS,xfRMaterial);
    }
    @ApiOperation("修改一条记录")
    @PostMapping("update")
    @ResponseBody
    public ResponseResult update(@RequestBody XfRMaterial xfRMaterial) throws Exception {
        xfRMaterial.setDnModifytime(new Date());
        commonService.updateEntity(xfRMaterial);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    @ApiOperation("保存或者插入")
    @PostMapping("addOrUpdate")
    @ResponseBody
    public ResponseResult addOrUpdate(@RequestBody XfRMaterial xfRMaterial) throws Exception {
        if(StringUtils.isEmpty(xfRMaterial.getPk_material()))
            save(xfRMaterial);
        else update(xfRMaterial);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    @ApiOperation("批量删除")
    @DeleteMapping("batchDelete")
    @ResponseBody
    public ResponseResult batchDelete(@RequestBody List<XfRMaterial> xfRMaterial) throws Exception {
        commonService.batchEntityDelete(xfRMaterial);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    @ApiOperation("删除单个")
    @DeleteMapping("delete")
    @ResponseBody
    public ResponseResult delete(@RequestBody XfRMaterial xfRMaterial) throws Exception {
        xfRMaterial.setDr(0);
        xfRMaterial.setDnModifytime(new Date());
        commonService.updateEntity(xfRMaterial);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}

```

### 4.1.3 启动类

```java
@EnableTransactionManagement
@SpringBootApplication
//配置实体类的扫描路径
@ScanEntity({"com.kc.app.xfjl.entity","com.kc.app.xfjl.vo","com.kc.app.xfjl.view"})
public class XfjlApplication {
    public static void main(String[] args) {
        SpringApplication.run(XfjlApplication.class, args);
    }
}
```

### 4.1.4 yml配置

```yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      url: jdbc:mysql://ip:3306/springsys?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull&&allowPublicKeyRetrieval=true
      username: root
      password:
```

## 4.2 表关联示例

针对表关联的实现，是个很有趣的问题。请看以下的示例。

### 4.2.1 关联实体定义

```java
package com.kc.app.xfjl.view;
//定义表之间的关系（可以理解为一条join语句，可以复用之前定义的实体）
@ViewTable
public class XfRMaterialView {

    @MainTable(tableAlias = "xfRMaterialVo") //定义视图查询的主表信息（其也可是视图实体）
    private XfRMaterial xfRMaterial;

    @ReferTable(tableAlias = "xfMatertype",relation = "left join",condition = "xfRMaterialVo.pk_matertype = xfMatertype.pkMatertype",includeColumns = {"typename"})
    private XfMatertype xfMatertype; //这里也可以是其它视图
}
```

### 4.2.2 使用方法

```java
package com.kc.app.xfjl;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTest {
    @Autowired
    private CommonService commonService;
    
    @Test
    public void test3() throws Exception {
        List<Map> entityList = commonService.findEntityList(XfRMaterialView.class, Map.class);//这里的map是接收的实体类，也可以定义指定的java类型
        System.out.println(entityList);
    }
}
```

