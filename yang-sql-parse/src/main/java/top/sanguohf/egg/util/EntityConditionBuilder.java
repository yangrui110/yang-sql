package top.sanguohf.egg.util;

import com.alibaba.fastjson.JSONObject;

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
    public EntityConditionBuilder in(String column,Object value){
        JSONObject object = new JSONObject();
        object.put("left",column);
        object.put("right",value);
        object.put("relation","in");
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
    public JSONObject combine(JSONObject left,JSONObject right,String relation){
        JSONObject result = new JSONObject();
        result.put("left",left);
        result.put("right",right);
        result.put("relation",relation);
        return result;
    }
}
