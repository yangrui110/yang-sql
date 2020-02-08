package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntityConditionValue implements EntityCondition {

    private Object column;

    private String tableAlias;

    public String toSql() {
        StringBuilder builder=new StringBuilder();
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(".");
        if(column instanceof String)
            builder.append("'");
        builder.append(column);
        if(column instanceof String)
            builder.append("'");
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
