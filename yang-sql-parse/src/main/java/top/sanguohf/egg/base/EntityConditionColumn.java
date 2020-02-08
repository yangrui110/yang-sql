package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntityConditionColumn implements EntityCondition {

    private String column;

    private String tableAlias;

    public String toSql() {
        StringBuilder builder=new StringBuilder();
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(".");
        builder.append(column);
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
