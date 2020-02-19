package top.sanguohf.egg.reflect;

import top.sanguohf.egg.annotation.*;
import top.sanguohf.egg.annotation.Condition;
import top.sanguohf.egg.base.*;
import top.sanguohf.egg.constant.ValueType;
import top.sanguohf.egg.ops.EntitySelectSql;
import top.sanguohf.egg.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 实体反射解析
 * */
public class ReflectEntity {

    public static String reflectTableName(Class entity){
        String tableName = StringUtils.camel2Underline(entity.getSimpleName()) ;
        TableName annotation = (TableName) entity.getAnnotation(TableName.class);
        if(annotation!=null){
            tableName = annotation.value();
        }
        return tableName;
    }
    /**
     * 解析出主键,首先查看是否存在@Id注解的列，如果存在，添加到返回值中
     * 不存在@Id注解，则会自动寻找名为id的属性作为注解
     * */
    public static List<EntityInsert> reflectPrimaryKeys(Class entity, Map<String,Object> columns) throws NoSuchFieldException, ClassNotFoundException {
        LinkedList<EntityInsert> ids = new LinkedList<>();
        for(String key:columns.keySet()){
            Field field = entity.getDeclaredField(key);
            if(field.isAnnotationPresent(Id.class)){
                EntityInsert insert = new EntityInsert();
                String tableField = getTableField(entity, key);
                insert.setColumn(tableField);
                insert.setValue(columns.get(key));
                ids.add(insert);
            }
        }
        if(ids.size()>0)
            return ids;
        else {
            //查找当前的所有属性中是否存在名为id
            try {
                String tableField = getTableField(entity, "id");
                EntityInsert insert = new EntityInsert();
                insert.setColumn(tableField);
                insert.setValue(columns.get("id"));
                ids.add(insert);
                return ids;
            }catch (NoSuchFieldException ex){
                throw new RuntimeException("主键不存在或者未设置");
            }

        }
    }

    //获取到所有待查询的列
    public static List<EntityColumn> reflectSelectColumns(Class entity){
        List<EntityColumn> columnList = new LinkedList<>();
        Field[] field = entity.getDeclaredFields();
        for(Field field1: field){
            top.sanguohf.egg.annotation.Field fs=field1.getAnnotation(top.sanguohf.egg.annotation.Field.class);
            IgnoreSelectReback ignore=field1.getAnnotation(IgnoreSelectReback.class);
            if(ignore==null||!ignore.value()) {
                EntityColumn column=new EntityColumn();
                if (fs != null) {
                    if(!StringUtils.isEmpty(fs.value()))
                        column.setOrignColumn(fs.value());
                    else column.setOrignColumn(StringUtils.camel2Underline(field1.getName()));
                    if(!StringUtils.isEmpty(fs.alias()))
                        column.setAliasColumn(fs.alias());
                    else column.setAliasColumn(field1.getName());
                }else{
                    column.setOrignColumn(StringUtils.camel2Underline(field1.getName()));
                    column.setAliasColumn(field1.getName());
                }
                column.setFieldName(field1.getName());
                columnList.add(column);
            }
        }
        return columnList;
    }

    public static String getTableField(Class entity,String fieldName) throws NoSuchFieldException, ClassNotFoundException {
        if(!entity.isAnnotationPresent(ViewTable.class)) {
            Field field = entity.getDeclaredField(fieldName);
            top.sanguohf.egg.annotation.Field fieldClass = field.getAnnotation(top.sanguohf.egg.annotation.Field.class);
            if (fieldClass != null) {
                return StringUtils.isEmpty(fieldClass.value())?fieldName:fieldClass.value();
            } else return StringUtils.camel2Underline(fieldName);
        }else{
            List<EntityColumn> viewTableColumns = getViewTableColumns(entity);
            for(EntityColumn entityColumn:viewTableColumns){
                if(entityColumn.getAliasColumn().equals(fieldName)){
                    return entityColumn.getAliasColumn();
                }
            }
            return null;
        }
    }

    /**
     * 设置表名
     * */
    public static void setTableNameAndAlias(Class entity, EntitySelectSql selectSql){

    }
    /**
     * 收集到实体类上的列默认值
     * */
    public static EntityCondition collectDefaultCondition(Class entity) throws NoSuchFieldException, ClassNotFoundException {
        Field[] fields = entity.getDeclaredFields();
        EntityListCondition condition = new EntityListCondition();
        List<EntityConditionDom> conditions = new LinkedList<>();
        for(Field field: fields){
            if(field.isAnnotationPresent(Condition.class)){
                Condition annotation = field.getAnnotation(Condition.class);
                conditions.add(getOneCondition(annotation,entity,field));
            }else if(field.isAnnotationPresent(Conditions.class)){
                Conditions annotation = field.getAnnotation(Conditions.class);
                for (Condition condition1: annotation.value()){
                    conditions.add(getOneCondition(condition1,entity,field));
                }
            }
        }
        if(conditions.size()>0){
            condition.setCondition(conditions);
            condition.setCombine("and");
            return condition;
        }
        return null;
    }

    //收集到一个condition
    private static EntityConditionDom getOneCondition(Condition annotation,Class entity,Field field) throws NoSuchFieldException, ClassNotFoundException {
        EntityConditionDom conditionDom = new EntityConditionDom();
        EntityConditionColumn column = new EntityConditionColumn();
        String tableField = "";
        boolean present = entity.isAnnotationPresent(ViewTable.class);
        if(present){
            Class<?> forName = Class.forName(field.getGenericType().getTypeName());
            if(field.isAnnotationPresent(MainTable.class)){
                MainTable mainTable = field.getAnnotation(MainTable.class);
                column.setTableAlias(mainTable.tableAlias());
            }else if(field.isAnnotationPresent(ReferTable.class)) {
                ReferTable referTable = field.getAnnotation(ReferTable.class);
                column.setTableAlias(referTable.tableAlias());
            }
            tableField = getTableField(forName, annotation.column());
        }else {
            tableField = getTableField(entity, field.getName());
        }
        column.setColumn(tableField);
        conditionDom.setLeft(column);
        //
        if(StringUtils.isEmpty(annotation.relation()))
            conditionDom.setRelation("=");
        else conditionDom.setRelation(annotation.relation());
        EntityConditionValue columnRight = new EntityConditionValue();
        columnRight.setColumn(parseValue(annotation.value(),annotation.type()));
        conditionDom.setRight(columnRight);
        return conditionDom;
    }
    /**
     * 收集到待排序的字段
     * */
    public static List<EntityOrderBy> collectDefaultOrderBy(Class entity) throws NoSuchFieldException, ClassNotFoundException {
        Field[] fields = entity.getDeclaredFields();
        LinkedList<EntityOrderBy> list = new LinkedList<>();
        for(Field field:fields){
            if(field.isAnnotationPresent(OrderBy.class)){
                OrderBy annotation = field.getAnnotation(OrderBy.class);
                list.add(getOneOrderBy(annotation,entity,field));
            }else if(field.isAnnotationPresent(OrderBys.class)){
                OrderBys orderBys = field.getAnnotation(OrderBys.class);
                for(OrderBy orderBy:orderBys.value()){
                    list.add(getOneOrderBy(orderBy,entity,field));
                }
            }
        }
        return list;
    }

    //获取到单个ordeyBy数据
    private static EntityOrderBy getOneOrderBy(OrderBy orderBy,Class entity,Field field) throws NoSuchFieldException, ClassNotFoundException {

        EntityOrderBy entityOrderBy = new EntityOrderBy();
        String tableField = "";
        boolean present = entity.isAnnotationPresent(ViewTable.class);
        if(present){
            Class<?> forName = Class.forName(field.getGenericType().getTypeName());
            if(field.isAnnotationPresent(MainTable.class)){
                MainTable mainTable = field.getAnnotation(MainTable.class);
                entityOrderBy.setTableAlias(mainTable.tableAlias());
            }else if(field.isAnnotationPresent(ReferTable.class)) {
                ReferTable referTable = field.getAnnotation(ReferTable.class);
                entityOrderBy.setTableAlias(referTable.tableAlias());
            }
            tableField = getTableField(forName, orderBy.column());
        }else {
            tableField = getTableField(entity, field.getName());
        }
        entityOrderBy.setColumn(tableField);
        entityOrderBy.setDirect(orderBy.direct());
        return entityOrderBy;
    }

    //获取到待查询的列
    public static List<EntityColumn> collectColumns(List<EntityColumn> entityColumns,String[] includeCloumns,String[] excludeColumns,String tableAlias) throws NoSuchFieldException {
        for (EntityColumn c :entityColumns) {
            c.setTableAlias(tableAlias);
        }
        if((includeCloumns.length==0)&&excludeColumns.length==0){
            return entityColumns;
        }else if (includeCloumns.length>0){
            LinkedList list = new LinkedList<>();
            for(String column:includeCloumns){
                for(EntityColumn entityColumn:entityColumns){
                    if(column.equals(entityColumn.getFieldName())){
                        list.add(entityColumn);
                    }
                }
            }
            return list;
        }else if(excludeColumns.length>0){
            LinkedList<EntityColumn> resultList = new LinkedList<>();
            for(EntityColumn column:entityColumns){
                boolean exist = false;
                for(String one:excludeColumns){
                    if(column.getFieldName().equals(one)){
                        exist=true;
                    }
                }
                if(!exist)
                    resultList.add(column);
            }
            return resultList;
        }else return null;
    }
    //获取到视图表需要查询出的列
    public static List<EntityColumn> getViewTableColumns(Class viewEntity) throws ClassNotFoundException, NoSuchFieldException {
        Field[] fields = viewEntity.getDeclaredFields();
        LinkedList<EntityColumn> totalResult = new LinkedList<>();
        for(Field field:fields){
            if(field.isAnnotationPresent(ReferTable.class)){
                ReferTable annotation = field.getAnnotation(ReferTable.class);
                String[] includeColumns = annotation.includeColumns();
                String[] excludeColumns = annotation.excludeColumns();
                String alias = annotation.tableAlias();
                Class<?> forName = Class.forName(field.getGenericType().getTypeName());
                List<EntityColumn> entityColumns = collectColumns(reflectSelectColumns(forName), includeColumns, excludeColumns, alias);
                totalResult.addAll(entityColumns);
            }else {
                Class<?> forName = Class.forName(field.getGenericType().getTypeName());
                List<EntityColumn> entityColumns = new LinkedList<>();
                if(forName.isAnnotationPresent(ViewTable.class)) {
                    entityColumns = getViewTableColumns(forName);
                    for (EntityColumn column : entityColumns) {
                        column.setOrignColumn(column.getAliasColumn());
                    }
                }
                else {
                    entityColumns=reflectSelectColumns(forName);
                }
                MainTable annotation = field.getAnnotation(MainTable.class);
                String[] includeColumns = annotation.includeColumns();
                String[] excludeColumns = annotation.excludeColumns();
                String alias = annotation.tableAlias();
                List<EntityColumn> entityColumns1 = collectColumns(entityColumns, includeColumns, excludeColumns, alias);
                totalResult.addAll(entityColumns1);
            }
        }
        return totalResult;
    }
    //获取到关联条件
    public static List<EntitySimpleJoin> getRelationJoins(Class viewEntity) throws ClassNotFoundException, NoSuchFieldException {
        if(viewEntity.isAnnotationPresent(ViewTable.class)) {
            Field[] fields = viewEntity.getDeclaredFields();
            //1.收集到别名和类名的对应
            Map<String, Class> map = new HashMap<>();
            LinkedList<String> list = new LinkedList<>();
            for (Field field : fields) {
                if (field.isAnnotationPresent(MainTable.class)) {
                    MainTable annotation = field.getAnnotation(MainTable.class);
                    map.put(annotation.tableAlias(), Class.forName(field.getGenericType().getTypeName()));
                    list.add(annotation.tableAlias());
                } else {
                    ReferTable annotation = field.getAnnotation(ReferTable.class);
                    map.put(annotation.tableAlias(), Class.forName(field.getGenericType().getTypeName()));
                    list.add(annotation.tableAlias());
                }
            }
            //2.收集到每个列名，并且转化
            LinkedList<EntitySimpleJoin> simpleJoins = new LinkedList<>();

            for (Field field : fields) {
                if (field.isAnnotationPresent(ReferTable.class)) {
                    ReferTable annotation = field.getAnnotation(ReferTable.class);
                    String tableAlias = annotation.tableAlias();
                    String tableName = reflectTableName(map.get(tableAlias));
                    EntitySimpleJoin simpleJoin = new EntitySimpleJoin();
                    simpleJoin.setRelation(annotation.relation() == null ? "left join" : annotation.relation());
                    simpleJoin.setTableName(tableName);
                    simpleJoin.setTableAlias(tableAlias);
                    String condition = annotation.condition();
                    for (String alias : list) {
                        condition = StringUtils.patternReplace(map, alias, condition);
                    }
                    simpleJoin.setJoinConditions(condition);
                    simpleJoins.add(simpleJoin);
                }
            }
            return simpleJoins;
        }
        return null;
    }

    private static Object parseValue(String value,String type){
        if(ValueType.INTEGER.equals(type)){
            return Integer.parseInt(value);
        }
        return value;
    }
}
