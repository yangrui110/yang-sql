package top.sanguohf.top.bootcon.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.base.EntityOrderBy;
import top.sanguohf.egg.ops.*;
import top.sanguohf.egg.param.EntityParamParse;
import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.egg.reflect.ReflectEntity;
import top.sanguohf.egg.util.ConsoleSqlUtil;
import top.sanguohf.egg.util.EntityConditionBuilder;
import top.sanguohf.top.bootcon.config.DataBaseTypeInit;
import top.sanguohf.top.bootcon.config.ScanEntityConfigure;
import top.sanguohf.top.bootcon.page.Page;
import top.sanguohf.top.bootcon.resp.CommonPageResp;
import top.sanguohf.top.bootcon.service.CommonService;
import top.sanguohf.top.bootcon.util.ClassInfoUtil;
import top.sanguohf.top.bootcon.util.ObjectUtil;
import top.sanguohf.top.bootcon.util.ParamEntityParseUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ScanEntityConfigure configure;

    @Autowired
    DataBaseTypeInit dbType;
    @Autowired
    DataSource dataSource;

    @Override
    public CommonPageResp findPageList(EntityParams params, Page page) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException {
        EntityParams params1 = inteceptor(params);
        EntitySelectSql selectSql = new EntityParamParse(params1).parseToEntitySelectSql();
        long count=count(params);
        CommonPageResp resp=new CommonPageResp<>();
        if(count!=0){
            //获取到数据集
            EntityPageSql entityPageSql = new EntityPageSql(selectSql);
            String toPageSql = entityPageSql.toPageSql(page.getPage(), page.getSize(),true,dbType.getDbType());
            LinkedList objects = new LinkedList<>();
            entityPageSql.addValue(page.getPage(),page.getSize(),dbType.getDbType(),objects);
            List<Map<String, Object>> querys = jdbcTemplate.queryForList(toPageSql,objects.toArray());
            ConsoleSqlUtil.console(toPageSql);
            ConsoleSqlUtil.consoleParam(objects);
            ConsoleSqlUtil.consoleResult(querys);
            resp.setData(querys);
        }
        resp.setCount(count);
        return resp;
    }

    @Override
    public List findList(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException {
        EntityParams params1 = inteceptor(params);
        EntitySelectSql selectSql = new EntityParamParse(params1).parseToEntitySelectSql();
        String sqlOne = selectSql.sqlOne(true);
        LinkedList objects = new LinkedList<>();
        selectSql.addValue(objects);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sqlOne, objects.toArray());
        ConsoleSqlUtil.console(sqlOne);
        ConsoleSqlUtil.consoleParam(objects);
        ConsoleSqlUtil.consoleResult(mapList);
        return mapList;
    }

    @Override
    public long count(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException {
        EntityParams params1 = inteceptor(params);
        EntitySelectSql selectSql = new EntityParamParse(params1).parseToEntitySelectSql();
        EntityPageSql entityPageSql = new EntityPageSql(selectSql);
        String countSql = entityPageSql.toCountSql(dbType.getDbType(), true);
        LinkedList objects = new LinkedList<>();
        entityPageSql.addCountValue(objects);
        Map<String,Object> counts = jdbcTemplate.queryForMap(countSql,objects.toArray());
        ConsoleSqlUtil.console(countSql);
        ConsoleSqlUtil.consoleParam(objects);
        ConsoleSqlUtil.consoleResult(counts);
        long count=Long.parseLong(""+counts.get("__total__"));
        return count;
    }

    @Override
    public void insert(EntityParams paramData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException {
        EntityParams params1 = inteceptor(paramData);
        EntityInsertSql insertSql = new EntityParamParse(params1).parseToEntityInertSql();
        String sqlOne = insertSql.sqlOne(true);
        LinkedList objects = new LinkedList<>();
        insertSql.addValue(objects);
        ConsoleSqlUtil.console(sqlOne);
        ConsoleSqlUtil.consoleParam(objects);
        jdbcTemplate.update(sqlOne,objects.toArray());
    }

    @Transactional
    @Override
    public void update(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException {
        EntityParams params1 = inteceptor(paramsData);
        EntityUpdateSql updateSql = new EntityParamParse(params1).parseToEntityUpdateSql();
        String sqlOne = updateSql.sqlOne(true);
        LinkedList objects = new LinkedList<>();
        updateSql.addValue(objects);
        ConsoleSqlUtil.console(sqlOne);
        ConsoleSqlUtil.consoleParam(objects);
        jdbcTemplate.update(sqlOne,objects.toArray());

    }

    @Override
    public void delete(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException {
        EntityParams params1 = inteceptor(paramsData);
        EntityDeleteSql deleteSql = new EntityParamParse(params1).parseToEntityDeleteSql();
        String sqlOne = deleteSql.sqlOne(true);
        LinkedList objects = new LinkedList<>();
        deleteSql.addValue(objects);
        ConsoleSqlUtil.console(sqlOne);
        ConsoleSqlUtil.consoleParam(objects);
        jdbcTemplate.update(sqlOne,objects.toArray());
    }

    @Transactional
    @Override
    public void batchInsert(List<EntityParams> params) throws IOException, InvocationTargetException, IllegalAccessException {
        List<EntityParams> entityParams = inteceptorList(params);
        Connection con = null;
        try {
            con=DataSourceUtils.getConnection(dataSource);
            con.setAutoCommit(false);
            for (EntityParams params1 : entityParams) {
                EntityInsertSql updateSql = new EntityParamParse(params1).parseToEntityInertSql();
                executeSql(updateSql,con);
            }
            con.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    private void executeSql(AbstractEntityJoinTable updateSql,Connection con) throws SQLException {
        String sqlOne = updateSql.sqlOne(true);
        LinkedList objects = new LinkedList<>();
        updateSql.addValue(objects);
        Object[] toArray = objects.toArray(); //提升访问效率
        PreparedStatement statement = con.prepareStatement(sqlOne);
        for (int i = 0; i < toArray.length; i++) {
            Object o = toArray[i];
            if(o instanceof Date){
                java.sql.Date date = new java.sql.Date(((Date) o).getTime());
                statement.setObject(i + 1,date );
            }else statement.setObject(i + 1, o);

        }
        statement.executeUpdate();
        ConsoleSqlUtil.console(sqlOne);
        ConsoleSqlUtil.consoleParam(objects);
    }
    private Object[] collectSortValue(List<EntityInsert> list,List<EntityInsert> firstDomColumns){
        Object[] value = new Object[list.size()];
        int i= 0;
        for(EntityInsert insert:firstDomColumns) {
            boolean exist =false;
            for (EntityInsert cur:list) {
                if(cur.getColumn().equalsIgnoreCase(insert.getColumn())) {
                    value[i] = cur.getValue();
                    i++;
                    exist = true;
                }
            }
            if(!exist)
                throw new RuntimeException("当前列名："+insert.getColumn()+"不存在");
        }
        return value;
    }
    @Override
    public void batchUpdate(List<EntityParams> params) throws Exception {
        List<EntityParams> entityParams = inteceptorList(params);
        Connection con = null;
        try {
             con=DataSourceUtils.getConnection(dataSource);
            con.setAutoCommit(false);
            for (EntityParams params1 : entityParams) {
                EntityUpdateSql updateSql = new EntityParamParse(params1).parseToEntityUpdateSql();
                executeSql(updateSql,con);
            }
            con.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    @Override
    public void batchDelete(List<EntityParams> params) throws IOException, InvocationTargetException, IllegalAccessException {
        List<EntityParams> entityParams = inteceptorList(params);
        Connection con = null;
        try {
            con=DataSourceUtils.getConnection(dataSource);
            con.setAutoCommit(false);
            for (EntityParams params1 : entityParams) {
                EntityDeleteSql updateSql = new EntityParamParse(params1).parseToEntityDeleteSql();
                executeSql(updateSql,con);
            }
            con.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    /**
     * 只适配于单个主键
     * 复合主键，暂时不适用
     * */
    @Override
    public void batchSave(List<EntityParams> params) throws Exception {
        List<EntityParams> params2 = inteceptorList(params);
        //1.首先根据主键集合找到所有符合条件的数据
        List<EntityInsert> arrayList = new LinkedList<>();
        Class t= null;
        String primaryKey = "";
        for(EntityParams params1:params2){
            t= Class.forName(params1.getTableClassName());
            List<EntityInsert> entityInserts = ReflectEntity.reflectPrimaryKeys(t, params1.getCondition());
            arrayList.addAll(entityInserts);
            primaryKey = entityInserts.get(0).getColumn();
        }
        List<Object> collect = arrayList.stream().map(item -> item.getValue()).collect(Collectors.toList());
        List byPrimaryKeys = findByPrimaryKeys(t,t, collect);
        //2.根据数据的存在与否，构造出不同的SQL语句
        Connection con = null;
        try {
            con=DataSourceUtils.getConnection(dataSource);
            con.setAutoCommit(false);
            for (EntityParams params1 : params2) {
                boolean exist = false;
                for(Object os : byPrimaryKeys){
                    Map one = JSONObject.parseObject(JSONObject.toJSONString(os));
                    if(one.get(primaryKey).equals(params1.getCondition().getString(primaryKey))){
                        exist = true;
                    }
                }
                if(exist) {
                    EntityUpdateSql updateSql = new EntityParamParse(params1).parseToEntityUpdateSql();
                    executeSql(updateSql, con);
                }else {
                    EntityInsertSql updateSql = new EntityParamParse(params1).parseToEntityInertSql();
                    executeSql(updateSql, con);
                }
            }
            con.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }

    @Override
    public <T, E> CommonPageResp<List<E>> findEntityPageList(Class<T> viewClass, Class<E> toJavaBean, Page page) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        CommonPageResp pageList = findPageList(entityParams, page);
        List listData = (List) pageList.getData();
        if(listData==null) {
            CommonPageResp<List<E>> resp = new CommonPageResp<>();
            resp.setData(new ArrayList<>());
            resp.setCount(0);
            return resp;
        }
        List<E> parseList = ObjectUtil.parseList(listData, toJavaBean);
        pageList.setData(parseList);
        return pageList;
    }

    /*@Override
    public <T,E> CommonPageResp<List<E>> findEntityPageList(T data, Class<E> toJavaBean, Page page) throws Exception {
        EntityParams entityParams = ParamEntityParseUtil.parseToParam(data);
        CommonPageResp pageList = findPageList(entityParams, page);
        List listData = (List) pageList.getData();
        if(listData==null) {
            CommonPageResp<List<E>> resp = new CommonPageResp<>();
            resp.setData(new ArrayList<>());
            resp.setCount(0);
            return resp;
        }
        List<E> parseList = ObjectUtil.parseList(listData, toJavaBean);
        pageList.setData(parseList);
        return pageList;
    }*/

    @Override
    public <T,E> CommonPageResp<List<E>> findEntityPageList(Class<T> viewClass,Class<E> toJavaBean, JSONObject condition, Page page) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        entityParams.setCondition(condition);
        CommonPageResp pageList = findPageList(entityParams, page);
        List listData = (List) pageList.getData();
        if(listData==null) {
            CommonPageResp<List<E>> resp = new CommonPageResp<>();
            resp.setData(new ArrayList<>());
            resp.setCount(0);
            return resp;
        }
        List<E> parseList = ObjectUtil.parseList(listData, toJavaBean);
        pageList.setData(parseList);
        return pageList;
    }

    @Override
    public <T,E> CommonPageResp<List<E>> findEntityPageList(Class<T> viewClass,Class<E> toJavaBean, JSONObject condition, List<EntityOrderBy> orderBys, Page page) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        entityParams.setCondition(condition);
        entityParams.setOrderBy(orderBys);
        CommonPageResp pageList = findPageList(entityParams, page);
        List listData = (List) pageList.getData();
        if(listData==null) {
            CommonPageResp<List<E>> resp = new CommonPageResp<>();
            resp.setData(new ArrayList<>());
            resp.setCount(0);
            return resp;
        }
        List<E> parseList = ObjectUtil.parseList(listData, toJavaBean);
        pageList.setData(parseList);
        return pageList;
    }

    @Override
    public <T,E> CommonPageResp<List<E>> findEntityPageList(Class<T> viewClass,Class<E> toJavaBean, List<EntityOrderBy> orderBys, Page page) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        entityParams.setOrderBy(orderBys);
        CommonPageResp pageList = findPageList(entityParams, page);
        List listData = (List) pageList.getData();
        if(listData==null) {
            CommonPageResp<List<E>> resp = new CommonPageResp<>();
            resp.setData(new ArrayList<>());
            resp.setCount(0);
            return resp;
        }
        List<E> parseList = ObjectUtil.parseList(listData, toJavaBean);
        pageList.setData(parseList);
        return pageList;
    }

    @Override
    public <T,E> List<E> findEntityList(Class<T> viewClass,Class<E> toJavaBean) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        List list = findList(entityParams);
        if(list==null) {
            ArrayList<E> list1 = new ArrayList<>();
            return list1;
        }
        List<E> parseList = ObjectUtil.parseList(list, toJavaBean);
        return parseList;
    }

    @Override
    public <T,E> List<E> findEntityList(Class<T> viewClass,Class<E> toJavaBean, JSONObject condition) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        entityParams.setCondition(condition);
        List list = findList(entityParams);
        if(list==null) {
            ArrayList<E> list1 = new ArrayList<>();
            return list1;
        }
        List<E> parseList = ObjectUtil.parseList(list, toJavaBean);
        return parseList;
    }

    @Override
    public <T,E> List<E> findEntityList(Class<T> viewClass,Class<E> toJavaBean, JSONObject condition, List<EntityOrderBy> orderBys) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        entityParams.setCondition(condition);
        entityParams.setOrderBy(orderBys);
        List list = findList(entityParams);
        if(list==null) {
            ArrayList<E> list1 = new ArrayList<>();
            return list1;
        }
        List<E> parseList = ObjectUtil.parseList(list, toJavaBean);
        return parseList;
    }

    @Override
    public <T,E> List<E> findEntityList(Class<T> viewClass,Class<E> toJavaBean, List<EntityOrderBy> orderBys) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        entityParams.setOrderBy(orderBys);
        List list = findList(entityParams);
        if(list==null) {
            ArrayList<E> list1 = new ArrayList<>();
            return list1;
        }
        List<E> parseList = ObjectUtil.parseList(list, toJavaBean);
        return parseList;
    }

    @Override
    public <T> long countEntity(T params) throws Exception {
        EntityParams entityParams = ParamEntityParseUtil.parseToParam(params);
        return count(entityParams);
    }

    @Override
    public <T> void insertEntity(T paramData) throws Exception {
        EntityParams entityParams = ParamEntityParseUtil.parseToParam(paramData);
        insert(entityParams);
    }

    @Override
    public <T> void updateEntity(T paramsData) throws Exception {
        EntityParams entityParams = ParamEntityParseUtil.parseToParam(paramsData);
        update(entityParams);
    }

    @Override
    public <T> void deleteEntity(T paramsData) throws Exception {
        EntityParams entityParams = ParamEntityParseUtil.parseToParam(paramsData);
        delete(entityParams);
    }

    @Override
    public <T> void batchEntityInsert(List<T> params) throws IOException, IllegalAccessException, InvocationTargetException {
        ArrayList<EntityParams> entityParams = new ArrayList<>();
        for(T data:params){
            entityParams.add(ParamEntityParseUtil.parseToParam(data));
        }
        batchInsert(entityParams);
    }

    @Override
    public <T> void batchEntityUpdate(List<T> params) throws Exception {
        ArrayList<EntityParams> entityParams = new ArrayList<>();
        for(T data:params){
            entityParams.add(ParamEntityParseUtil.parseToParam(data));
        }
       batchUpdate(entityParams);
    }

    @Override
    public <T> void batchEntityDelete(List<T> params) throws Exception {
        ArrayList<EntityParams> entityParams = new ArrayList<>();
        for(T data:params){
            entityParams.add(ParamEntityParseUtil.parseToParam(data));
        }
        batchDelete(entityParams);
    }

    @Override
    public <T,E> E findByPrimaryKey(Class<T> viewClass,Class<E> toJavaBean, Object key) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        Map<String, Object> primaryKeys = ReflectEntity.reflectPrimaryKeys(viewClass);
        EntityConditionBuilder builder = EntityConditionBuilder.getInstance();
        for(String pk:primaryKeys.keySet()){
            builder.eq(pk,key);
        }
        entityParams.setCondition(builder.combineAnd());
        List list = findList(entityParams);
        List<E> parseList = ObjectUtil.parseList(list, toJavaBean);
        if(parseList.size()>1)
            throw new RuntimeException("结果数大于2条，最多只能返回一条");
        if(parseList.size()>0)
            return parseList.get(0);
        return null;
    }

    @Override
    public <T,E> List<E> findByPrimaryKeys(Class<T> viewClass,Class<E> toJavaBean, List<? extends Object> keys) throws Exception {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName(viewClass.getSimpleName());
        Map<String, Object> primaryKeys = ReflectEntity.reflectPrimaryKeys(viewClass);
        EntityConditionBuilder builder = EntityConditionBuilder.getInstance();
        for(String pk:primaryKeys.keySet()){
            builder.in(pk,keys.toArray());
        }
        entityParams.setCondition(builder.combineAnd());
        List list = findList(entityParams);
        List<E> parseList = ObjectUtil.parseList(list, toJavaBean);
        return parseList;
    }

    @Override
    public <T> void batchSaveEntity(List<T> entitys) throws Exception {
        ArrayList<EntityParams> entityParams = new ArrayList<>();
        for(T data:entitys){
            entityParams.add(ParamEntityParseUtil.parseToParam(data));
        }
        batchSave(entityParams);
    }

    private EntityParams inteceptor(EntityParams params) throws IOException, InvocationTargetException, IllegalAccessException {
        EntityParams params1 = new EntityParams();
        BeanUtils.copyProperties(params,params1);
        String classPackage = ClassInfoUtil.getPackageByRelativeName(params.getTableClassName(),configure.getBasePackage());
        params1.setTableClassName(classPackage);
        return params1;
    }

    private List<EntityParams> inteceptorList(List<EntityParams> prs) throws IOException, InvocationTargetException, IllegalAccessException {
        EntityParams params = prs.get(0);
        LinkedList<EntityParams> list = new LinkedList<>();
        String classPackage = ClassInfoUtil.getPackageByRelativeName(params.getTableClassName(),configure.getBasePackage());
        for(EntityParams pl: prs){
            EntityParams entityParams = new EntityParams();
            BeanUtils.copyProperties(pl,entityParams);
            entityParams.setTableClassName(classPackage);
            list.add(entityParams);
        }
        return list;
    }
}
