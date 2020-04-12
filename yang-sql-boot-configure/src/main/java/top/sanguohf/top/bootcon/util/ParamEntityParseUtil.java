package top.sanguohf.top.bootcon.util;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.egg.reflect.ReflectEntity;
import top.sanguohf.egg.reflect.SubTeacher;
import top.sanguohf.egg.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ParamEntityParseUtil {
    //把实体转换为参数
    public static <T> EntityParams parseToParam(T data) throws IllegalAccessException {
        EntityParams entityParams = new EntityParams();
        String name = data.getClass().getSimpleName();
        List<Field> fields = ReflectEntity.getFields(data.getClass());
        JSONObject map = new JSONObject();
        for (Field field:fields){
            field.setAccessible(true);
            Object o = field.get(data);
            if(o!=null&&!"".equals(o))
                map.put(field.getName(),o);
        }
        entityParams.setTableClassName(name);
        entityParams.setCondition(map);
        return entityParams;
    }

    public static void main(String[] args) throws IllegalAccessException {
        SubTeacher teacher = new SubTeacher();
        teacher.setTitle("mmmmmmm");
        teacher.setTeacherId("99990");
        teacher.setTeacherName("jjjjjj");
        EntityParams entityParams = parseToParam(teacher);
        System.out.println(entityParams);
    }

}
