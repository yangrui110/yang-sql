package top.sanguohf.egg.base;

import lombok.Data;
import top.sanguohf.egg.SqlParse;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.ops.EntityJoinTable;
import top.sanguohf.egg.util.StringUtils;

@Data
public class EntitySimpleJoin implements SqlParse {
    private JoinCondition joinConditions;
    private String relation;
    private EntityJoinTable tableName;
    private String tableAlias;

    public String toSql() {
        StringBuilder builder = new StringBuilder();
        builder.append(relation).append(" ");
        if(!StringUtils.isEmpty(tableAlias))
            builder.append(tableAlias).append(".");
        builder.append(tableName).append(" ");
        builder.append(joinConditions.toSql());
        return builder.toString();
    }

    public String toSql(DbType dbType) {
        return toSql();
    }
}
