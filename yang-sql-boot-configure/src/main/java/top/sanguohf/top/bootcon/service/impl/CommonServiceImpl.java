package top.sanguohf.top.bootcon.service.impl;

import top.sanguohf.egg.ops.*;
import top.sanguohf.egg.param.EntityParamParse;
import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.top.bootcon.callback.BatchInsertCallback;
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
        long count=Long.parseLong(""+counts.get("total"));
        return count;
    }

    @Override
    public void insert(EntityParams paramData) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(paramData);
        EntityInsertSql insertSql = new EntityParamParse(params1).parseToEntityInertSql();
        jdbcTemplate.execute(insertSql.toSql(dbType.getDbType()));
    }

    @Transactional
    @Override
    public void update(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(paramsData);
        EntityUpdateSql updateSql = new EntityParamParse(params1).parseToEntityUpdateSql();
        jdbcTemplate.update(updateSql.toSql(dbType.getDbType()));
    }

    @Override
    public void delete(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException {
        EntityParams params1 = inteceptor(paramsData);
        EntityDeleteSql deleteSql = new EntityParamParse(params1).parseToEntityDeleteSql();
        jdbcTemplate.execute(deleteSql.toSql(dbType.getDbType()));
    }

    @Transactional
    @Override
    public void batchInsert(List<EntityParams> params) throws IOException {
        if(params.size()>0) {
            List<EntityParams> entityParams = inteceptorList(params);
            LinkedList<String> strings = entityParams.stream().collect(LinkedList<String>::new, (list, v) -> {
                try {
                    EntityInsertSql updateSql = new EntityParamParse(v).parseToEntityInertSql();
                    list.add(updateSql.toSql(dbType.getDbType()));
                } catch (ClassNotFoundException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }, List::addAll);
            BatchInsertCallback callback = new BatchInsertCallback(strings);
            jdbcTemplate.execute(callback);
        }
    }

    @Transactional
    @Override
    public void batchUpdate(List<EntityParams> params) throws IOException {
        if(params.size()>0) {
            List<EntityParams> entityParams = inteceptorList(params);
            LinkedList<String> strings = entityParams.stream().collect(LinkedList<String>::new, (list, v) -> {
                try {
                    EntityUpdateSql updateSql = new EntityParamParse(v).parseToEntityUpdateSql();
                    list.add(updateSql.toSql(dbType.getDbType()));
                } catch (ClassNotFoundException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }, List::addAll);
            BatchInsertCallback callback = new BatchInsertCallback(strings);
            jdbcTemplate.execute(callback);
        }
        //jdbcTemplate.batchUpdate(strings.get(0),strings.get(1));
    }

    @Override
    public void batchDelete(List<EntityParams> params) throws IOException {
        if(params.size()>0) {
            List<EntityParams> entityParams = inteceptorList(params);
            LinkedList<String> strings = entityParams.stream().collect(LinkedList<String>::new, (list, v) -> {
                try {
                    EntityDeleteSql updateSql = new EntityParamParse(v).parseToEntityDeleteSql();
                    list.add(updateSql.toSql(dbType.getDbType()));
                } catch (ClassNotFoundException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }, List::addAll);
            BatchInsertCallback callback = new BatchInsertCallback(strings);
            jdbcTemplate.execute(callback);
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
            list.add(pl);
        }
        return list;
    }
}
