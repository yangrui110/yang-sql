package top.sanguohf.egg.util;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.SqlParse;
import top.sanguohf.egg.base.EntityColumn;
import top.sanguohf.egg.base.EntityInsert;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EntityParseUtil {
    public static List<EntityColumn> getColumns(Class param) {
        // TODO: implement
        return null;
    }

    /**
     * 转换为SqlParse
     * */
    public static String parseList(List orderBys){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<orderBys.size();i++){
            SqlParse sqlParse = (SqlParse) orderBys.get(i);
            builder.append(sqlParse.toSql());
            if(i!=orderBys.size()-1)
                builder.append(",");
        }
        return builder.toString();
    }

    public static String listInsertsToString(List<EntityInsert> inserts, String split){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<inserts.size();i++){
            builder.append(inserts.get(i).getColumn()).append(" = ");
            Object value=inserts.get(i).getValue();
            if(value instanceof String){
                builder.append("'");
            }
            builder.append(value);
            if(value instanceof String){
                builder.append("'");
            }
            if(i!=inserts.size()-1)
                builder.append(split);
        }
        return builder.toString();
    }

    /**
     * 更新条件转换成选择条件
     * */
    public static JSONObject saveConditionToSelectCondition(Map saveCondition){
        LinkedList list = new LinkedList<>();
        for(Object key:saveCondition.entrySet()){
            Map hashMap = new HashMap<>();
            hashMap.put("left",key);
            hashMap.put("right",saveCondition.get(key));
            hashMap.put("relation","=");
            list.add(hashMap);
        }
        JSONObject result = new JSONObject();
        result.put("condition",list);
        result.put("combine","and");
        return result;
    }
}
