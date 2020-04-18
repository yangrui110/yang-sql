package top.sanguohf.top.bootcon.util;


import top.sanguohf.egg.reflect.ReflectEntity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ObjectUtil {
    public static <T> T create(Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return t;
    }
    public static <T> List<T> parseList(List list,Class<T> tClass) throws IllegalAccessException, InstantiationException {
        LinkedList<T> linkedList = new LinkedList<>();
        for(Object os :list){
            T t = create(tClass);
            copyMapToBean((Map) os,t);
            linkedList.add(t);
        }
        return linkedList;
    }
    public static void copyMapToBean(Map map,Object instance) throws IllegalAccessException, InstantiationException {
        if(instance instanceof Map){
            instance = map;
            return;
        }
        Class<?> aClass = instance.getClass();
        List<Field> fields = ReflectEntity.getFields(aClass);
        for (Field field:fields){
            field.setAccessible(true);
            String name = field.getName();
            Object o = map.get(name);
            String declarName = field.getGenericType().getTypeName();
            if(o!=null) {
                String mapName = o.getClass().getName();
                if (mapName.equals(declarName)) {
                    field.set(instance, o);
                } else {
                    if (o instanceof Date) {//转换日期
                        long time = ((Date) o).getTime();
                        if(field.getType().isInstance(new java.sql.Date(time))){
                            java.sql.Date date1 = new java.sql.Date(time);
                            field.set(instance, date1);
                        }else if(field.getType().isInstance(new java.sql.Timestamp(time))){
                            java.sql.Timestamp date1 = new java.sql.Timestamp(time);
                            field.set(instance, date1);
                        }else if(field.getType().isInstance(new java.sql.Time(time))){
                            java.sql.Time date1 = new java.sql.Time(time);
                            field.set(instance, date1);
                        }
                    } else if(o instanceof Number){
                        String s = ""+o;
                        if(field.getType().isInstance(new Short((short)0))){
                            field.set(instance,Short.parseShort(s));
                        }else if(field.getType().isInstance(new Integer(0))){
                            field.set(instance,Integer.parseInt(s));
                        }else if(field.getType().isInstance(new Float(0))){
                            field.set(instance,Float.parseFloat(s));
                        }else if(field.getType().isInstance(new Double(0))){
                            field.set(instance,Double.parseDouble(s));
                        }else if(field.getType().isInstance(new Long(0))){
                            field.set(instance,Long.parseLong(s));
                        }else if(field.getType().isInstance(new BigDecimal(0))){
                            field.set(instance,new BigDecimal(s));
                        }else if(field.getType().isInstance(new String())){
                            field.set(instance,s);
                        }
                    }
                }
            }
        }
    }
}
