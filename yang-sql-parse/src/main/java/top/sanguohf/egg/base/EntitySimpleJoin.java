package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.SqlParse;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.ops.EntityJoinTable;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntitySimpleJoin implements SqlParse {
    private String joinConditions;
    private String relation;
    private String tableName;
    private String tableAlias;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(relation).append(" ");
        builder.append(tableName).append(" ");
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(" ");
        if(!StringUtils.isEmpty(joinConditions))
            builder.append(" on ");
        //后期这里会变成子查询的范畴
        builder.append(joinConditions);
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
