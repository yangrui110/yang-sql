package top.sanguohf.egg.util;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.SqlParse;
import top.sanguohf.egg.base.EntityColumn;
import top.sanguohf.egg.base.EntityInsert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

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

    /**
     * 给prepareStatement设值
     * */
    public static PreparedStatement setValueForStatement(List<EntityInsert> inserts,String sql,Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for(int i =0;i<inserts.size();i++){
            Object value=inserts.get(i).getValue();
            if(value instanceof Date){
                value = new java.sql.Date(((Date) value).getTime());
            }
            preparedStatement.setObject(i+1,value);
        }
        return preparedStatement;
    }

    public static String listInsertsToString(List<EntityInsert> inserts, String split,boolean isPrepare){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<inserts.size();i++){
            builder.append(inserts.get(i).getColumn()).append(" = ");
            Object value=inserts.get(i).getValue();
            if(!isPrepare) {
                if (value instanceof String) {
                    builder.append("'");
                }
                builder.append(value);
                if (value instanceof String) {
                    builder.append("'");
                }
            }else builder.append("?");
            if(i!=inserts.size()-1)
                builder.append(split);
        }
        return builder.toString();
    }

    /**
     * 更新条件转换成选择条件
     * */
    public static JSONObject saveConditionToSelectCondition(Map saveCondition){
        Object con = saveCondition.get("condition");
        Object left = saveCondition.get("left");
        Object right = saveCondition.get("right");
        if(con==null&&(left==null&&right==null)) {
            LinkedList list = new LinkedList<>();
            for (Object key : saveCondition.keySet()) {
                Map hashMap = new HashMap<>();
                hashMap.put("left", key);
                hashMap.put("right", saveCondition.get(key));
                hashMap.put("relation", "=");
                list.add(hashMap);
            }
            JSONObject result = new JSONObject();
            result.put("condition", list);
            result.put("combine", "and");
            return result;
        }
        return null;
    }
    public static Map excludeNoExistColumn(Map condition,List<EntityColumn> list){
        //排除掉不存在的列
        Map one = new HashMap();
        for(Object key:condition.keySet()){
            for(EntityColumn column:list){
                String alias = StringUtils.isEmpty(column.getAliasColumn())?column.getAliasColumn():column.getFieldName();
                if(alias.equals(key)){
                    one.put(key,condition.get(key));
                }
            }
        }
        return one;
    }

}
