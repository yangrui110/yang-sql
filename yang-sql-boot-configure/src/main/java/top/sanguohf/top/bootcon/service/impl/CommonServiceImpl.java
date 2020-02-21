package top.sanguohf.top.bootcon.service.impl;

import org.springframework.jdbc.core.PreparedStatementCreator;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.ops.*;
import top.sanguohf.egg.param.EntityParamParse;
import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.top.bootcon.config.DataBaseTypeInit;
import top.sanguohf.top.bootcon.config.ScanEntityConfigure;
import top.sanguohf.top.bootcon.page.Page;
import top.sanguohf.top.bootcon.resp.CommonPageResp;
import top.sanguohf.top.bootcon.service.CommonService;
import top.sanguohf.top.bootcon.util.ClassInfoUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ScanEntityConfigure configure;

    @Autowired
    DataBaseTypeInit dbType;

    @Override
    public CommonPageResp findPageList(EntityParams params, Page page) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(params);
        EntitySelectSql selectSql = new EntityParamParse(params1).parseToEntitySelectSql();
        long count=count(params);
        CommonPageResp resp=new CommonPageResp<>();
        if(count!=0){
            //获取到数据集
            String toPageSql = new EntityPageSql(selectSql).toPageSql(page.getPage(), page.getSize(),dbType.getDbType());
            System.out.println("查询语句："+toPageSql);
            List<Map<String, Object>> querys = jdbcTemplate.queryForList(toPageSql);
            resp.setData(querys);
        }
        resp.setCount(count);
        return resp;
    }

    @Override
    public List findList(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(params);
        EntitySelectSql selectSql = new EntityParamParse(params1).parseToEntitySelectSql();
        System.out.println("查询语句："+selectSql.toSql());
        List<Map<String, Object>> query = jdbcTemplate.queryForList(selectSql.toSql(dbType.getDbType()));
        return query;
    }

    @Override
    public long count(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(params);
        EntitySelectSql selectSql = new EntityParamParse(params1).parseToEntitySelectSql();
        String countSql = new EntityPageSql(selectSql).toCountSql(dbType.getDbType());
        Map<String,Object> counts = jdbcTemplate.queryForMap(countSql);
        System.out.println("查询语句:"+countSql);
        long count=Long.parseLong(""+counts.get("__total__"));
        return count;
    }

    @Override
    public void insert(EntityParams paramData) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(paramData);
        EntityInsertSql insertSql = new EntityParamParse(params1).parseToEntityInertSql();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement statement = insertSql.toSql(connection);
                return statement;
            }
        });
//        jdbcTemplate.execute(insertSql.toSql(dbType.getDbType()));
    }

    @Transactional
    @Override
    public void update(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(paramsData);
        EntityUpdateSql updateSql = new EntityParamParse(params1).parseToEntityUpdateSql();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement statement = updateSql.toSql(connection);
                return statement;
            }
        });
    }

    @Override
    public void delete(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(paramsData);
        EntityDeleteSql deleteSql = new EntityParamParse(params1).parseToEntityDeleteSql();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement statement = deleteSql.toSql(connection);
                return statement;
            }
        });
    }

    @Transactional
    @Override
    public void batchInsert(List<EntityParams> params) throws IOException {
        if(params.size()>0) {
            List<EntityParams> entityParams = inteceptorList(params);
            LinkedList<EntityInsertSql> strings = entityParams.stream().collect(LinkedList<EntityInsertSql>::new, (list, v) -> {
                try {
                    EntityInsertSql updateSql = new EntityParamParse(v).parseToEntityInertSql();
                    list.add(updateSql);
                } catch (ClassNotFoundException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }, List::addAll);
            //检测长度是否是一致
            EntityInsertSql first = strings.getFirst();
            for(EntityInsertSql one:strings){
                if(first.getInsertList().size()!=one.getInsertList().size()){
                    throw new RuntimeException("批量插入的数据列数不一致");
                }
            }
            List<Object[]> values = new LinkedList<>();
            //保证排序的一致性
            List<EntityInsert> insertList = first.getInsertList();

            //根据列名的顺序收集到值
            for (EntityInsertSql one : strings) {
                List<EntityInsert> list = one.getInsertList();
                values.add(collectSortValue(list,insertList));
            }
            //获取到预加载的sql语句
            String sqlOne = first.sqlOne(true);
            jdbcTemplate.batchUpdate(sqlOne,values);
        }
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
    @Transactional
    @Override
    public void batchUpdate(List<EntityParams> params) throws IOException {
        if(params.size()>0) {
            List<EntityParams> entityParams = inteceptorList(params);
            LinkedList<EntityUpdateSql> strings = entityParams.stream().collect(LinkedList<EntityUpdateSql>::new, (list, v) -> {
                try {
                    EntityUpdateSql updateSql = new EntityParamParse(v).parseToEntityUpdateSql();
                    list.add(updateSql);
                } catch (ClassNotFoundException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }, List::addAll);
            //检测长度是否是一致
            EntityUpdateSql first = strings.getFirst();
            for(EntityUpdateSql one:strings){
                int len1 = first.getWheres().size()+first.getUpdates().size();
                int len2 = one.getUpdates().size()+one.getWheres().size();
                if(len1!=len2){
                    throw new RuntimeException("批量更新的数据列不一致");
                }
            }
            List<Object[]> values = new LinkedList<>();
            //根据列名的顺序收集到值
            for (EntityUpdateSql one : strings) {
                Object[] updates = collectSortValue(one.getUpdates(), first.getUpdates());
                Object[] wheres = collectSortValue(one.getWheres(), first.getWheres());
                Object[] objects = new Object[updates.length + wheres.length];
                System.arraycopy(updates,0,objects,0,updates.length);
                System.arraycopy(wheres,0,objects,updates.length,wheres.length);
                values.add(objects);
            }
            //获取到预加载的sql语句
            String sqlOne = first.one(true);
            System.out.println(sqlOne);
            jdbcTemplate.batchUpdate(sqlOne,values);
        }
    }

    @Override
    public void batchDelete(List<EntityParams> params) throws IOException {
        if(params.size()>0) {
            List<EntityParams> entityParams = inteceptorList(params);
            LinkedList<EntityDeleteSql> strings = entityParams.stream().collect(LinkedList<EntityDeleteSql>::new, (list, v) -> {
                try {
                    EntityDeleteSql updateSql = new EntityParamParse(v).parseToEntityDeleteSql();
                    list.add(updateSql);
                } catch (ClassNotFoundException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }, List::addAll);
            //检测长度是否是一致
            EntityDeleteSql first = strings.getFirst();
            for(EntityDeleteSql one:strings){
                if(first.getWheres().size()!=one.getWheres().size()){
                    throw new RuntimeException("批量删除数据列数不一致");
                }
            }
            List<Object[]> values = new LinkedList<>();
            //保证排序的一致性
            List<EntityInsert> insertList = first.getWheres();

            //根据列名的顺序收集到值
            for (EntityDeleteSql one : strings) {
                List<EntityInsert> list = one.getWheres();
                values.add(collectSortValue(list,insertList));
            }
            //获取到预加载的sql语句
            String sqlOne = first.one(true);
            jdbcTemplate.batchUpdate(sqlOne,values);
        }
    }

    private EntityParams inteceptor(EntityParams params) throws IOException {
        EntityParams params1 = new EntityParams();
        BeanUtils.copyProperties(params,params1);
        String classPackage = ClassInfoUtil.getPackageByRelativeName(params.getTableClassName(),configure.getBasePackage());
        params1.setTableClassName(classPackage);
        return params1;
    }

    private List<EntityParams> inteceptorList(List<EntityParams> prs) throws IOException {
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
