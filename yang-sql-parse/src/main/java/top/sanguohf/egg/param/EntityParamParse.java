package top.sanguohf.egg.param;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.annotation.MainTable;
import top.sanguohf.egg.annotation.ReferTable;
import top.sanguohf.egg.annotation.TableName;
import top.sanguohf.egg.annotation.ViewTable;
import top.sanguohf.egg.base.*;
import top.sanguohf.egg.ops.*;
import top.sanguohf.egg.reflect.ReflectEntity;
import top.sanguohf.egg.util.EntityParseUtil;
import top.sanguohf.egg.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
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

    private EntityJoinTable parseViewEntityTable(Class entity) throws ClassNotFoundException, NoSuchFieldException {
        EntitySelectSql selectSql = new EntitySelectSql();
        Field[] fields = entity.getDeclaredFields();
        boolean present = entity.isAnnotationPresent(ViewTable.class);
        if(present) {
            //设置表别名
            for (Field field : fields) {
                Class<?> forName = Class.forName(field.getGenericType().getTypeName());
                if (field.isAnnotationPresent(MainTable.class)) {
                    //解析出主表
                    MainTable fieldAnnotation = field.getAnnotation(MainTable.class);
                    if (forName.isAnnotationPresent(ViewTable.class)) {
                        EntityJoinTable entityJoinTable = parseViewEntityTable(forName);
                        selectSql.setTabelName(entityJoinTable);
                        selectSql.setTableAlias(fieldAnnotation.tableAlias());
                    } else {
                        EntitySimpleJoinTable joinTable = new EntitySimpleJoinTable();
                        joinTable.setTableAlias(fieldAnnotation.tableAlias());
                        if (forName.isAnnotationPresent(TableName.class))
                            joinTable.setTableName(forName.getAnnotation(TableName.class).value());
                        else joinTable.setTableName(StringUtils.camel2Underline(forName.getSimpleName()));
                        //设置表名
                        selectSql.setTabelName(joinTable);
                    }
                }
            }
        }else {
            String tableName= ReflectEntity.reflectTableName(entity);
            EntitySimpleJoinTable joinTable = new EntitySimpleJoinTable();
            joinTable.setTableName(tableName);
            selectSql.setTabelName(joinTable);
        }
        if(present) {
            List<EntityColumn> viewTableColumns = ReflectEntity.getViewTableColumns(entity);
            selectSql.getColumns().addAll(viewTableColumns);
        }else {
            List<EntityColumn> viewTableColumns = ReflectEntity.reflectSelectColumns(entity);
            selectSql.getColumns().addAll(viewTableColumns);
        }
        //获取到关联条件
        List<EntitySimpleJoin> relationJoins = ReflectEntity.getRelationJoins(entity);
        selectSql.setJoins(relationJoins);
        //设置条件
        EntityCondition entityCondition = ReflectEntity.collectDefaultCondition(entity);
        selectSql.setWheres(entityCondition);
        //设置排序
        List<EntityOrderBy> orderByList = ReflectEntity.collectDefaultOrderBy(entity);
        selectSql.setOrderBys(orderByList);
        return selectSql;
    }


    public EntitySelectSql parseToEntitySelectSql() throws ClassNotFoundException, NoSuchFieldException {
        //1.构造实体条件
        selectSql = (EntitySelectSql) parseViewEntityTable(classEntity);

        JSONObject condition= params.getCondition();
        EntityCondition entityCondition = ReflectEntity.collectDefaultCondition(classEntity);
        if(condition !=null&&condition.keySet().size()>0){
            Map one = EntityParseUtil.excludeNoExistColumn(condition, selectSql.getColumns());
            JSONObject selectCondition = EntityParseUtil.saveConditionToSelectCondition(one);
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
        selectSql.getOrderBys().addAll(orderBy);
        /*
        //2.确定查询的数据库表
        String tableName= ReflectEntity.reflectTableName(classEntity);
        EntitySimpleJoinTable joinTable = new EntitySimpleJoinTable();
        joinTable.setTableName(tableName);
        selectSql.setTabelName(joinTable);
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
        selectSql.setOrderBys(params.getOrderBy());*/
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
                for(EntityColumn conditionColumn:selectSql.getColumns()){
                    String alias = StringUtils.isEmpty(conditionColumn.getAliasColumn())?conditionColumn.getAliasColumn():conditionColumn.getFieldName();
                    if(alias.equals(os)){
                        ((EntityConditionColumn) condition).setColumn(conditionColumn.getOrignColumn());
                        ((EntityConditionColumn) condition).setTableAlias(conditionColumn.getTableAlias());
                    }
                }
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
