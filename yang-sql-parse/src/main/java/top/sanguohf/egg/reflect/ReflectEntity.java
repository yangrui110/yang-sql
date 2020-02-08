package top.sanguohf.egg.reflect;

import top.sanguohf.egg.annotation.Ignore;
import top.sanguohf.egg.annotation.TableName;
import top.sanguohf.egg.base.EntityColumn;
import top.sanguohf.egg.util.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

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

    public static List<EntityColumn> reflectSelectColumns(Class entity){
        List<EntityColumn> columnList = new LinkedList<>();
        Field[] field = entity.getDeclaredFields();
        for(Field field1: field){
            top.sanguohf.egg.annotation.Field fs=field1.getAnnotation(top.sanguohf.egg.annotation.Field.class);
            Ignore ignore=field1.getAnnotation(Ignore.class);
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
