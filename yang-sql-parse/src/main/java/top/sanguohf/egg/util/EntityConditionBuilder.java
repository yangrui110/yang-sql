package top.sanguohf.egg.util;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.reflect.ReflectEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityConditionBuilder {
    private List<Object> doms;

    public static EntityConditionBuilder getInstance(){
        return new EntityConditionBuilder();
    }
    public EntityConditionBuilder() {
        this.doms = new ArrayList<>();
    }

    public EntityConditionBuilder like(String column, Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right","%"+value+"%");
        object.put("relation","like");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder likeLeft(String column, Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right","%"+value);
        object.put("relation","like");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder likeRight(String column, Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value+"%");
        object.put("relation","like");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder notLike(String column, Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right","%"+value+"%");
        object.put("relation","not like");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder isNull(String column){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",null);
        object.put("relation","is");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder isNotNull(String column){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",null);
        object.put("relation","is not");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder eq(String column,Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value);
        object.put("relation","=");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder lt(String column,Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value);
        object.put("relation","<");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder lte(String column,Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value);
        object.put("relation","<=");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder gt(String column,Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value);
        object.put("relation",">");
        doms.add(object);
        return this;
    }
    public EntityConditionBuilder gte(String column,Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value);
        object.put("relation",">=");
        doms.add(object);
        return this;
    }
    /**
     * @param column the attr of entity
     * @param value the value is instance of Object[] or List
     * */
    public EntityConditionBuilder in(String column,Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value);
        object.put("relation","in");
        doms.add(object);
        return this;
    }
    /**
     * @param column the attr of entity
     * @param value the value is instance of Object[] or List
     * */
    public EntityConditionBuilder notIn(String column,Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value);
        object.put("relation","not in");
        doms.add(object);
        return this;
    }
    public JSONObject combineAnd(){
        JSONObject result = new JSONObject();
        if(doms.size()==0){
            return null;
        }
        result.put("condition",doms);
        result.put("combine","and");
        return result;
    }
    public JSONObject combineOr(){
        JSONObject result = new JSONObject();
        if(doms.size()==0){
            return null;
        }
        result.put("condition",doms);
        result.put("combine","or");
        return result;
    }
    public static JSONObject combine(JSONObject left,JSONObject right,String relation){
        JSONObject result = new JSONObject();
        result.put("left",left);
        result.put("right",right);
        result.put("relation",relation);
        return result;
    }
    public static JSONObject combine(JSONObject left,JSONObject right){
        JSONObject result = new JSONObject();
        result.put("left",left);
        result.put("right",right);
        result.put("relation","and");
        return result;
    }

    /**
     * 转换java为Map查询条件
     * */
    public static <T> JSONObject buildClass(T data) throws IllegalAccessException {
        JSONObject map = new JSONObject();
        Class<?> aClass = data.getClass();
        List<Field> fields = ReflectEntity.getFields(aClass);
        for(Field field:fields){
            field.setAccessible(true);
            Object o = field.get(data);
            if(o!=null)
                map.put(field.getName(),o);
        }
        if(map.keySet().size()>0)
            return map;
        return null;
    }
}
