package top.sanguohf.egg.util;

import top.sanguohf.egg.SqlParse;
import top.sanguohf.egg.base.EntityColumn;

import java.util.List;

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
}
