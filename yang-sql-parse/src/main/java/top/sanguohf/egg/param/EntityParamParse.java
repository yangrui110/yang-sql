package top.sanguohf.egg.param;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.annotation.ViewTable;
import top.sanguohf.egg.base.*;
import top.sanguohf.egg.ops.EntityDeleteSql;
import top.sanguohf.egg.ops.EntityInsertSql;
import top.sanguohf.egg.ops.EntitySelectSql;
import top.sanguohf.egg.ops.EntityUpdateSql;
import top.sanguohf.egg.reflect.ReflectEntity;
import top.sanguohf.egg.util.EntityParseUtil;
import top.sanguohf.egg.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EntityParamParse {

    private EntityParams params;

    private Class classEntity;

    private EntitySelectSql selectSql;

    private boolean present =false;

    public EntityParamParse(EntityParams entityParams) throws ClassNotFoundException {
        this.params = entityParams;
        //获取到指定的class实体
        classEntity = Class.forName(entityParams.getTableClassName());
        present = classEntity.isAnnotationPresent(ViewTable.class);
    }



    public EntitySelectSql parseToEntitySelectSql() throws ClassNotFoundException, NoSuchFieldException {
        //1.构造实体条件
        selectSql = (EntitySelectSql) EntityParseUtil.parseViewEntityTable(classEntity);

        JSONObject condition= params.getCondition();
        EntityCondition entityCondition = ReflectEntity.collectDefaultCondition(classEntity);
        if(condition !=null&&condition.keySet().size()>0){
            JSONObject selectCondition = EntityParseUtil.saveConditionToSelectCondition(condition);
            //EntityParseUtil.excludeNoExistColumn(selectCondition, selectSql.getColumns());
            if(selectCondition!=null)
                condition=selectCondition;
            EntityCondition condition1 = parserParamCondition(condition);
            if(entityCondition!=null&&condition1!=null){
                EntityConditionDom conditionDom = new EntityConditionDom();
                conditionDom.setLeft(entityCondition);
                conditionDom.setRight(condition1);
                conditionDom.setRelation("and");
                selectSql.setWheres(conditionDom);
            }else {
                selectSql.setWheres(condition1==null?entityCondition:condition1);
            }
        }
        List<EntityOrderBy> orderBy=params.getOrderBy();
        List<EntityColumn> entityColumns = selectSql.getColumns();
        for(EntityOrderBy orderBy1: orderBy){
            for(EntityColumn column:entityColumns){
                String alias = column.getAliasColumn()==null?column.getOrignColumn():column.getAliasColumn();
                if(alias.equals(orderBy1.getColumn())){
                    EntityOrderBy order = new EntityOrderBy();
                    order.setDirect(orderBy1.getDirect());
                    order.setColumn(column.getOrignColumn());
                    order.setTableAlias(column.getTableAlias());
                    selectSql.getOrderBys().add(order);
                }
            }
        }
        return selectSql;
    }
    private EntityCondition parserParamCondition(Map condition) throws NoSuchFieldException, ClassNotFoundException {
        Object os = condition.get("condition");
        if(os==null){
            EntityCondition dom = parseOneCondition(condition);
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
    private EntityCondition parseOneCondition(Map map) throws NoSuchFieldException, ClassNotFoundException {
        Object relation=map.get("relation");
        if(relation!=null&&"is".equalsIgnoreCase(((String) relation).trim())){
            EntityConditionPre pre = new EntityConditionPre();
            pre.setRelation((String) relation);
            Object left = map.get("left");
            EntityColumn fieldAlias = getFieldAlias((String) left);
            if(fieldAlias!=null){
                pre.setTableAlias(fieldAlias.getTableAlias());
                pre.setColumn(fieldAlias.getOrignColumn());
            }
            pre.setValue(null);
            return pre;
        }else {
            EntityConditionDom dom = new EntityConditionDom();
            Object left = map.get("left");
            dom.setLeft(parseToCondition(left, false));
            dom.setRelation((String) relation);
            Object right = map.get("right");
            EntityCondition condition = parseToCondition(right, true);
            dom.setRight(condition);
            return dom;
        }
    }
    //解析left或者right元素
    private EntityCondition parseToCondition(Object os,boolean isRight) throws ClassNotFoundException, NoSuchFieldException {
        EntityCondition condition=null;
        if(os !=null && os instanceof Map) {
            condition = parserParamCondition((Map) os);
            return condition;
        }else if(!isRight&&os==null){
            throw new RuntimeException("列名不能为空,列："+os);
        } else{
            if(!isRight) {
                condition = new EntityConditionColumn();
                EntityColumn fieldAlias = getFieldAlias((String) os);
                if(fieldAlias!=null){
                    ((EntityConditionColumn) condition).setColumn(fieldAlias.getOrignColumn());
                    ((EntityConditionColumn) condition).setTableAlias(fieldAlias.getTableAlias());
                }
            } else {
                condition=new EntityConditionValue();
                ((EntityConditionValue) condition).setColumn(os);
            }

            return condition;
        }
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
        return updateSql;
    }

    public EntityDeleteSql parseToEntityDeleteSql() throws NoSuchFieldException, ClassNotFoundException {
        // TODO: implement
        EntityDeleteSql deleteSql=new EntityDeleteSql();
        deleteSql.setTableName(ReflectEntity.reflectTableName(classEntity));
        JSONObject condition= params.getCondition();
        deleteSql.setWheres(ReflectEntity.reflectPrimaryKeys(classEntity,condition));
        return deleteSql;
    }

    /**
     * 获取到列的别名
     * */
    private EntityColumn getFieldAlias(String column){
        for(EntityColumn conditionColumn:selectSql.getColumns()){
            String alias = StringUtils.isEmpty(conditionColumn.getAliasColumn())?conditionColumn.getAliasColumn():conditionColumn.getFieldName();
            if(alias.equalsIgnoreCase(column)){
                return conditionColumn;
            }
        }
        return null;
    }
}
