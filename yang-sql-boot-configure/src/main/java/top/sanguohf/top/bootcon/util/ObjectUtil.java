package top.sanguohf.top.bootcon.util;


import top.sanguohf.egg.reflect.ReflectEntity;

import java.lang.reflect.Field;
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
                    if (Date.class.getName().equals(declarName) && "java.sql.Date".equals(mapName)) {//转换日期
                        Date date = (Date) map.get(name);
                        java.sql.Date date1 = new java.sql.Date(date.getTime());
                        field.set(instance, date1);
                    } else if (Date.class.getName().equals(mapName) && "java.sql.Date".equals(declarName)) {
                        java.sql.Date date = (java.sql.Date) map.get(name);
                        Date date1 = new Date(date.getTime());
                        field.set(instance, date1);
                    } else if (declarName.equals(Short.class.getName())) {//转换short
                        if (Integer.class.getName().equals(mapName) ||
                                Long.class.getName().equals(mapName) ||
                                Short.class.getName().equals(mapName) ||
                                Float.class.getName().equals(mapName) ||
                                Double.class.getName().equals(mapName)) {
                            field.set(instance, Short.parseShort(""+ map.get(name)));
                        } else {
                            if (String.class.getName().equals(mapName))
                                field.set(instance, Short.parseShort((String) map.get(name)));
                        }
                    } else if (declarName.equals(Integer.class.getName())) {//转换integer
                        if (Short.class.getName().equals(mapName) ||
                                Long.class.getName().equals(mapName) ||
                                Float.class.getName().equals(mapName) ||
                                Double.class.getName().equals(mapName)) {
                            field.set(instance, Integer.parseInt(""+map.get(name)));
                        } else {
                            if (String.class.getName().equals(mapName))
                                field.set(instance, Integer.parseInt((String) map.get(name)));
                        }
                    } else if (declarName.equals(Float.class.getName())) {//转换float
                        if (Integer.class.getName().equals(mapName) ||
                                Long.class.getName().equals(mapName) ||
                                Short.class.getName().equals(mapName) ||
                                Double.class.getName().equals(mapName)) {
                            field.set(instance, Float.parseFloat(""+ map.get(name)));
                        } else {
                            if (String.class.getName().equals(mapName))
                                field.set(instance, Float.parseFloat((String) map.get(name)));
                        }
                    } else if (declarName.equals(Double.class.getName())) {//转换double
                        if (Integer.class.getName().equals(mapName) ||
                                Long.class.getName().equals(mapName) ||
                                Short.class.getName().equals(mapName) ||
                                Float.class.getName().equals(mapName)) {
                            field.set(instance, Double.parseDouble(""+ map.get(name)));
                        } else {
                            if (String.class.getName().equals(mapName))
                                field.set(instance, Double.parseDouble((String) map.get(name)));
                        }
                    }else if (declarName.equals(Long.class.getName())) {//转换long
                        if (Integer.class.getName().equals(mapName) ||
                                Short.class.getName().equals(mapName) ||
                                Float.class.getName().equals(mapName)||
                                Double.class.getName().equals(mapName)) {
                            field.set(instance, Long.parseLong(""+ map.get(name)));
                        } else {
                            if (String.class.getName().equals(mapName))
                                field.set(instance, Long.parseLong((String) map.get(name)));
                        }
                    }
                }
            }
        }
    }

}
