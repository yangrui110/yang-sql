package top.sanguohf.top.bootcon.util;

import org.springframework.beans.BeanUtils;

import java.util.LinkedList;
import java.util.List;

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
    public static <T> List<T> parseList(List list,Class<T> tClass){
        LinkedList<T> linkedList = new LinkedList<>();
        for(Object os :list){
            T t = create(tClass);
            BeanUtils.copyProperties(os,t);
            linkedList.add(t);
        }
        return linkedList;
    }
}
