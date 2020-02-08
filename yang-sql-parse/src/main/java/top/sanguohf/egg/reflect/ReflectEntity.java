package top.sanguohf.egg.reflect;

import top.sanguohf.egg.annotation.Id;
import top.sanguohf.egg.annotation.IgnoreSelectReback;
import top.sanguohf.egg.annotation.TableName;
import top.sanguohf.egg.base.EntityColumn;
import top.sanguohf.egg.base.EntityInsert;
import top.sanguohf.egg.util.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 实体反射解析
 * */
public class ReflectEntity {

    public static String reflectTableName(Class entity){
        String tableName = StringUtils.camel2Underline(entity.getName()) ;
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
    public static List<EntityInsert> reflectPrimaryKeys(Class entity, Map<String,Object> columns) throws NoSuchFieldException {
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

    public static List<EntityColumn> reflectSelectColumns(Class entity){
        List<EntityColumn> columnList = new LinkedList<>();
        Field[] field = entity.getDeclaredFields();
        for(Field field1: field){
            top.sanguohf.egg.annotation.Field fs=field1.getAnnotation(top.sanguohf.egg.annotation.Field.class);
            IgnoreSelectReback ignore=field1.getAnnotation(IgnoreSelectReback.class);
            if(ignore==null||!ignore.value()) {
                EntityColumn column=new EntityColumn();
                if (fs != null) {
                    column.setOrignColumn(fs.value());
                    column.setAliasColumn(field1.getName());
                }else{
                    column.setOrignColumn(StringUtils.camel2Underline(field1.getName()));
                    column.setAliasColumn(field1.getName());
                }
                columnList.add(column);
            }
        }
        return columnList;
    }

    public static String getTableField(Class entity,String fieldName) throws NoSuchFieldException {
        Field field=entity.getDeclaredField(fieldName);
        top.sanguohf.egg.annotation.Field fieldClass=field.getAnnotation(top.sanguohf.egg.annotation.Field.class);
        if(fieldClass!=null){
            return fieldClass.value();
        }else return StringUtils.camel2Underline(fieldName);
    }

}
