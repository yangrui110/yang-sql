package top.sanguohf.egg.param;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.base.*;
import top.sanguohf.egg.ops.EntityDeleteSql;
import top.sanguohf.egg.ops.EntityInsertSql;
import top.sanguohf.egg.ops.EntitySelectSql;
import top.sanguohf.egg.ops.EntityUpdateSql;
import top.sanguohf.egg.reflect.ReflectEntity;
import top.sanguohf.egg.util.EntityParseUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EntityParamParse {

    private EntityParams params;

    private Class classEntity;

    public EntityParamParse(EntityParams entityParams) throws ClassNotFoundException {
        this.params = entityParams;
        //获取到指定的class实体
        classEntity = Class.forName(entityParams.getTableClassName());
    }

    public EntitySelectSql parseToEntitySelectSql() throws ClassNotFoundException, NoSuchFieldException {
        EntitySelectSql selectSql = new EntitySelectSql();
        //1.构造实体条件
        JSONObject condition= params.getCondition();
        if(condition !=null){
            JSONObject selectCondition = EntityParseUtil.saveConditionToSelectCondition(condition);
            if(selectCondition!=null)
                condition=selectCondition;
            EntityCondition condition1 = parserParamCondition(condition);
            //System.out.println(condition1.toSql());
            selectSql.setWheres(condition1);
        }
        //2.确定查询的数据库表
        String tableName= ReflectEntity.reflectTableName(classEntity);
        selectSql.setTabelName(tableName);
        //3.确定查询的列名
        List<EntityColumn> columnList=ReflectEntity.reflectSelectColumns(classEntity);
        selectSql.setColumns(columnList);
        //4.确定排序
        List<EntityOrderBy> orderBy=params.getOrderBy();
        if(orderBy !=null) {
            for (EntityOrderBy order : orderBy) {
                order.setColumn(ReflectEntity.getTableField(classEntity, order.getColumn()));
            }
        }
        selectSql.setOrderBys(params.getOrderBy());
        return selectSql;
    }
    private EntityCondition parserParamCondition(Map condition) throws NoSuchFieldException, ClassNotFoundException {
        Object os = condition.get("condition");
        if(os==null){
            EntityConditionDom dom = parseOneCondition(condition);
            return dom;
        }else {
            List ls = (List) os;
            EntityListCondition condition1=parseListCondition(ls);
            condition1.setCombine((String) condition.get("combine"));
            return condition1;
        }
    }
    //解析List
    private EntityListCondition parseListCondition(List ls) throws NoSuchFieldException, ClassNotFoundException {
        //有值的情况
        EntityListCondition condition1=new EntityListCondition();
        List lm=new LinkedList();
        for(Object one : ls){
            Map oo= (Map) one;
            lm.add(parseOneCondition(oo));
        }
        condition1.setCondition(lm);
        return condition1;
    }
    //解析一个基础DOM元素
    private EntityConditionDom parseOneCondition(Map map) throws NoSuchFieldException, ClassNotFoundException {
        EntityConditionDom dom = new EntityConditionDom();
        Object left=map.get("left");
        dom.setLeft(parseToCondition(left,false));
        Object relation=map.get("relation");
        dom.setRelation((String) relation);
        Object right=map.get("right");
        EntityCondition condition=parseToCondition(right,true);
        dom.setRight(condition);
        return dom;
    }
    //解析left或者right元素
    private EntityCondition parseToCondition(Object os,boolean isRight) throws ClassNotFoundException, NoSuchFieldException {
        EntityCondition condition=null;
        if(os !=null && os instanceof Map) {
            condition = parserParamCondition((Map) os);
            return condition;
        }else if(os !=null){
            if(!isRight) {
                condition = new EntityConditionColumn();
                ((EntityConditionColumn) condition).setColumn(ReflectEntity.getTableField(classEntity,(String) os));
            } else {
                condition=new EntityConditionValue();
                ((EntityConditionValue) condition).setColumn(os);
            }

            return condition;
        }
        return null;
    }

    public EntityInsertSql parseToEntityInertSql() throws ClassNotFoundException, NoSuchFieldException {
        // TODO: implement
        //此种情况，主要是解析data的值
        EntityInsertSql insertSql=new EntityInsertSql();
        JSONObject os = params.getCondition();
        List<EntityInsert> inserts = new LinkedList<>();
        for(String k:os.keySet()){
            EntityInsert insert = new EntityInsert();
            insert.setColumn(ReflectEntity.getTableField(classEntity,k));
            insert.setValue(os.get(k));
            inserts.add(insert);
        }

        insertSql.setTableName(ReflectEntity.reflectTableName(classEntity));
        insertSql.setInsertList(inserts);
        System.out.println(insertSql.toSql());
        return insertSql;
    }

    public EntityUpdateSql parseToEntityUpdateSql() throws NoSuchFieldException, ClassNotFoundException {
        // TODO: implement
        EntityUpdateSql updateSql = new EntityUpdateSql();
        JSONObject os = params.getCondition();
        List<EntityInsert> inserts = new LinkedList<>();
        for(String k:os.keySet()){
            EntityInsert insert = new EntityInsert();
            insert.setColumn(ReflectEntity.getTableField(classEntity,k));
            insert.setValue(os.get(k));
            inserts.add(insert);
        }
        updateSql.setTableName(ReflectEntity.reflectTableName(classEntity));
        updateSql.setUpdates(inserts);
        updateSql.setWheres(ReflectEntity.reflectPrimaryKeys(classEntity,os));
        System.out.println(updateSql.toSql());
        return updateSql;
    }

    public EntityDeleteSql parseToEntityDeleteSql() throws NoSuchFieldException, ClassNotFoundException {
        // TODO: implement
        EntityDeleteSql deleteSql=new EntityDeleteSql();
        deleteSql.setTableName(ReflectEntity.reflectTableName(classEntity));
        JSONObject condition= params.getCondition();
        deleteSql.setWheres(ReflectEntity.reflectPrimaryKeys(classEntity,condition));
        System.out.println(deleteSql.toSql());
        return deleteSql;
    }
}
